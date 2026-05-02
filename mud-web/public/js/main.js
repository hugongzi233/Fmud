import { loadJSON, saveJSON } from './store.js';
import {
  createAnsiState,
  renderMudText,
  parseLoginServers,
  parseActionItems,
  parseExitItems,
  parseTargetItems,
  parseStatusBars,
  parseDialog,
  parseQuickCmds,
  escapeHtml
} from './protocol.js';

function stripAnsiLike(input) {
  return String(input || '')
    .replace(/\u001b\[[0-9;?]*[A-Za-z]/g, '')
    // translate $br# placeholders into actual newlines
    .replace(/\$br#/g, '\n')
    // collapse repeated spaces/tabs but preserve newlines
    .replace(/[ \t]+/g, ' ')
    .replace(/\n{2,}/g, '\n')
    .trim();
}

function extractGuiTitle(payload) {
  const text = String(payload || '');
  const quoted = text.match(/「([^」]+)」/);
  if (quoted && quoted[1]) return quoted[1].trim();
  // 先尝试移除带ESC的ANSI代码，再尝试移除不带ESC的ANSI代码
  let firstLine = text.replace(/\u001b\[[0-9;?]*[A-Za-z]/g, '').replace(/\[[0-9;?]*m/g, '').split('$br#')[0].trim();
  if (!firstLine) return 'GUI';
  const short = firstLine.replace(/\(.+\)$/, '').trim();
  return short || firstLine;
}

const mudAppOptions = {
  data() {
    const profile = loadJSON('mud.profile', { id: '', pass: '', email: '', phone: '', name: '', sex: '男性' });
    const settings = loadJSON('mud.settings', { encoding: 'utf8', mode: 'dark', cmdss: true, chatHeight: 5, rememberAccount: true });
    return {
      screen: 'home',
      profile,
      settings,
      quickSlots: loadJSON('mud.quickSlots', [
        { id: 'q1', label: '观察', cmd: 'look' },
        { id: 'q2', label: '属性', cmd: 'score' },
        { id: 'q3', label: '物品', cmd: 'i' },
        { id: 'q4', label: '技能', cmd: 'skills' }
      ]),
      servers: loadJSON('mud.servers', []),
      recentServer: loadJSON('mud.recentServer', null),
      showRegister: false,
      showSettings: false,
      showUserCenter: false,
      showServerEditor: false,
      showMap: false,
      showMoreText: false,
      showWeb: false,
      showGui: false,
      guiTitle: '',
      guiTitleHtml: '',
      guiColumns: 3,
      guiHtml: '',
      guiActions: [],
      guiActions1: [],
      guiActions2: [],
      guiTab: 'main',
      // Android-style UI states
      chatRoomVisible: false,
      chatTab: 'all',
      allChatHtml: '',
      systemChatHtml: '',
      quickCmds: [],  // 021消息的快捷菜单（飞行、门派等）
      customCmds: [],  // 006消息的自定义命令（常用、技能等）
      showCustomCmds: false,
      locationName: '短歌行',
      topInfo: '',
      connected: false,
      activeServer: null,
      activeServerLabel: '未选择分区',
      recentServerLabel: '暂无记录',
      roomName: '等待登录',
      sceneTitle: '游戏',
      storyVisible: true,
      fightVisible: false,
      storyHtml: '<span class="mini-note">等待服务器消息。</span>',
      mapHtml: '',
      moreHtml: '',
      webUrl: 'about:blank',
      topMessage: '',
      floatingTexts: [],
      commandInput: '',
      chatInput: '',
      chatChannel: '世界',
      chatInputText: '',  // 聊天输入框文本
      commandInputText: '',  // 命令输入框文本
      showEmoji: false,
      emojiList: ['😀', '😄', '😎', '🔥', '✨', '🎯', '👍', '👋'],
      feedTab: 'main',
      feed: { main: [], chat: [], fight: [], system: [] },
      actionBlocks: [],
      targets: [],
      exits: [
        { key: 'northwest', label: '西北', cmd: 'northwest', visible: false },
        { key: 'northeast', label: '东北', cmd: 'northeast', visible: false },
        { key: 'southwest', label: '西南', cmd: 'southwest', visible: false },
        { key: 'southeast', label: '东南', cmd: 'southeast', visible: false },
        { key: 'north', label: '北', cmd: 'north', visible: false },
        { key: 'south', label: '南', cmd: 'south', visible: false },
        { key: 'west', label: '西', cmd: 'west', visible: false },
        { key: 'east', label: '东', cmd: 'east', visible: false }
      ],
      statusBars: [],
      dialog: {
        visible: false,
        title: '对话框',
        blocks: [],
        items: [],
        exp: '',
        money: '',
        numberNeeded: false,
        input: '',
        okCommands: [],
        cancelCommand: ''
      },
      popup: {
        visible: false,
        title: '菜单',
        items: [],
        columns: 4
      },
      serverDraft: { name: '', host: '', port: '23', encoding: 'utf8', status: '' },
      registerForm: { id: '', pass: '', pass2: '', phone: '', email: '', name: '', sex: '男性' },
      ws: null,
      wsEncoding: 'utf8',
      gameState: {
        serverKey: '',
        loginQueued: false,
        versionChecked: false,
        checkSent: false,
        ansi: createAnsiState(settings.mode || 'dark')
      },
      fallbackItemImage: 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI4MCIgaGVpZ2h0PSI4MCIgdmlld0JveD0iMCAwIDgwIDgwIj48cmVjdCB3aWR0aD0iODAiIGhlaWdodD0iODAiIGZpbGw9IiMwYzEzMjIiLz48dGV4dCB4PSI0MCIgeT0iNDYiIGZpbGw9IiM4ZmEwYjYiIGZvbnQtc2l6ZT0iMTYiIHRleHQtYW5jaG9yPSJtaWRkbGUiPk0tPjwvdGV4dD48L3N2Zz4='
    };
  },
  computed: {
    activeFeedHtml() {
      const rows = this.feed[this.feedTab] || [];
      if (!rows.length) return '<div class="line">暂无内容。</div>';
      return rows.map((row) => `<div class="line">${row.html}</div>`).join('');
    },
    chatFeedHtml() {
      const rows = this.feed.chat;
      if (!rows.length) return '<div class="line">暂无聊天内容。</div>';
      return rows.map((row) => `<div class="line">${row.html}</div>`).join('');
    }
  },
  mounted() {
    this.refreshLabels();
    this.requestVersion();
    this.ensureSavedServerDraft();
  },
  methods: {
    refreshLabels() {
      this.recentServerLabel = this.recentServer ? `${this.recentServer.name || this.recentServer.host}:${this.recentServer.port}` : '暂无记录';
      this.activeServerLabel = this.activeServer ? `${this.activeServer.name || this.activeServer.host}:${this.activeServer.port}` : '未选择分区';
      this.roomName = this.connected ? (this.activeServer?.name || '游戏中') : '等待登录';
    },
    pushToast(message) {
      console.log('[Toast]', message);
    },
    pushSystem(message) {
      this.appendSystem(message);
    },
    appendSystem(text) {
      if (!stripAnsiLike(text)) return;
      this.appendRow('system', renderMudText(text, this.gameState.ansi, { mode: this.settings.mode }));
    },
    closeGui() {
      this.showGui = false;
      this.guiTitle = '';
      this.guiHtml = '';
      this.guiActions1 = [];
      this.guiActions2 = [];
      this.guiTab = 'main';
    },
    appendChat(text) {
      if (!stripAnsiLike(text)) return;
      const html = renderMudText(text, this.gameState.ansi, { mode: this.settings.mode });
      this.appendRow('chat', html);
      this.allChatHtml += `<div class="line">${html}</div>`;
    },
    appendFight(text) {
      if (!stripAnsiLike(text)) return;
      this.appendRow('fight', renderMudText(text, this.gameState.ansi, { mode: this.settings.mode }));
    },
    ensureSavedServerDraft() {
      const draft = loadJSON('mud.manualServer', null);
      if (draft) this.serverDraft = draft;
    },
    persist() {
      saveJSON('mud.profile', this.profile);
      saveJSON('mud.settings', this.settings);
      saveJSON('mud.quickSlots', this.quickSlots);
      saveJSON('mud.servers', this.servers);
      saveJSON('mud.recentServer', this.recentServer);
      saveJSON('mud.manualServer', this.serverDraft);
      this.refreshLabels();
    },
    requestVersion() {
      fetch('/api/version').then((r) => r.text()).then((text) => {
        if (text.trim()) this.pushToast('版本接口已连通');
      }).catch(() => {
        this.pushToast('版本接口暂时不可用');
      });
    },
    openUserCenter() { this.showUserCenter = true; },
    openSettings() { this.showSettings = true; },
    saveProfile() { this.persist(); this.pushToast('资料已保存'); },
    saveSettings() { this.persist(); this.showSettings = false; this.pushToast('设置已保存'); },
    loginAndLoadServers() {
      if (!this.profile.id || !this.profile.pass) {
        this.pushToast('请先输入账号和密码');
        return;
      }
      this.pushToast('正在登录...');
      fetch(`/api/login?id=${encodeURIComponent(this.profile.id)}&pass=${encodeURIComponent(this.profile.pass)}&page=1`)
        .then((r) => {
          if (!r.ok) {
            throw new Error(`HTTP ${r.status}: ${r.statusText}`);
          }
          return r.text();
        })
        .then((text) => {
          const result = parseLoginServers(text);
          if (result.error) {
            this.pushToast(result.error);
            return;
          }
          this.servers = result.servers;
          if (result.email) this.profile.email = result.email;
          if (this.servers.length) this.recentServer = this.servers[0];
          if (!this.settings.rememberAccount) {
            this.profile.id = '';
            this.profile.pass = '';
          }
          this.persist();
          this.pushToast(`成功获取 ${this.servers.length} 个服务器分区`);
        })
        .catch((error) => {
          this.pushToast(`登录失败: ${error.message || '未知错误'}`);
        });
    },
    addManualServer() {
      if (!this.serverDraft.host) return;
      const server = {
        id: `${Date.now()}`,
        name: this.serverDraft.name || this.serverDraft.host,
        host: this.serverDraft.host,
        port: this.serverDraft.port || '23',
        encoding: this.serverDraft.encoding || 'utf8',
        status: this.serverDraft.status || '',
        online: '',
        key: ''
      };
      this.servers.unshift(server);
      this.showServerEditor = false;
      this.persist();
      this.pushToast('分区已保存');
    },
    connectServer(server) {
      this.activeServer = server;
      this.recentServer = server;
      this.persist();
      this.screen = 'game';
      this.connected = false;
      this.feed = { main: [], chat: [], fight: [], system: [] };
      this.actionBlocks = [];
      this.targets = [];
      this.statusBars = [];
      this.exits.forEach((exit) => { exit.visible = false; });
      this.storyHtml = '<span class="mini-note">无</span>';
      this.showGui = false;
      this.guiTitle = '';
      this.guiHtml = '';
      this.guiActions = [];
      this.gameState = {
        serverKey: '',
        loginQueued: false,
        versionChecked: false,
        checkSent: false,
        ansi: createAnsiState(this.settings.mode || 'dark')
      };
      this.openSocket(server.host, server.port, server.encoding || this.settings.encoding || 'utf8');
    },
    openSocket(host, port, encoding) {
      if (this.ws) {
        try { this.ws.close(); } catch {}
      }
      const socketUrl = `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/`;
      this.wsEncoding = encoding || 'utf8';
      this.ws = new WebSocket(socketUrl);
      this.ws.addEventListener('open', () => {
        this.ws.send(JSON.stringify({ action: 'connect', host, port, encoding: this.wsEncoding }));
      });
      this.ws.addEventListener('message', (event) => this.handleSocketMessage(event.data));
      this.ws.addEventListener('close', () => {
        this.connected = false;
        this.pushSystem('连接已关闭');
      });
      this.ws.addEventListener('error', () => this.pushToast('连接错误'));
    },
    handleSocketMessage(raw) {
      let message;
      try { message = JSON.parse(raw); } catch { return; }
      if (message.type === 'status') {
        if (message.status === 'connected') {
          this.connected = true;
          this.pushSystem(`已连接 ${message.host}:${message.port}`);
        } else if (message.status === 'closed') {
          this.connected = false;
        } else if (message.status === 'error') {
          this.connected = false;
          this.pushToast(message.message || '连接错误');
        }
        return;
      }
      if (message.type === 'data') {
        this.processIncomingText(String(message.text || ''));
      }
    },
    processIncomingText(text) {
      if (!text) return;
      // if previous incoming indicated a marker-only control (e.g. a line with only 0000007),
      // consume this text as its payload
      if (this.gameState.pendingControl) {
        const code = this.gameState.pendingControl;
        this.gameState.pendingControl = null;
        try {
          this.handleControlMessage('\u001b' + code + String(text || ''));
        } catch (e) {
          this.appendMain(text);
        }
        return;
      }
      // quick-hotfix: if the entire line begins with zero-prefixed numeric marker (e.g. 0000007),
      // treat it as a control message immediately
      const zeroLineMatch = String(text || '').match(/^0{3,}(\d{1,3})([\s\S]*)$/);
      if (zeroLineMatch) {
        const code = zeroLineMatch[1].padStart(3, '0');
        const payload = zeroLineMatch[2] || '';
        if (!payload || payload.trim() === '') {
          // marker-only line: remember and let next incoming text be the payload
          this.gameState.pendingControl = code;
          return;
        }
        try {
          this.handleControlMessage('\u001b' + code + payload);
        } catch (e) {
          this.appendMain(text);
        }
        return;
      }
      const handshakeIndex = String(text || '').indexOf('ver');
      // allow ver to be preceded by some non-ASCII/garbage bytes — check within first 80 chars
      if (handshakeIndex !== -1 && handshakeIndex <= 80) {
        const line = String(text || '').slice(handshakeIndex).split(/\r?\n/)[0];
        const parts = line.split(',');
        const key = parts[1] || '';
        this.gameState.serverKey = key;
        if (!this.gameState.checkSent) {
          fetch(`/api/check?ip=${encodeURIComponent(this.activeServer.host)}&port=${encodeURIComponent(this.activeServer.port)}&key=${encodeURIComponent(key)}`)
            .then((r) => r.text())
            .then((resp) => {
              // Android 客户端先把 check.php 的响应原样发回 MUD 服务器，服务器会返回“版本验证成功”或失败信息
              // 只发送一次，防止服务器重复握手导致循环发送无意义命令
              this.sendRaw(String(resp || '') + '\n');
              this.gameState.checkSent = true;
            })
            .catch(() => {});
        }
        return;
      }
      if (text === '版本验证成功') {
        if (!this.gameState.loginQueued) {
          this.gameState.loginQueued = true;
          this.sendRaw(`${this.profile.id}║${this.profile.pass}║${this.activeServer.key || this.gameState.serverKey || ''}║${this.profile.email || ''}\n`);
        }
        return;
      }
      // process ESC-prefixed control sequences anywhere in the text
      // also support zero-prefixed numeric markers like "0000007" which can appear
      // when binary bytes are replaced by high-ASCII characters in the text stream
      if (text.includes('\u001b') || /0{3,}\d{1,3}/.test(text)) {
        let rest = String(text || '');
        while (rest.length) {
          const escIdx = rest.indexOf('\u001b');
          const zeroIdxMatch = rest.match(/0{3,}\d{1,3}/);
          const zeroIdx = zeroIdxMatch ? rest.indexOf(zeroIdxMatch[0]) : -1;
          // choose the earliest marker (ESC vs zero-prefixed)
          let markerIdx = -1;
          let markerType = null; // 'esc' or 'zero'
          if (escIdx !== -1 && (zeroIdx === -1 || escIdx <= zeroIdx)) {
            markerIdx = escIdx; markerType = 'esc';
          } else if (zeroIdx !== -1) {
            markerIdx = zeroIdx; markerType = 'zero';
          }
          if (markerIdx > 0) {
            const prefix = rest.slice(0, markerIdx);
            this.appendMain(prefix);
            rest = rest.slice(markerIdx);
            continue;
          }
          if (markerIdx === 0 && markerType === 'esc') {
            if (rest.length <= 4) {
              // marker-only (e.g. ESC+3digits with no payload yet) — pend until next incoming
              const pendingCode = rest.slice(1, 4);
              this.gameState.pendingControl = pendingCode;
              return;
            }
            const code = rest.slice(1, 4);
                        // 计算consumeLength：查找下一个真正的消息头（\u001b + 3位数字）
            // 而不是ANSI颜色代码（\u001b[...m）
            let consumeLength = rest.length; // 默认消费到末尾
            
            // 从位置4开始查找下一个ESC
            let searchPos = 4;
            while (searchPos < rest.length) {
              const nextEscIdx = rest.indexOf('\u001b', searchPos);
              if (nextEscIdx === -1) break;
              
              // 检查这个ESC后面是否是3位数字（新的消息头）
              if (nextEscIdx + 4 <= rest.length) {
                const nextCode = rest.slice(nextEscIdx + 1, nextEscIdx + 4);
                // 判断是否是3位数字代码
                if (/^\d{3}$/.test(nextCode)) {
                  // 这是新的消息头，在此处停止
                  consumeLength = nextEscIdx;
                  break;
                }
              }
              
              // 否则这是ANSI代码或其他控制序列，跳过它
              // 查找这个ESC序列的结束位置（通常是'm'字符）
              const seqEnd = rest.indexOf('m', nextEscIdx);
              if (seqEnd !== -1) {
                searchPos = seqEnd + 1;
              } else {
                // 如果找不到'm'，跳过这个ESC继续查找
                searchPos = nextEscIdx + 1;
              }
            }
            
            const payload = rest.slice(4, consumeLength);
            try {
              this.handleControlMessage('\u001b' + code + payload);
            } catch (e) {
              // 不将原始payload回退到主界面，避免显示未解析的协议数据
            }
            // 保留剩余文本继续处理
            rest = rest.slice(consumeLength);
            continue;
          }
          if (markerIdx === 0 && markerType === 'zero') {
            // match zero-prefixed marker like "0000007" or "000007" followed by payload
            const m = rest.match(/^(0{3,})(\d{1,3})/);
            if (!m) {
              // fallback: append and bail
              this.appendMain(rest);
              rest = '';
              break;
            }
            const zeros = m[1];
            const code = m[2].padStart(3, '0');
            const markerLen = zeros.length + code.length;
            const after = rest.slice(markerLen);
            if (!after || after.trim() === '') {
              // marker-only line: pend until next incoming
              this.gameState.pendingControl = code;
              return;
            }
            // emulate Android: payload is everything after marker
            const payload = after;
            try {
              this.handleControlMessage('\u001b' + code + payload);
            } catch (e) {
              this.appendMain(rest.slice(0, markerLen) + payload);
            }
            rest = rest.slice(markerLen + payload.length);
            continue;
          }
          // no marker found — append remaining text as main content
          this.appendMain(rest);
          rest = '';
        }
        return;
      }
      // fallback: treat entire text as main content
      this.appendMain(text);
    },
    handleControlMessage(text) {
      const code = text.slice(1, 4);
      const payload = text.slice(4);
      switch (code) {
        case '002':
          this.targets = [];
          this.exits = [];
          this.sceneTitle = payload;
          this.roomName = payload;
          this.updateLocationName(payload);
          return;
        case '001':
          this.actionBlocks = [{ key: `a-${Date.now()}`, title: '交互面板', kind: '001', cols: 2, items: parseActionItems(payload).items }];
          return;
        case '003': {
          const newExits = parseExitItems(payload).map((item) => ({ ...item, visible: true }));
          if (this.exits && this.exits.length > 0) {
            const existingKeys = new Set(this.exits.map(e => e.key));
            newExits.forEach(exit => {
              if (!existingKeys.has(exit.key)) {
                this.exits.push(exit);
              }
            });
          } else {
            this.exits = newExits;
          }
          return;
        }
        case '004':
          this.storyHtml = renderMudText(payload, this.gameState.ansi, { mode: this.settings.mode });
          return;
        case '005': {
          const newTargets = parseTargetItems(payload);
          if (this.targets && this.targets.length > 0) {
            const existingKeys = new Set(this.targets.map(t => t.key));
            newTargets.forEach(target => {
              if (!existingKeys.has(target.key)) {
                this.targets.push(target);
              }
            });
          } else {
            this.targets = newTargets;
          }
          return;
        }
        case '006':
          this.customCmds = parseActionItems(payload, 4).items.map((item, idx) => ({
            key: item.key || `cmd-${idx}`,
            cmd: item.cmd || item.key || '',
            label: item.label || item.html || '',
            labelHtml: item.labelHtml || item.html || ''
          }));
          return;
        case '905': {
          // 从targets（物品列表）中移除指定的对象
          const objId = payload.trim();
          if (this.targets && this.targets.length > 0) {
            this.targets = this.targets.filter(target => target.cmd !== objId);
          }
          return;
        }
        case '007': {
          this.showGui = true;
          this.guiTitle = extractGuiTitle(payload);
          this.guiTitleHtml = renderMudText(this.guiTitle, createAnsiState(), { mode: 'dark' });
          this.guiColumns = 3; // 默认3列
          // 将$br#转换为\n换行符，让renderStyledText内部的replace(/\n/g, '<br/>')处理
          const processedPayload = payload.replace(/\$br#/g, '\n');
          this.guiHtml = renderMudText(processedPayload, createAnsiState(), { mode: 'dark' });
          // 不清空guiActions1和guiActions2，等待008/009消息设置
          this.guiTab = 'content';
          return;
        }
        case '008': {
          const parsed = parseActionItems(payload);
          this.showGui = true;
          this.guiColumns = parsed.columns || 3;
          this.guiActions1 = parsed.items;
          this.guiTab = 'actions';
          return;
        }
        case '009': {
          const parsed = parseActionItems(payload);
          this.showGui = true;
          this.guiColumns = parsed.columns || 3;
          this.guiActions2 = parsed.items;
          this.guiTab = 'actions';
          return;
        }
        case '010':
          this.dialog = parseDialog(payload);
          return;
        case '011':
          this.mapHtml = renderMudText(payload, this.gameState.ansi, { mode: this.settings.mode });
          this.showMap = true;
          return;
        case '012': {
          console.log('[DEBUG] 收到012消息，原始payload:', payload);
          const parsed = parseStatusBars(payload);
          console.log('[DEBUG] 解析后的statusBars:', parsed);
          this.statusBars = parsed;
          return;
        }
        case '013':
          this.moreHtml = renderMudText(payload.replace(/\$br#/g, '\n'), this.gameState.ansi, { mode: this.settings.mode });
          this.showMoreText = true;
          return;
        case '015':
          this.pushToast(payload);
          this.appendSystem(payload);
          return;
        case '016':
          this.fightVisible = true;
          this.appendFight(payload);
          return;
        case '017':
          this.fightVisible = false;
          return;
        case '020': {
          const parsed = parseActionItems(payload, 4);
          this.popup = { visible: true, title: '菜单', items: parsed.items, columns: 4 };
          return;
        }
        case '021': {
          const parsed = parseQuickCmds(payload);
          this.quickCmds = parsed.items;
          return;
        }
        case '022':
          this.updateTargetBars(payload);
          return;
        case '023':
          this.storyVisible = payload !== '屏蔽描述';
          return;
        case '024':
          this.spawnFloatingText(payload);
          return;
        case '045':
          this.webUrl = payload;
          this.showWeb = true;
          return;
        case '100':
          this.appendChat(payload);
          return;
        case '900': {
          const [host, port] = payload.split(':');
          if (host && port) this.connectServer({ name: host, host, port, encoding: this.settings.encoding, key: this.activeServer?.key || '' });
          return;
        }
        case '997':
          this.settings.cmdss = false;
          this.persist();
          return;
        case '998':
          this.settings.cmdss = true;
          this.persist();
          return;
        case '999':
          this.disconnect();
          return;
      }
      this.appendMain(text);
    },
    appendRow(feed, html) {
      this.feed[feed].push({ html });
      if (this.feed[feed].length > 500) this.feed[feed].shift();
      this.$nextTick(() => {
        const main = this.$refs.leftFeed;
        if (main && feed !== 'chat') main.scrollTop = main.scrollHeight;
        const chat = this.$refs.chatFeed;
        if (chat && feed === 'chat') chat.scrollTop = chat.scrollHeight;
      });
    },
    appendMain(text) {
      if (!stripAnsiLike(text)) return;
      const rawText = String(text || '');
      this.settings = this.settings || {};
      if (typeof this.settings.showMycmdsInMain === 'undefined') this.settings.showMycmdsInMain = false;
      if (/(u001b|0{3,}\d{1,3})/.test(rawText)) {
        try {
          if (!rawText.startsWith('\u001b')) {
            this.processIncomingText(text);
            return;
          }
        } catch (e) {}
      }
      if (rawText.includes('$zj#') && /:/.test(rawText)) {
        const isMycmds = /\bmycmds\b/.test(rawText) || rawText.includes('常用') || rawText.includes('mycmds');
        if (this.settings && this.settings.showMycmdsInMain) {
          const inner = renderMudText(text, this.gameState.ansi, { mode: this.settings.mode });
          if (isMycmds) {
            this.appendRow('main', `<div style="background:#fff7c4;padding:6px;border-left:3px solid #ffcc00">${inner}</div>`);
          } else {
            this.appendRow('main', inner);
          }
          return;
        }
      }
      const html = renderMudText(text, this.gameState.ansi, { mode: this.settings.mode });
      this.appendRow('main', html);
    },
    updateTargetBars(payload) {
      // 022消息格式: look /clone/user/user#2909738$zj#350:350:350
      const parts = payload.split('$zj#');
      if (parts.length >= 2) {
        const targetId = parts[0].trim(); // 目标标识，如 "look /clone/user/user#2909738"
        const qiXueRaw = parts[1].trim(); // 气血值，如 "350:350:350"
        
        // 将冒号分隔转换为斜杠分隔 (350:350:350 -> 350/350)
        const qiXueParts = qiXueRaw.split(':');
        let qiXueValue = qiXueRaw;
        if (qiXueParts.length >= 2) {
          qiXueValue = `${qiXueParts[0]}/${qiXueParts[1]}`;
        }
        
        // 在targets中查找匹配的目标并更新气血值
        if (this.targets && this.targets.length > 0) {
          for (let i = 0; i < this.targets.length; i++) {
            const item = this.targets[i];
            // 检查目标的cmd是否包含目标标识
            if (item.cmd && item.cmd.includes(targetId)) {
              // 更新目标的气血显示
              this.$set(this.targets, i, {
                ...item,
                qiXue: qiXueValue
              });
              break;
            }
                     }
        }
      }
    },
    spawnFloatingText(payload) {
      const id = Date.now();
      this.floatingTexts.push({ id, text: payload, style: 'top:20%;left:50%' });
      setTimeout(() => {
        this.floatingTexts = this.floatingTexts.filter(t => t.id !== id);
      }, 2200);
    },
    sendCommand(cmd) {
      if (!cmd || !this.ws || this.ws.readyState !== WebSocket.OPEN) return;
      this.ws.send(JSON.stringify({ action: 'input', text: cmd + '\n' }));
    },
    sendRaw(text) {
      if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;
      this.ws.send(JSON.stringify({ action: 'input', text }));
    },
    disconnect() {
      if (this.ws) {
        try { this.ws.close(); } catch {}
        this.ws = null;
      }
      this.connected = false;
      this.screen = 'home';
    },
    handleObjectClick(target) {
      this.sendCommand(target.cmd);
    },
    toggleChatRoom() {
      this.chatRoomVisible = !this.chatRoomVisible;
    },
    toggleStory() {
      this.storyVisible = !this.storyVisible;
    },
    updateLocationName(name) {
      // 移除ANSI代码（包括带ESC和不带ESC的）
      this.locationName = String(name || '').replace(/\u001b\[[0-9;?]*[A-Za-z]/g, '').replace(/\[[0-9;?]*m/g, '').trim();
    },
    submitDialog() {
      if (this.dialog.okCommands.length) {
        this.sendCommand(this.dialog.okCommands[0]);
      }
      this.dialog.visible = false;
    },
    cancelDialog() {
      if (this.dialog.cancelCommand) {
        this.sendCommand(this.dialog.cancelCommand);
      }
      this.dialog.visible = false;
    },
    closeDialog() {
      this.dialog.visible = false;
    },
    handleFeedClick(event) {
      // Handle clickable links in feed
    },
    renderBlock(block) {
      return renderMudText(block, this.gameState.ansi, { mode: this.settings.mode });
    }
  }
};

export default mudAppOptions;
