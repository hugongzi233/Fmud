const http = require('http');
const https = require('https');
const fs = require('fs');
const path = require('path');
const net = require('net');
const { URL } = require('url');
const WebSocket = require('ws');
const iconv = require('iconv-lite');
const crypto = require('crypto');

const ROOT = __dirname;
const PUBLIC_DIR = path.join(ROOT, 'public');
const DIST_DIR = path.join(ROOT, 'dist');
const PORT = process.env.PORT || 3000;
const REMOTE_BASE = process.env.MUD_REMOTE_BASE || 'https://mud1.foxmoe.top/mobi';
const LOGS_DIR = path.join(ROOT, 'logs');
if (!fs.existsSync(LOGS_DIR)) fs.mkdirSync(LOGS_DIR, { recursive: true });

function detectDecode(buf, preferred = 'utf8') {
  try {
    const s = iconv.decode(buf, preferred);
    if (s.indexOf('\uFFFD') === -1) return { text: s, encoding: preferred };
  } catch (e) {}
  const fallbacks = ['gb18030', 'gbk', 'latin1'];
  for (const enc of fallbacks) {
    try {
      const s = iconv.decode(buf, enc);
      if (s.indexOf('\uFFFD') === -1) return { text: s, encoding: enc };
    } catch (e) {}
  }
  // last resort: decode with preferred even if it contains replacement chars
  try {
    return { text: iconv.decode(buf, preferred), encoding: preferred };
  } catch (e) {
    return { text: buf.toString('utf8'), encoding: 'utf8' };
  }
}

function mimeType(filePath) {
  switch (path.extname(filePath).toLowerCase()) {
    case '.html': return 'text/html; charset=utf-8';
    case '.js': return 'application/javascript; charset=utf-8';
    case '.css': return 'text/css; charset=utf-8';
    case '.svg': return 'image/svg+xml';
    case '.png': return 'image/png';
    case '.ico': return 'image/x-icon';
    default: return 'application/octet-stream';
  }
}

function sendText(res, statusCode, contentType, body) {
  res.writeHead(statusCode, {
    'Content-Type': contentType,
    'Cache-Control': 'no-store'
  });
  res.end(body);
}

function sendJson(res, statusCode, payload) {
  sendText(res, statusCode, 'application/json; charset=utf-8', JSON.stringify(payload));
}

function readBody(req) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    req.on('data', (chunk) => chunks.push(chunk));
    req.on('end', () => resolve(Buffer.concat(chunks).toString('utf8')));
    req.on('error', reject);
  });
}

function parseBody(req, raw) {
  const type = String(req.headers['content-type'] || '').toLowerCase();
  if (type.includes('application/json')) {
    try {
      return JSON.parse(raw || '{}');
    } catch {
      return {};
    }
  }
  return Object.fromEntries(new URLSearchParams(raw || ''));
}

function requestRemote(targetUrl, method = 'GET', body = null) {
  return new Promise((resolve, reject) => {
    const parsed = new URL(targetUrl);
    const transport = parsed.protocol === 'https:' ? https : http;
    const headers = {
      'User-Agent': 'MudWeb/1.0',
      'Accept': '*/*'
    };
    let payload = null;
    if (body !== null && body !== undefined) {
      payload = typeof body === 'string' ? body : String(body);
      headers['Content-Type'] = 'application/x-www-form-urlencoded; charset=utf-8';
      headers['Content-Length'] = Buffer.byteLength(payload);
    }
    const req = transport.request({
      protocol: parsed.protocol,
      hostname: parsed.hostname,
      port: parsed.port,
      path: `${parsed.pathname}${parsed.search}`,
      method,
      headers,
      timeout: 10000
    }, (res) => {
      const chunks = [];
      res.on('data', (chunk) => chunks.push(chunk));
      res.on('end', () => resolve(Buffer.concat(chunks).toString('utf8')));
    });
    req.on('error', reject);
    req.on('timeout', () => req.destroy(new Error('Request timeout')));
    if (payload !== null) req.write(payload);
    req.end();
  });
}

function proxyText(res, url) {
  requestRemote(url)
    .then((text) => sendText(res, 200, 'text/plain; charset=utf-8', text))
    .catch((error) => sendJson(res, 502, { error: error.message }));
}

