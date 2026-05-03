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
    const profile = loadJSON('mud.profile', { id: '', pass: '', email: '', phone: '', name: '', sex: '男性', myserver: '' });
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
      ucTab: 'account',
      ucNewPass: '',
      ucNewPhone: '',
      ucNewEmail: '',
      ucServerName: '',
      ucServerHost: '',
      ucServerPort: '',
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
      customEditMode: false,  // 自定义编辑模式开关
      customButtons: {},  // 自定义按钮数据 {server_account: {b1: {label, cmd}, b2: {...}, ...}}
      lastCustomButtons: null,  // 保存上一次的自定义按钮数据
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
      allowMainFeed: false,  // 控制是否允许在主窗口显示消息（收到015后才允许）
      dialog: {
        visible: false,
        title: '对话框',
        blocks: [],
        items: [],
        exp: '',
        money: '',
        numberNeeded: false,
        hasQuantity: false,
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
      // 创建角色相关状态
      showCreateChar: false,
      createCharSex: '男性',
      createCharName: '',
      ws: null,
      wsEncoding: 'utf8',
      gameState: {
        serverKey: '',
        loginQueued: false,
        versionChecked: false,
        checkSent: false,
        ansi: createAnsiState(settings.mode || 'dark'),
        keepAliveTimer: null,
        reconnectTimer: null,
        reconnectAttempts: 0
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
    openUserCenter() { this.ucTab = 'account'; this.showUserCenter = true; this.fetchUserInfo(); },
    openSettings() { this.showSettings = true; },
    saveProfile() { this.persist(); this.pushToast('资料已保存'); },
    saveSettings() { this.persist(); this.showSettings = false; this.pushToast('设置已保存'); },
    fetchUserInfo() {
      if (!this.profile.id || !this.profile.pass) return;
      return fetch(`https://mud1.foxmoe.top/mobi/userinfo.php?id=${encodeURIComponent(this.profile.id)}&pass=${encodeURIComponent(this.profile.pass)}`)
        .then(r => r.text())
        .then(text => {
          if (text.indexOf('$i#') === 0) {
            const parts = text.substring(3).split('|');
            this.profile.id = parts[0] || this.profile.id;
            this.profile.email = parts[3] || this.profile.email;
            this.profile.phone = parts[2] || this.profile.phone;
            this.profile.myserver = parts[4] || '';
            this.persist();
          }
        })
        .catch(() => {});
    },
    updateUcenterPass(newpwd) {
      return fetch(`https://mud1.foxmoe.top/mobi/updateuser.php?id=${encodeURIComponent(this.profile.id)}&pass=${encodeURIComponent(this.profile.pass)}&newpwd=${encodeURIComponent(newpwd)}`)
        .then(r => r.text())
        .then(text => {
          if (text.indexOf('密码修改成功') !== -1) {
            this.profile.pass = text.replace('密码修改成功', '');
            this.persist();
            this.pushToast('密码修改成功');
          } else {
            this.pushToast(text || '修改失败');
          }
        })
        .catch(() => this.pushToast('修改失败，请检查网络'));
    },
    updateUcenterPhone(phone) {
      return fetch(`https://mud1.foxmoe.top/mobi/updateuser.php?id=${encodeURIComponent(this.profile.id)}&pass=${encodeURIComponent(this.profile.pass)}&phone=${encodeURIComponent(phone)}`)
        .then(r => r.text())
        .then(text => {
          if (text.indexOf('手机号绑定成功') !== -1) {
            this.profile.phone = text.replace('手机号绑定成功', '');
            this.persist();
            this.pushToast('手机号绑定成功');
          } else {
            this.pushToast(text || '修改失败');
          }
        })
        .catch(() => this.pushToast('修改失败，请检查网络'));
    },
    updateUcenterEmail(email) {
      return fetch(`https://mud1.foxmoe.top/mobi/updateuser.php?id=${encodeURIComponent(this.profile.id)}&pass=${encodeURIComponent(this.profile.pass)}&email=${encodeURIComponent(email)}`)
        .then(r => r.text())
        .then(text => {
          if (text.indexOf('邮箱绑定成功') !== -1) {
            this.profile.email = text.replace('邮箱绑定成功', '');
            this.persist();
            this.pushToast('邮箱绑定成功');
          } else {
            this.pushToast(text || '修改失败');
          }
        })
        .catch(() => this.pushToast('修改失败，请检查网络'));
    },
    updateMyServer(myserver) {
      const encoded = encodeURIComponent(myserver);
      return fetch(`https://mud1.foxmoe.top/mobi/updateuser.php?id=${encodeURIComponent(this.profile.id)}&pass=${encodeURIComponent(this.profile.pass)}&myserver=${encoded}`)
        .then(r => r.text())
        .then(text => {
          if (text.indexOf('测试服务器修改成功') !== -1) {
            this.profile.myserver = text.replace('测试服务器修改成功', '');
            this.persist();
            this.pushToast('自定义服务器已保存');
          } else {
            this.pushToast(text || '修改失败');
          }
        })
        .catch(() => this.pushToast('修改失败，请检查网络'));
    },
    saveUcenterServer() {
      if (!this.ucServerHost || !this.ucServerPort) {
        this.pushToast('请填写服务器地址和端口');
        return;
      }
      const name = this.ucServerName || this.ucServerHost;
      const portNum = parseInt(this.ucServerPort, 10) || 23;
      const myserver = `${name}&${this.ucServerHost}&${this.ucServerPort}&${portNum + 1}`;
      this.updateMyServer(myserver);
      this.ucServerName = '';
      this.ucServerHost = '';
      this.ucServerPort = '';
    },
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
      this.allowMainFeed = false;  // 重置标志位，等待015消息
      this.gameState = {
        serverKey: '',
        loginQueued: false,
        versionChecked: false,
        checkSent: false,
        ansi: createAnsiState(this.settings.mode || 'dark')
      };
      this.openSocket(server.host, server.port, server.encoding || this.settings.encoding || 'utf8');
    },
    
    // 获取自定义按钮的存储键（按服务器+账号）
    getCustomButtonsKey() {
      if (!this.activeServer || !this.profile.id) return null;
      return `custom_buttons_${this.activeServer.host}_${this.activeServer.port}_${this.profile.id}`;
    },
    
    // 保存上一次的自定义按钮数据
    saveLastCustomButtons() {
      const row1 = this.customCmds.filter(cmd => {
        const match = cmd.key.match(/^b(\d+)-/);
        if (match) {
          const num = parseInt(match[1]);
          return num >= 1 && num <= 5;
        }
        return false;
      });
      const row2 = this.customCmds.filter(cmd => {
        const match = cmd.key.match(/^b(\d+)-/);
        if (match) {
          const num = parseInt(match[1]);
          return num >= 6 && num <= 11;
        }
        return false;
      });
      
      this.lastCustomButtons = [...row1, ...row2];
    },
    
    // 加载自定义按钮用于编辑模式
    loadCustomButtonsForEdit() {
      const buttons = this.loadCustomButtonsArray();
      this.customCmds = buttons;
    },
    
    // 加载自定义按钮数组（b1-b11）
    loadCustomButtonsArray() {
      const key = this.getCustomButtonsKey();
      if (!key) {
        // 没有有效的键，显示空按钮
        return this.generateEmptyCustomButtons();
      }
      
      const saved = loadJSON(key, null);
      if (saved && Object.keys(saved).length > 0) {
        // 有保存的自定义按钮，转换为数组格式
        const buttons = [];
        for (let i = 1; i <= 11; i++) {
          const btnKey = `b${i}`;
          if (saved[btnKey]) {
            buttons.push({
              key: `${btnKey}-${saved[btnKey].label}`,
              cmd: saved[btnKey].cmd || '',
              label: saved[btnKey].label || '长按',
              labelHtml: renderMudText(saved[btnKey].label || '长按', createAnsiState(), { mode: this.settings.mode })
            });
          } else {
            // 没有自定义的按钮，显示为空
            buttons.push({
              key: `${btnKey}-empty`,
              cmd: '',
              label: '',
              labelHtml: ''
            });
          }
        }
        return buttons;
      } else {
        // 没有保存的数据，显示空按钮
        return this.generateEmptyCustomButtons();
      }
    },
    
    // 生成空的自定义按钮数组
    generateEmptyCustomButtons() {
      const buttons = [];
      for (let i = 1; i <= 11; i++) {
        buttons.push({
          key: `b${i}-empty`,
          cmd: '',
          label: '',
          labelHtml: ''
        });
      }
      return buttons;
    },
    
    // 合并自定义按钮到服务器发送的按钮列表
    mergeCustomButtons(serverItems) {
      // 检查新数据中是否包含 b12-b17
      const newRow3 = serverItems.filter(item => {
        const match = item.key.match(/^b(\d+)-/);
        if (match) {
          const num = parseInt(match[1]);
          return num >= 12 && num <= 17;
        }
        return false;
      });
      
      if (newRow3.length === 0) {
        // 新数据中没有 b12-b17，保留之前的 b12-b17
        const oldRow3 = this.customCmds.filter(cmd => {
          const match = cmd.key.match(/^b(\d+)-/);
          if (match) {
            const num = parseInt(match[1]);
            return num >= 12 && num <= 17;
          }
          return false;
        });
        
        // 合并：服务器的 b1-b11 + 保留的 b12-b17
        const merged = this.mergeCustomButtonsToArray(serverItems);
        this.customCmds = [...merged, ...oldRow3];
      } else {
        // 新数据中有 b12-b17，正常合并
        const merged = this.mergeCustomButtonsToArray(serverItems);
        this.customCmds = merged;
      }
    },
    
    // 合并自定义按钮到服务器发送的按钮列表（返回数组）
    mergeCustomButtonsToArray(serverItems) {
      const key = this.getCustomButtonsKey();
      if (!key) {
        return serverItems;
      }
      
      const saved = loadJSON(key, null);
      if (!saved || Object.keys(saved).length === 0) {
        // 没有自定义按钮，直接使用服务器的
        return serverItems;
      }
      
      // 合并：用自定义按钮替换对应的 b1-b11
      const merged = serverItems.map(item => {
        const match = item.key.match(/^b(\d+)-/);
        if (match) {
          const num = parseInt(match[1]);
          if (num >= 1 && num <= 11 && saved[`b${num}`]) {
            // 使用自定义按钮
            const custom = saved[`b${num}`];
            return {
              key: item.key,
              cmd: custom.cmd || item.cmd,
              label: custom.label || item.label,
              labelHtml: renderMudText(custom.label || item.label, createAnsiState(), { mode: this.settings.mode })
            };
          }
        }
        return item;
      });
      
      return merged;
    },
    
    // 切换自定义编辑模式
    toggleCustomEditMode() {
      if (this.customEditMode) {
        // 退出自定义模式
        this.customEditMode = false;
        
        // 保留当前的 b12-b17
        const row3 = this.customCmds.filter(cmd => {
          const match = cmd.key.match(/^b(\d+)-/);
          if (match) {
            const num = parseInt(match[1]);
            return num >= 12 && num <= 17;
          }
          return false;
        });
        
        if (this.lastCustomButtons && this.lastCustomButtons.length > 0) {
          // 有上一次的数据，恢复 b1-b11
          this.customCmds = [...this.lastCustomButtons, ...row3];
        } else {
          // 没有上一次的数据，b1-b11 显示为空按钮
          const emptyB1toB11 = this.generateEmptyCustomButtons();
          this.customCmds = [...emptyB1toB11, ...row3];
        }
        
        this.lastCustomButtons = null;
      } else {
        // 进入自定义模式，保存当前 b1-b11 作为上一次数据
        this.saveLastCustomButtons();
        this.customEditMode = true;
        
        // 加载自定义按钮（b1-b11）
        const customB1toB11 = this.loadCustomButtonsArray();
        
        // 保留当前的 b12-b17
        const row3 = this.customCmds.filter(cmd => {
          const match = cmd.key.match(/^b(\d+)-/);
          if (match) {
            const num = parseInt(match[1]);
            return num >= 12 && num <= 17;
          }
          return false;
        });
        
        // ✅ 合并：自定义的 b1-b11 + 保留的 b12-b17
        this.customCmds = [...customB1toB11, ...row3];
      }
    },
    
    // 保存自定义按钮
    saveCustomButton(buttonIndex, label, cmd) {
      const key = this.getCustomButtonsKey();
      if (!key) return;
      
      const saved = loadJSON(key, {});
      saved[`b${buttonIndex}`] = { label, cmd };
      saveJSON(key, saved);
      
      // 更新当前显示的按钮
      if (this.customCmds[buttonIndex - 1]) {
        this.customCmds[buttonIndex - 1].label = label;
        this.customCmds[buttonIndex - 1].cmd = cmd;
        this.customCmds[buttonIndex - 1].labelHtml = renderMudText(label, createAnsiState(), { mode: this.settings.mode });
      }
      
      this.pushToast('快捷键已保存');
    },
    
    openSocket(host, port, encoding) {
      if (this.ws) {
        try { this.ws.close(); } catch {}
      }
      const socketUrl = `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/`;
      this.wsEncoding = encoding || 'utf8';
      this.ws = new WebSocket(socketUrl);
      this.ws.addEventListener('open', () => {
        this.connected = true;
        this.gameState.reconnectAttempts = 0;
        this.startKeepAlive();
        this.ws.send(JSON.stringify({ action: 'connect', host, port, encoding: this.wsEncoding }));
      });
      this.ws.addEventListener('message', (event) => this.handleSocketMessage(event.data));
      this.ws.addEventListener('close', () => {
        this.connected = false;
        this.stopKeepAlive();
        this.scheduleReconnect(host, port, encoding);
        this.pushSystem('连接已关闭');
      });
      this.ws.addEventListener('error', () => {
        this.connected = false;
        this.stopKeepAlive();
      });
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
      
      // 处理编码检测通知
      if (message.type === 'encoding_detect') {
        this.handleEncodingDetection(message);
        return;
      }
      
      // 处理编码切换确认
      if (message.type === 'encoding_switched') {
        this.handleEncodingSwitched(message);
        return;
      }
      
      if (message.type === 'data') {
        const textToProcess = String(message.text || '');
        this.processIncomingText(textToProcess);
      }
    },
    
    // 处理编码检测通知
    handleEncodingDetection(message) {
      const { detected, current, suggestion } = message;
      
      // 如果检测到不同的编码，且当前是登录阶段（还未收到版本验证成功）
      if (detected !== current && !this.gameState.versionChecked) {
        console.log(`[自动检测] 建议使用编码: ${suggestion} (当前: ${current}, 检测到: ${detected})`);
        
        // 自动切换到建议的编码
        this.wsEncoding = suggestion;
        
        // 通知服务端切换编码并重发登录信息
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
          this.ws.send(JSON.stringify({ 
            action: 'switch_encoding', 
            encoding: suggestion 
          }));
          
          this.pushSystem(`检测到编码不匹配，自动切换到 ${suggestion} 并重试...`);
        }
      }
    },
    
    // 处理编码切换完成
    handleEncodingSwitched(message) {
      const { from, to, resent } = message;
      
      if (resent) {
        this.pushSystem(`已切换到 ${to} 编码并重新发送登录信息`);
        // 更新当前服务器的编码设置
        if (this.activeServer) {
          this.activeServer.encoding = to;
          this.persist();
        }
      } else {
        this.pushSystem(`编码已从 ${from} 切换到 ${to}`);
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
      // 但必须确保ver是在文本开头附近，而不是在payload中间
      if (handshakeIndex !== -1 && handshakeIndex <= 80 && handshakeIndex < 10) {
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
          // 如果ESC标记不在开头，且是零前缀标记，才分割处理
          // 对于ESC开头的消息，直接交给renderMudText处理，保持文本完整性
          if (markerIdx > 0 && markerType === 'zero') {
            const prefix = rest.slice(0, markerIdx);
            this.appendMain(prefix);
            rest = rest.slice(markerIdx);
            continue;
          }
          if (markerIdx === 0 && markerType === 'esc') {
            // 特殊处理：检查是否为可变长度数字代码（如 \u001b0000008）
            const numMatch = rest.match(/^\u001b(\d{3,})/);
            if (numMatch) {
              const fullCode = numMatch[1];
              const markerLen = 1 + fullCode.length;
              
              // 特殊处理：0000008 表示创建角色
              if (fullCode === '0000008') {
                this.showCreateChar = true;
                this.createCharSex = '男性';
                this.createCharName = '';
                rest = rest.slice(markerLen);
                continue;
              }
              
              // 其他消息类型，截取后3位作为标准代码
              const code = fullCode.slice(-3).padStart(3, '0');
              const payload = rest.slice(markerLen);
              
              try {
                this.handleControlMessage('\u001b' + code + payload);
              } catch (e) {
                // 不将原始payload回退到主界面
              }
              rest = rest.slice(markerLen + payload.length);
              continue;
            }
            
            if (rest.length <= 4) {
              // marker-only (e.g. ESC+3digits with no payload yet) — pend until next incoming
              const pendingCode = rest.slice(1, 4);
              this.gameState.pendingControl = pendingCode;
              return;
            }
            const code = rest.slice(1, 4);
                        // 对于012状态栏、006自定义命令、007/008/009 GUI消息，直接消费到末尾
            let consumeLength = rest.length;
            
            if (code === '012' || code === '006' || code === '007' || code === '008' || code === '009') {
              // 这些消息包含大量ANSI颜色代码，直接消费到末尾
              consumeLength = rest.length;
            } else {
              // 其他消息类型正常查找ESC边界
              let searchPos = 4;
              while (searchPos < rest.length) {
                const nextEscIdx = rest.indexOf('\u001b', searchPos);
                if (nextEscIdx === -1) break;
                
                if (nextEscIdx + 4 <= rest.length) {
                  const nextCode = rest.slice(nextEscIdx + 1, nextEscIdx + 4);
                  if (/^\d{3}$/.test(nextCode)) {
                    consumeLength = nextEscIdx;
                    break;
                  }
                }
                
                const seqEnd = rest.indexOf('m', nextEscIdx);
                if (seqEnd !== -1) {
                  searchPos = seqEnd + 1;
                } else {
                  searchPos = nextEscIdx + 1;
                }
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
        case '001': {
          // 001 消息是输入框类型，格式：提示文本$zj#命令前缀 $txt#
          const parts = payload.split('$zj#');
          let promptText = '';
          let cmdPrefix = '';
          
          if (parts.length >= 2) {
            promptText = parts[0].trim();
            // 第二部分可能包含 $txt# 标记
            const cmdPart = parts[1].replace(/\$txt#/g, '').trim();
            cmdPrefix = cmdPart;
          } else {
            // 如果没有 $zj# 分隔符，尝试直接查找 $txt#
            const txtIndex = payload.lastIndexOf('$txt#');
            if (txtIndex !== -1) {
              const beforeTxt = payload.slice(0, txtIndex).trim();
              cmdPrefix = beforeTxt;
              promptText = '请输入';
            } else {
              promptText = payload;
              cmdPrefix = '';
            }
          }
          
          this.actionBlocks = [{ 
            key: `a-${Date.now()}`, 
            title: '提示', 
            kind: '001', 
            cols: 1, 
            items: [{
              key: 'text-input-0',
              label: promptText,
              labelHtml: renderMudText(promptText, this.gameState.ansi, { mode: this.settings.mode }),
              cmd: '',
              cmdPrefix: cmdPrefix,
              isTextInput: true
            }]
          }];
          return;
        }
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
        case '903': {
          // 删除指定出口，payload 是出口的 cmd 字段（如 "northwest"、"eastup" 等）
          const exitCmd = payload.trim();
          if (this.exits && this.exits.length > 0) {
            this.exits = this.exits.filter(exit => exit.cmd !== exitCmd);
          }
          return;
        }
        case '913': {
          // 清除所有出口
          this.exits = [];
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
        case '006': {
          const parsed = parseActionItems(payload, 4);
          const newItems = parsed.items.map((item, idx) => ({
            key: item.key || `cmd-${idx}`,
            cmd: item.cmd || item.key || '',
            label: item.label || item.html || '',
            labelHtml: item.labelHtml || item.html || ''
          }));
          
          // ✅ 空响应保护：如果解析结果为空，不更新数据
          if (!newItems || newItems.length === 0) {
            return;
          }
          
          // 检查是否只包含 b12-b17（第三行按钮）
          const hasOnlyRow3 = newItems.every(item => {
            const match = item.key.match(/^b(\d+)-/);
            if (match) {
              const num = parseInt(match[1]);
              return num >= 12 && num <= 17;
            }
            return false;
          });
          
          if (hasOnlyRow3) {
            // 服务器只发送了 b12-b17
            // 保存当前的 b1-b11 作为上一次数据（如果还没保存）
            if (!this.customEditMode && this.lastCustomButtons === null) {
              this.saveLastCustomButtons();
            }
            
            // 进入自定义模式
            this.customEditMode = true;
            
            // 加载自定义按钮（b1-b11）
            const customB1toB11 = this.loadCustomButtonsArray();
            
            // 合并：自定义的 b1-b11 + 服务器的 b12-b17
            this.customCmds = [...customB1toB11, ...newItems];
          } else {
            // 服务器发送了完整的按钮列表（包含 b1-b11）
            if (this.customEditMode) {
              // ✅ 退出自定义模式
              this.customEditMode = false;
              
              // 检查新数据中是否包含 b12-b17
              const newRow3 = newItems.filter(item => {
                const match = item.key.match(/^b(\d+)-/);
                if (match) {
                  const num = parseInt(match[1]);
                  return num >= 12 && num <= 17;
                }
                return false;
              });
              
              if (newRow3.length === 0) {
                // 新数据中没有 b12-b17，保留之前的 b12-b17
                const oldRow3 = this.customCmds.filter(cmd => {
                  const match = cmd.key.match(/^b(\d+)-/);
                  if (match) {
                    const num = parseInt(match[1]);
                    return num >= 12 && num <= 17;
                  }
                  return false;
                });
                
                // 合并：服务器的 b1-b11 + 保留的 b12-b17
                const merged = this.mergeCustomButtonsToArray(newItems);
                this.customCmds = [...merged, ...oldRow3];
              } else {
                // 新数据中有 b12-b17，正常合并
                this.lastCustomButtons = null;
                this.mergeCustomButtons(newItems);
              }
            } else {
              // 正常模式：更新所有按钮，并合并自定义设置
              this.mergeCustomButtons(newItems);
            }
          }
          return;
        }
        case '905': {
          // 从targets（物品列表）中移除指定的对象
          const objId = payload.trim();
          if (this.targets && this.targets.length > 0) {
            this.targets = this.targets.filter(target => target.cmd !== objId);
          }
          return;
        }
        case '007': {
          // 如果当前有 actionBlocks，将 007 的 HTML 内容附加到其中
          if (this.actionBlocks && this.actionBlocks.length > 0) {
            const processedPayload = payload.replace(/\$br#/g, '\n');
            this.actionBlocks[0].guiHtml = renderMudText(processedPayload, createAnsiState(), { mode: 'dark' });
            // ✅ 不要修改 kind，保持原有的 001 类型
          } else {
            // 否则使用原有的 GUI 弹窗逻辑
            this.showGui = true;
            this.guiTitle = extractGuiTitle(payload);
            this.guiTitleHtml = renderMudText(this.guiTitle, createAnsiState(), { mode: 'dark' });
            this.guiColumns = 3;
            const processedPayload = payload.replace(/\$br#/g, '\n');
            this.guiHtml = renderMudText(processedPayload, createAnsiState(), { mode: 'dark' });
            this.guiTab = 'content';
          }
          return;
        }
        case '008': {
          const parsed = parseActionItems(payload);
          
          // 如果当前有 actionBlocks，将 008 的按钮附加到其中
          if (this.actionBlocks && this.actionBlocks.length > 0) {
            this.actionBlocks[0].guiActions1 = parsed.items;
            this.actionBlocks[0].guiColumns = parsed.columns || 3;
            // ✅ 不要修改 kind，保持原有的 001 类型
          } else {
            // 否则使用原有的 GUI 弹窗逻辑
            this.showGui = true;
            this.guiColumns = parsed.columns || 3;
            this.guiActions1 = parsed.items;
            this.guiTab = 'actions';
          }
          return;
        }
        case '009': {
          const parsed = parseActionItems(payload);
          
          // 如果当前有 actionBlocks（如 001 输入框），将 009 的按钮附加到 actionBlocks 中
          if (this.actionBlocks && this.actionBlocks.length > 0) {
            // 将 009 的按钮作为额外的 items 添加到 actionBlocks
            this.actionBlocks[0].guiButtons = parsed.items;
            this.actionBlocks[0].guiColumns = parsed.columns || 3;
          } else {
            // 否则使用原有的 GUI 弹窗逻辑
            this.showGui = true;
            this.guiColumns = parsed.columns || 3;
            this.guiActions2 = parsed.items;
            this.guiTab = 'actions';
          }
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
          const parsed = parseStatusBars(payload);
          // 使用splice确保Vue响应式更新
          this.statusBars.splice(0, this.statusBars.length, ...parsed);
          return;
        }
        case '013':
          this.moreHtml = renderMudText(payload.replace(/\$br#/g, '\n'), this.gameState.ansi, { mode: this.settings.mode });
          this.showMoreText = true;
          return;
        case '015':
          this.allowMainFeed = true;  // 收到015消息后允许主窗口显示消息
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
          const parsed = parseActionItems(payload);
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
      // 在收到015消息之前，不允许在主窗口显示消息
      if (!this.allowMainFeed) return;
      
      const rawText = String(text || '');
      this.settings = this.settings || {};
      if (typeof this.settings.showMycmdsInMain === 'undefined') this.settings.showMycmdsInMain = false;
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
    startKeepAlive() {
      this.stopKeepAlive();
      this.gameState.keepAliveTimer = setInterval(() => {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
          this.ws.send(JSON.stringify({ action: 'ping' }));
        }
      }, 30000);
    },
    stopKeepAlive() {
      if (this.gameState.keepAliveTimer) {
        clearInterval(this.gameState.keepAliveTimer);
        this.gameState.keepAliveTimer = null;
      }
    },
    scheduleReconnect(host, port, encoding) {
      if (this.gameState.reconnectAttempts >= 5) return;
      const delay = Math.min(1000 * Math.pow(2, this.gameState.reconnectAttempts), 30000);
      this.gameState.reconnectAttempts++;
      this.gameState.reconnectTimer = setTimeout(() => {
        this.openSocket(host, port, encoding);
      }, delay);
    },
    disconnect() {
      this.stopKeepAlive();
      if (this.gameState.reconnectTimer) {
        clearTimeout(this.gameState.reconnectTimer);
        this.gameState.reconnectTimer = null;
      }
      this.gameState.reconnectAttempts = 0;
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
      if (this.dialog.numberNeeded && !this.dialog.input) {
        this.pushToast('请输入数字');
        return;
      }
      if (this.dialog.okCommands.length) {
        let cmd = this.dialog.okCommands[0];
        if (this.dialog.hasQuantity) {
          cmd = cmd.replace(/\$N/g, this.dialog.input || '1');
        }
        this.sendCommand(cmd);
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
      const anchor = event.target.closest('[data-mud-cmd]');
      if (!anchor) return;
      event.preventDefault();
      let cmd = anchor.getAttribute('data-mud-cmd');
      // 移除 cmds: 前缀（如果存在）
      if (cmd && cmd.startsWith('cmds:')) {
        cmd = cmd.slice(5);
      }
      if (cmd) {
        this.sendCommand(cmd);
      }
    },
    renderBlock(block) {
      return renderMudText(block, this.gameState.ansi, { mode: this.settings.mode });
    },
    // 创建角色相关方法
    confirmCreateChar() {
      if (!this.createCharName || this.createCharName.trim() === '') {
        this.pushToast('请输入角色名称');
        return;
      }
      // 发送创建角色命令：性别║║角色名称
      const cmd = `${this.createCharSex}║║${this.createCharName}`;
      this.sendRaw(cmd + '\n');
      this.showCreateChar = false;
    },
    cancelCreateChar() {
      this.showCreateChar = false;
      this.createCharSex = '男性';
      this.createCharName = '';
    }
  }
};

export default mudAppOptions;
