# MUD Web 客户端

基于 Vue 3 + Vite 的网页版 MUD 游戏客户端，现已完全模仿安卓客户端的GUI布局。

## 📋 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [核心功能](#核心功能)
- [协议解析](#协议解析)
- [UI布局设计](#ui布局设计)
- [常见问题](#常见问题)
- [开发规范](#开发规范)
- [已知问题与修复记录](#已知问题与修复记录)

---

## 项目概述

这是一个网页版的MUD（Multi-User Dungeon）游戏客户端，完全复刻了Android原生客户端的界面布局和功能。项目采用Vue 3作为前端框架，通过WebSocket代理服务器连接到MUD游戏服务器，实现了完整的文本MUD游戏体验。

### 主要特性

-  **完整复刻Android客户端UI**：严格参照原生客户端XML布局进行复刻
-  **实时WebSocket通信**：支持二进制数据传输和协议解析
-  **进度条式状态栏**：可视化显示气血、内力、精神等状态
-  **多标签聊天系统**：主界面、信息、聊天、战斗四个标签页
-  **动态命令系统**：快捷命令、自定义命令、出口方向按钮
-  **响应式设计**：适配不同屏幕尺寸

---

## 技术栈

### 前端
- **Vue 3**: 渐进式JavaScript框架
- **Vite**: 下一代前端构建工具
- **Vue Composition API**: 组合式API
- **CSS3**: 现代样式和动画效果

### 后端代理
- **Node.js**: JavaScript运行时
- **ws**: WebSocket库
- **iconv-lite**: 字符编码转换（支持UTF-8、GBK、GB18030）
- **http/https**: HTTP代理模块

### 开发工具
- **ESLint**: 代码质量检查
- **Prettier**: 代码格式化（可选）

---

## 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 7.0.0

### 安装依赖

```bash
cd d:\WebProject\Fmud\mud-web
npm install
```

### 启动服务器

#### 方式一：生产模式（推荐）

```bash
npm start
```

访问 http://localhost:3000

#### 方式二：开发模式

**终端1 - 启动API服务器：**
```bash
npm start
```

**终端2 - 启动Vite开发服务器：**
```bash
npm run dev
```

访问 http://localhost:5173

> **注意**：开发模式下，Vite会自动代理 `/api/*` 请求到 `http://localhost:3000`

### 构建生产版本

```bash
npm run build
```

构建产物位于 `dist/` 目录。

---

## 项目结构

```
mud-web/
├── public/                    # 静态资源
│   ├── js/                   # 核心JavaScript文件
│   │   ├── main.js          # 主应用逻辑（715行）
│   │   ├── protocol.js      # 协议解析器（396行）
│   │   └── store.js         # 本地存储管理
│   ├── styles/              # 全局样式
│   │   └── app.css         # 应用样式（25.4KB）
│   └── index.html          # HTML入口
├── src/                     # Vue源代码
│   ├── components/         # Vue组件
│   │   ├── GameScreen.vue     # 游戏主界面
│   │   ├── HomeScreen.vue     # 登录界面
│   │   ├── StatusBars.vue     # 状态栏组件
│   │   ├── ChatInput.vue      # 聊天输入框
│   │   ├── CommandInput.vue   # 命令输入框
│   │   ├── ExitButtons.vue    # 出口方向按钮
│   │   ├── ObjectList.vue     # 对象列表
│   │   ├── QuickCmds.vue      # 快捷命令
│   │   ├── GameDialogs.vue    # 游戏对话框
│   │   └── AccountDialogs.vue # 账户对话框
│   ├── App.vue             # 根组件
│   └── main.js             # Vue入口
├── server.js               # Node.js代理服务器（355行）
├── vite.config.js          # Vite配置
├── package.json            # 项目配置
└── README.md               # 项目文档
```

---

## 核心功能

### 1. WebSocket代理服务器 (server.js)

**职责**：
- 接收前端WebSocket连接
- 转发请求到MUD游戏服务器
- 处理二进制数据的解码和编码转换
- 按行分割数据并转发给前端

**关键逻辑**：
```javascript
// 数据接收和处理
socket.on('data', (chunk) => {
  pending = Buffer.concat([pending, chunk]);
  let lineBreak = pending.indexOf(0x0a); // 按换行符分割
  while (lineBreak !== -1) {
    const lineBuffer = pending.slice(0, lineBreak);
    const detected = detectDecode(lineBuffer, ws.encoding);
    ws.send(JSON.stringify({ 
      type: 'data', 
      text: detected.text,
      encoding: detected.encoding 
    }));
  }
});
```

**支持的编码**：
- UTF-8（默认）
- GBK
- GB18030
- Latin1

### 2. 协议解析器 (protocol.js)

**职责**：解析MUD服务器的控制序列消息

**支持的消息类型**：
- `002`: 房间名称和描述
- `003`: 出口方向
- `004`: 场景描述
- `005`: 目标对象列表
- `006`: 自定义命令菜单
- `012`: **状态栏数据**（气血、内力等）
- `021`: 快捷命令菜单
- `022`: 目标栏数据

**关键函数**：
```javascript
// 解析状态栏数据（012消息）
export function parseStatusBars(payload) {
  // 格式: $列数,行数,宽度,高度#项1:#颜色║项2:#颜色...
  // 示例: $6,6,20,40#小狐:100/100:#990066FF║气血.350:350/350/350:#99FF0000
}
```

### 3. 主应用逻辑 (main.js)

**核心方法**：

#### handleSocketMessage(raw)
处理从服务器接收到的WebSocket消息
```javascript
handleSocketMessage(raw) {
  let message;
  try { message = JSON.parse(raw); } catch { return; }
  if (message.type === 'data' && message.text) {
    this.processIncomingText(message.text);
  }
}
```

#### processIncomingText(text)
解析文本中的控制序列
```javascript
processIncomingText(text) {
  // 检测ESC控制序列: \u001b + 3位代码
  if (text.includes('\u001b')) {
    const codeMatch = rest.match(/^\u001b(\d{3})/);
    if (codeMatch) {
      this.handleControlMessage(text);
    }
  }
}
```

#### handleControlMessage(text)
分发控制消息到对应的处理函数
```javascript
handleControlMessage(text) {
  const code = text.slice(1, 4);
  const payload = text.slice(4);
  switch (code) {
    case '012':
      this.statusBars = parseStatusBars(payload);
      break;
    case '022':
      // 目标栏数据处理
      break;
  }
}
```

---

## 协议解析

### MUD控制序列格式

#### ESC前缀格式（标准）
```
\u001b + 3位代码 + payload
示例: \u001b012$6,6,20,40#气血.350:...
```

#### 零前缀格式（兼容）
```
0{3,} + 3位代码 + payload
示例: 0000012$6,6,20,40#气血.350:...
```

### 012消息（状态栏）格式详解

**原始数据**：
```
\u001b012$6,6,20,40#小狐:100/100:#990066FF║气血.350:350/350/350:#99FF0000:exert recover║内力.1000:1000/1000/1586:#990066FF║精神.433:433/433/433:#996600CC:exert regenerate║精力.1000:1000/1000/260:#99006600║忙乱.0:100/100:#990066FF║怒气.0:0/7000:#99990000║经验.136331:136331/136331:#99FF0066║潜能.253700:253700/3646300/3900000:#99FF00FF║实战.5387:5387/200059/200059:#99FF00FF║储潜.0:0/100:#990066FF║活跃.0:0/100:#990066FF
```

**字段说明**：
- `$6,6,20,40`: 布局参数（列数=6, 行数=6, 宽度=20, 高度=40）
- `#`: 项分隔符
- `║`: 子项分隔符
- `小狐:100/100:#990066FF`: 项名:当前值/最大值:颜色
- `气血.350:350/350/350:#99FF0000:exert recover`: 名称.当前值:当前/最大/上限:颜色:快捷命令

**解析结果**：
```javascript
[
  { key: 'target-0', label: '小狐:100/100', value: 100, color: '#990066FF' },
  { key: 'hp', label: '气血.350', current: 350, max: 350, limit: 350, color: '#99FF0000', cmd: 'exert recover' },
  { key: 'mp', label: '内力.1000', current: 1000, max: 1000, limit: 1586, color: '#990066FF' },
  // ... 更多状态项
]
```

---

## UI布局设计



### 进度条式状态栏实现

**三层叠加结构**：
1. **背景层C**: 透明底层容器
2. **总宽度背景B**: 深色背景表示最大值范围
3. **当前值进度条A**: 彩色填充，从左到右显示当前值比例

**样式规范**：
```css
.status-bar-item {
  position: relative;
  height: 24px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 4px;
  overflow: hidden;
}

.status-bar-fill
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  transition: width 0.3s ease; /* 平滑过渡动画 */
}

.status-bar-label {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
  font-size: 12px;
  color: white;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.8);
}
```

**颜色映射**：
- 气血: `#FF0000` (红色)
- 内力: `#0066FF` (蓝色)
- 精神: `#6600CC` (紫色)
- 精力: `#006600` (绿色)
- 其他: `#0066FF` (默认蓝色)

---

## 常见问题

### Q1: 为什么看不到状态栏？

**可能原因**：
1. 服务器没有发送012消息
2. 协议解析失败
3. 前端代码错误

**排查步骤**：
1. 打开浏览器控制台（F12）
2. 查看是否有`[012消息] 状态栏数据:`日志
3. 检查`mud.statusBars`数组是否为空
4. 确认StatusBars.vue组件是否正确渲染

### Q2: 中文显示乱码？

**解决方案**：
1. 检查服务器设置的编码是否正确
2. 在设置中切换编码（UTF-8 / GBK / GB18030）
3. 查看server.js的detectDecode函数日志

### Q3: WebSocket连接失败？

**检查清单**：
- [ ] server.js是否正常运行
- [ ] 端口3000是否被占用
- [ ] 防火墙是否阻止连接
- [ ] MUD服务器地址是否正确

### Q4: 构建失败或文件被截断？

**原因**：大型核心文件（如main.js）在编辑时可能因会话长度限制被意外截断

**解决方案**：
1. 检查文件行数：`wc -l public/js/main.js`
2. 验证语法完整性：`node -c public/js/main.js`
3. 重新构建：`npm run build`
4. 如有必要，从备份恢复文件

---

## 开发规范

### 代码结构要求

1. **组件拆分**: 大型界面组件应拆分为独立的子组件，避免单文件过大
2. **避免硬编码**: 禁止在代码中硬编码UI布局参数或业务逻辑值
3. **数据驱动**: 使用动态配置和数据驱动方式实现界面行为

### 布局复刻原则

- Web端界面布局应严格参照原生客户端（如Android）的源码和XML布局进行复刻
- 确保结构一致，而非仅做简单的响应式适配

### 调试原则

1. **禁止盲目修改**: 在定位问题原因前，不要随意修改代码逻辑
2. **收集证据**: 必须确认具体表现，并获取浏览器控制台错误信息和最新日志文件内容
3. **精准定位**: 基于日志和错误信息分析根本原因，而非猜测

### 文件编辑规范

1. **构建前检查**: 在运行构建命令前，必须检查被编辑文件的语法完整性
2. **防止截断**: 编辑大型脚本文件时，需确认文件未被意外截断
3. **错误排查**: 遇到构建报错时，优先检查最近修改的文件是否存在语法结构问题

---

## 已知问题与修复记录

### 问题1: 状态栏完全不显示（已修复）

**时间**: 2026-05-02

**症状**：
- 浏览器控制台没有`[012消息]`日志
- `mud.statusBars`数组为空
- StatusBars组件不渲染

**根本原因**：
1. **第一阶段**：022消息处理逻辑错误地覆盖了statusBars数组
   - 修复：移除updateTargetBars中对statusBars的修改
   
2. **第二阶段**：main.js文件被意外截断至313行（原719行）
   - 导致handleControlMessage函数缺失
   - 012消息无法被处理

**修复方案**：
1. 恢复完整的main.js文件（715行）
2. 确保handleControlMessage包含012消息处理：
   ```javascript
   case '012':
     this.statusBars = parseStatusBars(payload);
     console.log('[012消息] 状态栏数据:', payload.substring(0, 100));
     break;
   ```
3. 重新构建并重启服务器

**验证**：
-  控制台显示`[012消息] 状态栏数据:`日志
-  `mud.statusBars`数组包含12个状态项
-  StatusBars组件正确渲染进度条

### 问题2: ESC控制字符丢失（已修复）

**时间**: 2026-05-02

**症状**：
- server.js接收到`\x1b012`但转发后变成`12`
- 前端无法识别控制序列

**根本原因**：
- server.js的日志输出被截断，误以为ESC字符丢失
- 实际上server.js正确转发了完整数据

**修复方案**：
- 清理调试日志，避免误导
- 确认WebSocket传输完整性

### 问题3: 协议解析器未调用（已修复）

**时间**: 2026-05-02

**症状**：
- 012消息到达前端但未被解析

**根本原因**：
- main.js文件截断导致processIncomingText和handleControlMessage函数缺失

**修复方案**：
- 恢复完整的协议解析逻辑
- 添加调试日志确认消息流向

---

## 日志文件说明

### 日志位置
```
mud-web/logs/
├── raw-{host}-{port}-{timestamp}.bin  # 原始二进制数据
├── out-{host}-{port}-{timestamp}.log  # 解码后的文本日志
```

### 查看原始日志
```bash
# Python查看十六进制
python -c "with open('logs/raw-xxx.bin', 'rb') as f: data = f.read(); print(data[:100].hex())"

# 查找特定消息
python -c "with open('logs/raw-xxx.bin', 'rb') as f: data = f.read(); count = data.count(b'\x1b012'); print(f'012消息数量: {count}')"
```

---

## 贡献指南

欢迎提交Issue和Pull Request！

### 提交Issue
- 详细描述问题现象
- 提供浏览器控制台错误信息
- 附上相关日志文件内容

### 提交PR
- 遵循现有代码风格
- 添加必要的注释
- 确保构建无错误

---

## 许可证

本项目仅供学习和研究使用。

---

## 联系方式

如有问题，请提交Issue或联系项目维护者。

---

**最后更新**: 2026-05-02  
**版本**: 1.0.0  
**维护者**: MUD Web Team