const server = http.createServer(async (req, res) => {
  const requestUrl = new URL(req.url, `http://${req.headers.host}`);

  if (requestUrl.pathname === '/api/health') {
    sendJson(res, 200, { ok: true });
    return;
  }

  if (requestUrl.pathname === '/api/version') {
    proxyText(res, `${REMOTE_BASE}/vers.php`);
    return;
  }

  if (requestUrl.pathname === '/api/login' && req.method === 'GET') {
    const id = requestUrl.searchParams.get('id') || '';
    const pass = requestUrl.searchParams.get('pass') || '';
    const page = requestUrl.searchParams.get('page') || '1';
    const key = `${id}${pass}AP4s3dF5`;
    proxyText(res, `${REMOTE_BASE}/loginto.php?id=${encodeURIComponent(id)}&pass=${encodeURIComponent(pass)}&key=${encodeURIComponent(key)}&page=${encodeURIComponent(page)}`);
    return;
  }

  if (requestUrl.pathname === '/api/register' && req.method === 'POST') {
    const raw = await readBody(req);
    const body = parseBody(req, raw);
    const id = body.id || '';
    const pass = body.pass || '';
    const phone = body.phone || '';
    const email = body.email || '';
    // 计算 MD5 key（与安卓客户端一致）
    const keyMessage = id + pass + phone + 'AP4s3dF5';
    const key = crypto.createHash('md5').update(keyMessage).digest('hex');
    proxyText(res, `${REMOTE_BASE}/reg.php?id=${encodeURIComponent(id)}&pass=${encodeURIComponent(pass)}&phone=${encodeURIComponent(phone)}&email=${encodeURIComponent(email)}&key=${encodeURIComponent(key)}`);
    return;
  }

  if (requestUrl.pathname === '/api/check') {
    const ip = requestUrl.searchParams.get('ip') || '';
    const port = requestUrl.searchParams.get('port') || '';
    const key = requestUrl.searchParams.get('key') || '';
    proxyText(res, `${REMOTE_BASE}/check.php?ip=${encodeURIComponent(ip)}&port=${encodeURIComponent(port)}&key=${encodeURIComponent(`${key}AP4s3dF5`)}`);
    return;
  }

  if (requestUrl.pathname === '/api/logOutgoing' && req.method === 'POST') {
    // write UI-sent outgoing command to a dedicated log for quick inspection
    const chunks = [];
    req.on('data', (c) => chunks.push(c));
    req.on('end', () => {
      try {
        const text = Buffer.concat(chunks).toString('utf8');
        const line = `${Date.now()} UI-OUT ${JSON.stringify(text)}\n`;
        try { fs.appendFileSync(path.join(LOGS_DIR, 'outgoing-ui.log'), line, { encoding: 'utf8' }); } catch (e) {}
      } catch (e) {}
      sendText(res, 200, 'text/plain; charset=utf-8', 'ok');
    });
    req.on('error', () => sendText(res, 500, 'text/plain; charset=utf-8', 'error'));
    return;
  }

  if (requestUrl.pathname === '/vue.global.prod.js') {
    try {
      const vueRuntime = require.resolve('vue/dist/vue.global.prod.js');
      fs.readFile(vueRuntime, (error, data) => {
        if (error) {
          sendText(res, 404, 'text/plain; charset=utf-8', 'Vue runtime not found');
          return;
        }
        sendText(res, 200, 'application/javascript; charset=utf-8', data);
      });
    } catch (error) {
      sendText(res, 404, 'text/plain; charset=utf-8', 'Vue runtime not installed');
    }
    return;
  }

  const staticRoot = fs.existsSync(path.join(DIST_DIR, 'index.html')) ? DIST_DIR : PUBLIC_DIR;

  let filePath = requestUrl.pathname;
  if (filePath === '/') filePath = '/index.html';
  const absolutePath = path.join(staticRoot, filePath);
  if (!absolutePath.startsWith(staticRoot)) {
    sendText(res, 403, 'text/plain; charset=utf-8', 'Forbidden');
    return;
  }

  fs.readFile(absolutePath, (error, data) => {
    if (error) {
      sendText(res, 404, 'text/plain; charset=utf-8', 'Not found');
      return;
    }
    sendText(res, 200, mimeType(absolutePath), data);
  });
});

const wss = new WebSocket.Server({ server });

