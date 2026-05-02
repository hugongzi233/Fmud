const ESC = '\u001b';

function escapeHtml(input) {
  return String(input)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function escapeAttr(input) {
  return escapeHtml(input).replace(/`/g, '&#96;');
}

function stripAnsiCodes(s) {
  return String(s || '').replace(/\u001b\[[0-9;?]*[A-Za-z]/g, '').trim();
}

export function toFullWidth(input) {
  return String(input).split('').map((char) => {
    if (char === ' ') return '\u3000';
    const code = char.charCodeAt(0);
    if (code >= 33 && code <= 126) return String.fromCharCode(code + 0xFEE0);
    return char;
  }).join('');
}

export function createAnsiState(mode = 'dark') {
  return {
    fg: '',
    bg: '',
    bold: false,
    full: false,
    size: null,
    url: '',
    mode
  };
}

function colorForAnsi(code, mode) {
  const dayBlue = {
    '32': '#8ab4f8',
    '33': '#8ab4f8',
    '36': '#8ab4f8',
    '37': '#8ab4f8',
    '1;32': '#8ab4f8',
    '1;33': '#8ab4f8',
    '1;36': '#8ab4f8',
    '1;37': '#8ab4f8'
  };
  if (mode === 'day' && dayBlue[code]) return dayBlue[code];
  const map = {
    '30': '#dbe3f0',
    '31': '#ff8a80',
    '32': '#7ee787',
    '33': '#f7d774',
    '34': '#8ab4f8',
    '35': '#d0a2ff',
    '36': '#8de1d6',
    '37': '#b6c3d8',
    '1;30': '#f8fafc',
    '1;31': '#ff5f57',
    '1;32': '#4ade80',
    '1;33': '#facc15',
    '1;34': '#60a5fa',
    '1;35': '#e879f9',
    '1;36': '#2dd4bf',
    '1;37': '#f8fafc'
  };
  return map[code] || '';
}

function backgroundForAnsi(code) {
  const map = {
    '40': '#0f172a',
    '41': '#7f1d1d',
    '42': '#14532d',
    '43': '#854d0e',
    '44': '#1e3a8a',
    '45': '#581c87',
    '46': '#134e4a',
    '47': '#334155',
    '40;1': '#111827',
    '41;1': '#991b1b',
    '42;1': '#166534',
    '43;1': '#92400e',
    '44;1': '#1d4ed8',
    '45;1': '#7e22ce',
    '46;1': '#0f766e',
    '47;1': '#cbd5e1'
  };
  return map[code] || '';
}

function renderStyledText(raw, state) {
  const text = state.full ? toFullWidth(raw) : raw;
  const style = [];
  if (state.fg) style.push(`color:${state.fg}`);
  if (state.bg) style.push(`background:${state.bg}`);
  if (state.bold) style.push('font-weight:800');
  if (state.size) {
    // 将 Android px 转换为 Web CSS px
    // Android px 通常需要考虑屏幕密度，这里使用简单的缩放比例
    // 假设 Android 设计基准为 2x 密度，所以除以 2
    const webSize = Math.round(state.size * 0.5);
    style.push(`font-size:${webSize}px`);
  }
  style.push('white-space:pre-wrap');
  // 注意：不在这里转换换行符，让 CSS white-space:pre-wrap 处理实际的换行
  const content = escapeHtml(text);
  if (state.url) {
    return `<a class="mud-link" data-mud-cmd="${escapeAttr(state.url)}" style="${style.join(';')}">${content}</a>`;
  }
  return `<span style="${style.join(';')}">${content}</span>`;
}

export function renderMudText(input, state, options = {}) {
  const onClear = options.onClear || null;
  const onReset = options.onReset || null;
  const mode = options.mode || state.mode || 'dark';
  
  // 创建状态副本，避免修改原始状态对象导致样式泄漏
  const localState = { ...state, mode };
  
  let text = String(input ?? '').replace(/\r/g, '');
  if (!text) return '';
  if (text.indexOf(`${ESC}[2;37;0m`) === 0) {
    onReset && onReset();
    text = text.slice(9);
  }
  const tokens = text.split(ESC);
  let html = '';
  for (let i = 0; i < tokens.length; i++) {
    const chunk = tokens[i];
    if (!chunk) continue;
    if (i === 0 && !text.startsWith(ESC)) {
      html += renderStyledText(chunk, localState);
      continue;
    }
    let working = chunk;
    if (working.startsWith('[2J')) {
      onClear && onClear();
      working = working.slice(3);
    }
    if (working.startsWith('[H')) {
      working = working.slice(2);
    }
    if (working.startsWith('[u')) {
      const end = working.indexOf(']');
      if (end > 0) {
        localState.url = working.slice(3, end);
        working = working.slice(end + 1);
      }
    }
    if (working.startsWith('[s')) {
      const end = working.indexOf(']');
      if (end > 0) {
        const size = Number.parseInt(working.slice(3, end), 10);
        if (Number.isFinite(size) && size > 0) localState.size = size;
        working = working.slice(end + 1);
      }
    }
    const codeEnd = working.indexOf('m');
    if (codeEnd >= 0) {
      const code = working.slice(0, codeEnd + 1);
      const shortCode = code.slice(1, -1);
      if (shortCode === '0' || shortCode.endsWith(';0')) {
        localState.fg = '';
        localState.bg = '';
        localState.bold = false;
        localState.full = false;
        localState.size = null;
        localState.url = '';
      } else if (shortCode === '1') {
        localState.bold = true;
      } else if (shortCode === '9') {
        localState.full = true;
      } else if (shortCode.startsWith('f#')) {
        localState.fg = shortCode.slice(2);
      } else if (shortCode.startsWith('b#')) {
        localState.bg = shortCode.slice(2);
      } else {
        const fg = colorForAnsi(shortCode, mode);
        const bg = backgroundForAnsi(shortCode);
        if (fg) localState.fg = fg;
        if (bg) localState.bg = bg;
      }
      working = working.slice(codeEnd + 1);
    }
    if (working) {
      html += renderStyledText(working, localState);
    }
  }
  return html;
}

export function parseLoginServers(raw) {
  const text = String(raw || '').trim();
  if (!text.startsWith('$l#')) {
    return { servers: [], email: '', error: text };
  }
  const parts = text.slice(3).split('|').filter(Boolean);
  const email = parts.length ? parts[parts.length - 1] : '';
  const servers = parts.slice(0, -1).map((entry, index) => {
    const fields = entry.split('&');
    return {
      id: `${index}-${Date.now()}`,
      name: fields[0] || `分区 ${index + 1}`,
      host: fields[1] || '',
      port: fields[2] || '23',
      online: fields[3] || '',
      status: fields[4] || '',
      key: fields[5] || '',
      raw: entry,
      encoding: 'utf8'
    };
  });
  return { servers, email };
}

export function splitPairs(payload) {
  return String(payload || '').split('$zj#').filter(Boolean);
}

export function parseActionItems(raw, defaultColumns = 2) {
  let payload = String(raw || '');
  let columns = defaultColumns;
  
  // 解析元数据 $columns,rows,sizeX,sizeY#
  if (payload.startsWith('$')) {
    const end = payload.indexOf('#');
    if (end > 0) {
      const meta = payload.slice(1, end).split(',');
      columns = Math.max(1, Number.parseInt(meta[0] || String(defaultColumns), 10) || defaultColumns);
      payload = payload.slice(end + 1);
    }
  }
  
  const items = splitPairs(payload).map((entry, index) => {
    let working = String(entry || '');
    
    // 检测是否包含 $txt# 标记（输入框类型）
    const txtMarkerIndex = working.lastIndexOf('$txt#');
    let isTextInput = false;
    let cmdPrefix = '';
    let labelRaw = '';
    
    if (txtMarkerIndex !== -1) {
      // 包含 $txt# 标记，这是输入框类型
      isTextInput = true;
      // $txt# 之前的部分是 "label:cmd" 格式
      const beforeTxt = working.slice(0, txtMarkerIndex).trim();
      const lastColon = beforeTxt.lastIndexOf(':');
      
      if (lastColon !== -1) {
        labelRaw = beforeTxt.slice(0, lastColon).trim();
        cmdPrefix = beforeTxt.slice(lastColon + 1).trim();
      } else {
        // 没有冒号，整个作为命令前缀
        cmdPrefix = beforeTxt;
        labelRaw = '输入';
      }
    } else {
      // 普通按钮类型，使用最后一个冒号分割label和cmd
      const lastColon = working.lastIndexOf(':');
      
      if (lastColon !== -1) {
        labelRaw = working.slice(0, lastColon).trim();
        cmdPrefix = working.slice(lastColon + 1).trim();
      } else {
        // 没有冒号，整个作为label
        labelRaw = working;
        cmdPrefix = '';
      }
    }
    
    // 提取并保存按钮标识前缀（如 b1, b12, b17 等）
    let buttonId = '';
    const buttonIdMatch = labelRaw.match(/^b(\d+):/);
    if (buttonIdMatch) {
      buttonId = `b${buttonIdMatch[1]}`;
    }
    
    // 移除按钮标识前缀（如 b1:, b12:, b17: 等）
    // 匹配模式：以 b 开头，后跟数字，然后是冒号
    labelRaw = labelRaw.replace(/^b\d+:/, '');
    
    // 将 $br# 转换为换行符，让 renderStyledText 内部的 white-space:pre-wrap 处理
    const labelWithNewlines = labelRaw.replace(/\$br#/g, '\n');
    const cmdWithNewlines = (cmdPrefix || labelRaw).replace(/\$br#/g, '\n');
    
    const labelHtml = renderMudText(labelWithNewlines, createAnsiState(), { mode: 'dark' });
    const captionHtml = renderMudText(cmdWithNewlines, createAnsiState(), { mode: 'dark' });
    
    return {
      key: buttonId ? `${buttonId}-${stripAnsiCodes(labelRaw)}` : `${index}-${stripAnsiCodes(labelRaw)}`,
      label: stripAnsiCodes(labelRaw),
      labelHtml,
      cmd: String(cmdPrefix || '').trim(),
      caption: stripAnsiCodes(cmdPrefix || labelRaw),
      captionHtml,
      isTextInput,  // 标记是否为输入框类型
      cmdPrefix     // 保存命令前缀
    };
  });
  return { columns, items };
}

export function parseExitItems(raw) {
  return splitPairs(raw).map((entry, index) => {
    const parts = entry.split(':');
    // Common formats:
    // 1) key:label            -> parts[0]=key (command), parts[1]=label
    // 2) key:label:cmd        -> parts[0]=key, parts[1]=label, parts[2]=cmd
    // 3) label:cmd            -> parts[0]=label, parts[1]=cmd
    // We prefer using parts[0] as the command/key when sensible.
    const key = parts[0] || `exit-${index}`;
    const labelRaw = parts[1] || parts[0] || '';
    const cmd = (parts.length >= 3 && parts[2]) ? parts[2] : (parts[0] || parts[1] || '');
    // 将 $br# 转换为换行符
    const labelWithNewlines = labelRaw.replace(/\$br#/g, '\n');
    const labelHtml = renderMudText(labelWithNewlines, createAnsiState(), { mode: 'dark' });
    return {
      key,
      label: stripAnsiCodes(labelRaw),
      labelHtml,
      cmd: String(cmd || '').trim(),
      visible: true
    };
  });
}

export function parseTargetItems(raw) {
  return splitPairs(raw).map((entry, index) => {
    let working = String(entry || '');
    if (working.startsWith('$')) {
      const hash = working.indexOf('#');
      if (hash >= 0) working = working.slice(hash + 1).trim();
    }
    const parts = working.split(':').map((p) => p.trim()).filter(Boolean);
    const labelRaw = parts[0] || `目标 ${index + 1}`;
    // prefer the last field as the actual command when multiple ':' are present
    const cmd = parts.length > 1 ? parts[parts.length - 1] : parts[0] || '';
    // 将 $br# 转换为换行符
    const labelWithNewlines = labelRaw.replace(/\$br#/g, '\n');
    const cmdWithNewlines = (cmd || labelRaw).replace(/\$br#/g, '\n');
    const labelHtml = renderMudText(labelWithNewlines, createAnsiState(), { mode: 'dark' });
    const captionHtml = renderMudText(cmdWithNewlines, createAnsiState(), { mode: 'dark' });
    return {
      key: `${index}-${labelRaw}`,
      label: stripAnsiCodes(labelRaw),
      labelHtml,
      cmd: String(cmd || '').trim(),
      caption: stripAnsiCodes(cmd || labelRaw),
      captionHtml
    };
  });
}

export function parseStatusBars(raw) {
  let payload = String(raw || '');
  let columns = 2;
  let size = 35;
  
  // 解析元数据 $columns,?,sizeX,sizeY#
  if (payload.startsWith('$')) {
    const end = payload.indexOf('#');
    if (end > 0) {
      const meta = payload.slice(1, end).split(',');
      columns = Math.max(1, Number.parseInt(meta[0] || '2', 10) || 2);
      size = Number.parseInt(meta[3] || meta[2] || '35', 10) || 35;
      payload = payload.slice(end + 1);
    }
  }
  
  // 按"║"分割所有部分（包括目标信息）
  const bars = payload.split('║').filter(Boolean).map((entry, index) => {
    const parts = entry.split(':');
    const valueParts = String(parts[1] || '').split('/');
    let percentA = 0;
    let percentB = null;
    
    // 使用原始标签文本（如"气血.350"）
    const label = parts[0] || `属性 ${index + 1}`;
    
    if (valueParts.length === 3) {
      const cur = Number.parseInt(valueParts[0], 10) || 0;
      const mid = Number.parseInt(valueParts[1], 10) || cur;
      const max = Number.parseInt(valueParts[2], 10) || 1;
      percentA = Math.max(0, Math.min(100, Math.round((cur * 100) / max)));
      percentB = Math.max(0, Math.min(100, Math.round((mid * 100) / max)));
    } else if (valueParts.length === 2) {
      const cur = Number.parseInt(valueParts[0], 10) || 0;
      const max = Number.parseInt(valueParts[1], 10) || 1;
      percentA = Math.max(0, Math.min(100, Math.round((cur * 100) / max)));
      percentB = 100;
    } else {
      percentA = 100;
      percentB = 100;
    }
    
    return {
      key: `${parts[0] || 'bar'}-${index}`,
      name: parts[0] || `属性 ${index + 1}`,
      currentText: parts[1] || '',
      color: parts[2] || '#8ab4f8',
      cmd: parts[3] || '',
      percentA,
      percentB,
      label,
      columns,
      size
    };
  });
  return bars;
}

// 专门用于解析021消息（快捷菜单）
export function parseQuickCmds(raw) {
  let payload = String(raw || '');
  let columns = 4; // 默认4列
  
  // 解析元数据 $columns,rows,sizeX,sizeY#
  if (payload.startsWith('$')) {
    const end = payload.indexOf('#');
    if (end > 0) {
      const meta = payload.slice(1, end).split(',');
      columns = Math.max(1, Number.parseInt(meta[0] || '4', 10) || 4);
      payload = payload.slice(end + 1);
    }
  }
  
  const items = splitPairs(payload).map((entry, index) => {
    let working = String(entry || '').trim();
    
    // 找到第一个冒号
    const colonPos = working.indexOf(':');
    if (colonPos === -1) {
      // 没有冒号，整个作为label
      const labelRaw = working;
      // 将 $br# 转换为换行符
      const labelWithNewlines = labelRaw.replace(/\$br#/g, '\n');
      const labelHtml = renderMudText(labelWithNewlines, createAnsiState(), { mode: 'dark' });
      return {
        key: `${index}-${stripAnsiCodes(labelRaw)}`,
        label: stripAnsiCodes(labelRaw),
        labelHtml,
        cmd: '',
        html: labelHtml
      };
    }
    
    const labelRaw = working.slice(0, colonPos).trim();
    const cmd = working.slice(colonPos + 1).trim();
    
    // 将 $br# 转换为换行符
    const labelWithNewlines = labelRaw.replace(/\$br#/g, '\n');
    const labelHtml = renderMudText(labelWithNewlines, createAnsiState(), { mode: 'dark' });
    
    return {
      key: `${index}-${stripAnsiCodes(labelRaw)}`,
      label: stripAnsiCodes(labelRaw),
      labelHtml,
      cmd: cmd,
      html: labelHtml
    };
  });
  return { columns, items };
}

export function parseDialog(raw) {
  const dialog = {
    visible: true,
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
  };
  String(raw || '').split('$dh#').forEach((piece) => {
    if (!piece) return;
    if (piece.startsWith('ok11.')) {
      const cmds = piece.slice(5).split('$sock#').filter(Boolean);
      dialog.okCommands.push(...cmds);
      if (cmds.some(c => c.includes('$N'))) dialog.hasQuantity = true;
      return;
    }
    if (piece.startsWith('no11.')) {
      dialog.cancelCommand = piece.slice(5);
      return;
    }
    if (piece.startsWith('numb.')) {
      dialog.numberNeeded = true;
      dialog.blocks.push(piece.slice(5));
      return;
    }
    piece.split('$br#').forEach((block) => {
      if (!block) return;
      if (block.startsWith('$exp#')) {
        dialog.exp = block.slice(5);
        return;
      }
      if (block.startsWith('$god#')) {
        dialog.money = block.slice(5);
        return;
      }
      if (block.startsWith('$obj#')) {
        const fields = block.slice(5).split(',');
        dialog.items.push({
          key: fields[0] || `obj-${dialog.items.length}`,
          image: fields[1] ? `/assets/item/${fields[1]}.png` : '/assets/item/icon.png',
          label: fields[0] || '物品',
          bg: fields[2] || ''
        });
        return;
      }
      dialog.blocks.push(block);
    });
  });
  return dialog;
}

export function parseMapView(raw) {
  return { html: raw };
}

export function parseFloatingText(raw) {
  const text = String(raw || '');
  // 格式: "文字内容" 或 "文字内容#颜色码"
  const hashIdx = text.lastIndexOf('#');
  if (hashIdx !== -1 && hashIdx < text.length - 1) {
    const possibleColor = text.slice(hashIdx + 1);
    // 颜色码应该是短的（如 "R" 红色 或 "#ff0000" 格式）
    if (/^[a-fA-F0-9]{3,8}$/.test(possibleColor) || /^[a-zA-Z]+$/.test(possibleColor)) {
      return { text: text.slice(0, hashIdx), color: possibleColor };
    }
  }
  return { text, color: '' };
}

export function parseWebView(raw) {
  return { url: String(raw || '').trim() };
}

export function parseReconnect(raw) {
  const text = String(raw || '').trim();
  const colonIdx = text.lastIndexOf(':');
  if (colonIdx !== -1) {
    return {
      host: text.slice(0, colonIdx),
      port: text.slice(colonIdx + 1)
    };
  }
  return { host: text, port: '' };
}

export function parseCommandHistoryMode(raw) {
  const code = String(raw || '').trim();
  return { enabled: code === '998' };
}

export { ESC, escapeHtml };