wss.on('connection', (ws) => {
  ws.tcp = null;
  ws.encoding = 'utf8';

  const closeTcp = () => {
    if (ws.tcp) {
      ws.tcp.destroy();
      ws.tcp = null;
    }
  };

  ws.on('message', (raw) => {
    let message;
    try {
      message = JSON.parse(raw.toString());
    } catch {
      return;
    }

    if (message.action === 'connect') {
      closeTcp();
      const host = message.host || '127.0.0.1';
      const port = Number.parseInt(message.port, 10) || 23;
      ws.encoding = message.encoding || 'utf8';
      const socket = new net.Socket();
      ws.tcp = socket;
      let pending = Buffer.alloc(0);
      // prepare a raw binary log per connection to help debug encoding/framing
      let rawLog = null;
      let outLog = null;
      try {
        const safeHost = host.replace(/[:\\/]/g, '_');
        rawLog = fs.createWriteStream(path.join(LOGS_DIR, `raw-${safeHost}-${port}-${Date.now()}.bin`));
        try {
          outLog = fs.createWriteStream(path.join(LOGS_DIR, `out-${safeHost}-${port}-${Date.now()}.log`), { encoding: 'utf8' });
        } catch (e) { outLog = null; }
      } catch (e) {
        rawLog = null;
      }
      socket.setKeepAlive(true);
      socket.setNoDelay(true);
      socket.connect(port, host, () => {
        ws.send(JSON.stringify({ type: 'status', status: 'connected', host, port }));
      });
      socket.on('data', (chunk) => {
        // write raw bytes for inspection
        try { if (rawLog) rawLog.write(chunk); } catch (e) {}
        
        pending = Buffer.concat([pending, chunk]);
        let lineBreak = pending.indexOf(0x0a);
        while (lineBreak !== -1) {
          const lineBuffer = pending.slice(0, lineBreak);
          pending = pending.slice(lineBreak + 1);
          try {
            const detected = detectDecode(lineBuffer, ws.encoding);
            let text = detected.text.replace(/\r$/, '');
            
            ws.send(JSON.stringify({ type: 'data', text, raw: lineBuffer.toString('base64'), encoding: detected.encoding }));
          } catch (err) {
            const text = lineBuffer.toString('utf8').replace(/\r$/, '');
            ws.send(JSON.stringify({ type: 'data', text, raw: lineBuffer.toString('base64'), encoding: 'utf8' }));
          }
          lineBreak = pending.indexOf(0x0a);
        }
      });
      socket.on('close', () => {
        try { if (rawLog) rawLog.end(); } catch (e) {}
        if (pending.length) {
          try {
            const detected = detectDecode(pending, ws.encoding);
            const text = detected.text.replace(/\r$/, '');
            if (text) ws.send(JSON.stringify({ type: 'data', text, raw: pending.toString('base64'), encoding: detected.encoding }));
          } catch {
            const text = pending.toString('utf8').replace(/\r$/, '');
            if (text) ws.send(JSON.stringify({ type: 'data', text, raw: pending.toString('base64'), encoding: 'utf8' }));
          }
        }
        ws.send(JSON.stringify({ type: 'status', status: 'closed' }));
        ws.tcp = null;
      });
      socket.on('error', (error) => {
        ws.send(JSON.stringify({ type: 'status', status: 'error', message: error.message }));
        closeTcp();
      });
      return;
    }

    if (message.action === 'input') {
      if (!ws.tcp || ws.tcp.destroyed) return;
      const text = String(message.text || '');
      let buffer;
      try {
        buffer = iconv.encode(text, ws.encoding);
      } catch {
        buffer = Buffer.from(text, 'utf8');
      }
      // Log outgoing (client -> MUD) data both as base64 and decoded text for debugging
      try {
        const b64 = buffer.toString('base64');
        let decoded = text;
        try { decoded = iconv.decode(buffer, ws.encoding); } catch (e) { decoded = text; }
        const line = `${Date.now()} OUT-B64 ${b64} OUT-TEXT ${JSON.stringify(decoded)}\n`;
        try {
          if (typeof outLog !== 'undefined' && outLog) {
            outLog.write(line);
          }
        } catch (e) {
          console.error('failed to write outLog', e && e.message ? e.message : e);
        }
        // also log to server console for quick inspection
        console.log(`[OUT -> ${ws.encoding}]`, decoded);
      } catch (e) { console.error('failed to write outLog', e.message); }
      ws.tcp.write(buffer);
      return;
    }

    if (message.action === 'disconnect') {
      closeTcp();
      ws.send(JSON.stringify({ type: 'status', status: 'closed' }));
    }
  });

  ws.on('close', closeTcp);
});

server.listen(PORT, () => {
  console.log(`Mud web server running at http://localhost:${PORT}`);
});
