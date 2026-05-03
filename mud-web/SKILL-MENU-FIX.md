# 技能菜单弹窗功能修复

## 问题描述

点击技能列表中的技能项时,应该弹出二级菜单(包含激发、准备、研究、查看、放弃等操作),但实际没有看到弹窗,且发送的命令不正确。

## 根本原因

根据安卓客户端源码分析,ESC 009消息的处理流程应该是:

1. **第一层**:显示技能列表(如"基本内功"、"战神决"等)
2. **第二层**:点击某个技能后,检测到命令中包含 `\u001b020` 前缀,调用 `takepopmenu()` 弹出二级菜单
3. **第三层**:二级菜单中显示该技能的多个操作选项(skills/prepare/yanjiu/checkskill/abandon)

**Web端的问题:**
- 协议解析时,`\u001b020` 前缀被直接移除,导致无法识别需要弹出菜单
- 点击技能项时,直接发送普通命令而不是触发弹窗

## 修复方案

### 1. 修改协议解析逻辑 (`public/js/protocol.js`)

在 `parseActionItems` 函数中:
- 检测命令是否包含 `\u001b020` 前缀
- 保留前缀信息到 `hasEscMenu` 和 `escPayload` 字段
- 不直接移除前缀,而是保存原始payload用于后续弹窗处理

```
// 在函数顶部声明变量,确保在所有分支中都可用
let hasEscPrefix = false;
let escPayload = '';

if (cmdPrefix.startsWith('\u001b')) {
  const controlCode = cmdPrefix.slice(1, 4);
  if (controlCode === '020') {
    hasEscPrefix = true;
    escPayload = cmdPrefix.slice(4).trim();
  }
}

return {
  // ... 其他字段
  hasEscMenu: hasEscPrefix || false,  // 标记是否有ESC菜单
  escPayload: hasEscPrefix ? escPayload : ''  // 保存ESC菜单的payload
};
```

**重要**: 变量必须在函数作用域顶部声明,不能在 `if/else` 分支内部声明,否则会导致 ReferenceError。

### 2. 添加ESC菜单处理方法 (`public/js/main.js`)

在mud对象中添加 `handleEscMenu` 方法:

```
handleEscMenu(payload) {
  // 处理ESC 020菜单,使用现有的GUI弹窗机制
  const parsed = parseActionItems(payload);
  this.showGui = true;           // ✅ 触发GUI弹窗显示
  this.guiColumns = parsed.columns || 3;
  this.guiActions1 = parsed.items;  // ✅ 设置按钮数据
  this.guiTab = 'actions';       // ✅ 切换到actions标签
}
```

### 3. 修改点击处理逻辑 (`src/components/GameScreen.vue` 和 `GameDialogs.vue`)

在 `handleActionItemClick` 函数中:
- 检测 `item.hasEscMenu` 标记
- 如果存在,调用 `mud.handleEscMenu(item.escPayload)` 触发弹窗
- 否则按原有逻辑发送命令

```
const handleActionItemClick = (item) => {
  // 检查是否有ESC菜单标记
  if (item.hasEscMenu && item.escPayload) {
    console.log('检测到ESC菜单,触发弹窗:', item.escPayload);
    mud.handleEscMenu(item.escPayload);
    return;
  }
  
  if (item.cmd) {
    mud.sendCommand(item.cmd);
  }
};
```

## Bug修复记录

### 2026-05-03: 修复变量作用域导致的ReferenceError

**问题**: 一进入游戏就弹出两个弹窗(自定义命令配置和聊天室),且关不掉。

**原因**: 在 `parseActionItems` 函数中,`hasEscPrefix` 和 `escPayload` 变量只在 `else` 分支中定义,当走 `if (txtMarkerIndex !== -1)` 分支时,这两个变量未定义,导致访问时报错 `ReferenceError: hasEscPrefix is not defined`。

**影响**: 
- 所有包含 `$txt#` 的消息(如输入框)都会触发错误
- 错误导致后续代码无法执行,可能影响其他UI状态的初始化
- 用户看到的"弹窗关不掉"可能是由于JavaScript错误导致事件处理中断

**修复**: 将 `hasEscPrefix` 和 `escPayload` 变量的声明移到函数作用域顶部,确保在所有分支中都可访问。

```
// ❌ 错误的写法 - 变量在分支内部定义
if (txtMarkerIndex !== -1) {
  // ... 这里没有定义 hasEscPrefix
} else {
  let hasEscPrefix = false;  // 只在这个分支可用
  let escPayload = '';
}
// 下面使用时会报错
hasEscMenu: hasEscPrefix || false  // ReferenceError!

// ✅ 正确的写法 - 变量在函数顶部定义
let hasEscPrefix = false;  // 所有分支都可用
let escPayload = '';

if (txtMarkerIndex !== -1) {
  // ... 可以使用 hasEscPrefix
} else {
  // ... 也可以修改 hasEscPrefix
  hasEscPrefix = true;
}
// 安全使用
hasEscMenu: hasEscPrefix || false
```

### 2026-05-03: 修复ESC 020菜单项发送错误命令的问题

**问题**: 点击技能列表中的技能项(如"基本拳脚")后,没有弹出二级菜单,而是直接发送了错误的命令,导致服务器返回"什么?"。

**错误命令示例**:
```
[OUT -> utf8] 向独孤求败学习基本拳脚|learn dugu qiubai unarmed
```

**正确行为**: 应该先弹出二级菜单,显示"向独孤求败学习基本拳脚"等按钮,点击按钮后才发送 `learn dugu qiubai unarmed`。

**根本原因**: 
在 `parseActionItems` 函数中,当检测到 `\u001b020` 前缀时:
1. 提取了 `escPayload` = "练习基本拳脚|lian unarmed$z2#研究基本拳脚|yanjiu unarmed$z2#..."
2. **错误地**将 `cmdPrefix` 设置成了 `escPayload`
3. 返回对象时,`cmd` 字段被设置为 `String(cmdPrefix || '').trim()`,即整个 escPayload 字符串
4. 点击技能项时,发送了整个 payload 字符串作为命令,而不是触发弹窗

**修复方案**:
1. 当有 ESC 020 标记时,将 `cmd` 字段设为空字符串,避免直接发送命令
2. 将 `caption` 改为 "(点击展开)",提示用户可以点击
3. 清空 `cmdPrefix`,确保不会误发送

```
return {
  // ... 其他字段
  // 如果有ESC菜单标记,cmd设为空,避免直接发送命令
  cmd: hasEscPrefix ? '' : String(cmdPrefix || '').trim(),
  caption: stripAnsiCodes(hasEscPrefix ? '(点击展开)' : (cmdPrefix || labelRaw)),
  captionHtml: hasEscPrefix ? renderMudText('(点击展开)', createAnsiState(), { mode: 'dark' }) : captionHtml,
  cmdPrefix: hasEscPrefix ? '' : cmdPrefix,     // 有ESC菜单时清空cmdPrefix
  hasEscMenu: hasEscPrefix || false,
  escPayload: hasEscPrefix ? escPayload : ''
};
```

**验证逻辑**:
1. 点击技能项 → 检测到 `hasEscMenu === true` → 调用 `handleEscMenu(escPayload)` → 弹出二级菜单
2. 点击二级菜单中的按钮 → 发送正确的命令(如 `learn dugu qiubai unarmed`)

### 2026-05-03: 修复handleGuiActionClick未处理ESC菜单的问题

**问题**: 点击技能按钮后没有出现任何弹窗,命令也没有任何输出,控制台也没有报错。

**根本原因**: 
在 [GameDialogs.vue](file://d:\WebProject\Fmud\mud-web\src\components\GameDialogs.vue) 组件中,有两个不同的点击处理函数:
1. **[handleActionItemClick](file://d:\WebProject\Fmud\mud-web\src\components\GameDialogs.vue#L154-L169)** - 用于动作块弹窗中的按钮(已修复)
2. **[handleGuiActionClick](file://d:\WebProject\Fmud\mud-web\src\components\GameDialogs.vue#L195-L210)** - 用于GUI弹窗中的按钮(**未修复**)

ESC 009消息的按钮被渲染在GUI弹窗中(`safeMud.showGui`),使用的是 [handleGuiActionClick](file://d:\WebProject\Fmud\mud-web\src\components\GameDialogs.vue#L195-L210)。但该函数没有检测 `hasEscMenu` 标记,直接尝试发送 `item.cmd`。由于之前修复时将 ESC 020 标记项的 [cmd](file://d:\WebProject\Fmud\mud-web\src\components\GameScreen.vue#L261-L261) 设为空字符串,导致点击后没有任何反应。

**修复方案**:
给 [handleGuiActionClick](file://d:\WebProject\Fmud\mud-web\src\components\GameDialogs.vue#L195-L210) 添加与 [handleActionItemClick](file://d:\WebProject\Fmud\mud-web\src\components\GameDialogs.vue#L154-L169) 相同的ESC菜单检测逻辑:

``javascript
const handleGuiActionClick = (item) => {
  // 检查是否有ESC菜单标记
  if (item.hasEscMenu && item.escPayload) {
    // 触发弹窗而不是发送命令
    console.log('检测到ESC菜单,触发弹窗:', item.escPayload);
    safeMud.handleEscMenu(item.escPayload);
    safeMud.closeGui();
    return;
  }
  
  if (item.cmd) {
    safeMud.sendCommand(item.cmd);
  }
  safeMud.closeGui();
};
```

**关键要点**:
- Web端有多个点击处理函数,必须确保所有函数都正确处理 ESC 菜单标记
- GUI弹窗和动作块弹窗使用不同的处理函数,需要分别修复
- 保持一致性:所有点击处理函数都应该先检测 `hasEscMenu`,再决定是否发送命令

### 2026-05-03: 修复popup状态未渲染导致弹窗不显示的问题

**问题**: 点击技能按钮后仍然没有任何弹窗显示,控制台也没有输出。

**根本原因**: 
[handleEscMenu](file://d:\WebProject\Fmud\mud-web\public\js\main.js#L1329-L1335) 方法最初使用了 `this.popup` 状态来显示弹窗:
```javascript
handleEscMenu(payload) {
  const parsed = parseActionItems(payload);
  this.popup = { visible: true, title: '快捷菜单', items: parsed.items, columns: parsed.columns || 3 };
}
```

但问题是:**Vue 模板中根本没有渲染 `popup` 状态的代码**!虽然 [popup](file://d:\WebProject\Fmud\mud-web\public\js\main.js#L137-L142) 在 data 中定义了,但没有对应的组件使用它,所以即使设置了 `visible: true`,页面上也不会显示任何内容。

**修复方案**:
修改 [handleEscMenu](file://d:\WebProject\Fmud\mud-web\public\js\main.js#L1329-L1335) 方法,使用现有的 GUI 弹窗机制(`showGui`、`guiActions1`、`guiColumns`),这些状态在 [GameDialogs.vue](file://d:\WebProject\Fmud\mud-web\src\components\GameDialogs.vue) 中有完整的渲染逻辑:

```javascript
handleEscMenu(payload) {
  // 处理ESC 020菜单,使用现有的GUI弹窗机制
  const parsed = parseActionItems(payload);
  this.showGui = true;           // ✅ 触发GUI弹窗显示
  this.guiColumns = parsed.columns || 3;
  this.guiActions1 = parsed.items;  // ✅ 设置按钮数据
  this.guiTab = 'actions';       // ✅ 切换到actions标签
}
```

**关键要点**:
- Web端已有成熟的 GUI 弹窗机制(用于 ESC 007/008/009),应复用而不是创建新的弹窗状态
- 新增功能时应优先检查是否有现成的 UI 组件可以复用
- 避免创建未被模板使用的响应式状态,这会导致"静默失败"(状态改变但UI无反应)

**验证方法**:
1. 打开浏览器控制台(F12)
2. 输入 `skills` 查看技能列表
3. 点击任意技能项(如"基本拳脚")
4. 应该看到 GUI 弹窗弹出,显示二级菜单选项

## 测试验证

### 原始数据格式
```
ESC 009 $3,3,12,35# 基本内功$br#$br# 101级/0%:\u001b020练习基本内功|lian force$z2#研究基本内功|yanjiu force$z2#...
```

### 预期行为
1. 收到ESC 009消息,显示技能列表
2. 点击"基本内功",检测到 `\u001b020` 前缀
3. 弹出二级菜单,显示"练习基本内功"、"研究基本内功"等选项
4. 点击二级菜单中的选项,发送对应命令(如 `lian force`)

### 调试建议
打开浏览器控制台,点击技能项时应看到日志:
```
检测到ESC菜单,触发弹窗: 练习基本内功|lian force$z2#研究基本内功|yanjiu force$z2#...
```

如果没有看到弹窗,请检查:
1. 控制台是否有 `ReferenceError` 或其他错误
2. 原始消息中是否包含 `\u001b020` 前缀
3. [popup.visible](file://d:\WebProject\Fmud\mud-web\public\js\main.js#L56-L56) 状态是否正确设置为 `true`

## 其他ESC消息处理

根据安卓客户端源码,以下ESC消息也需要正确处理:

| ESC码 | 用途 | Web端处理 | 实现位置 |
|-------|------|----------|---------|
| ESC 013 | 显示更多文本 | ✅ 已实现 (showMoreText) | main.js L1187-1189 |
| ESC 014 | 直接发送命令 | ✅ 已添加 | main.js L1190-1194 |
| ESC 020 | 弹出快捷菜单 | ✅ 已修复 | main.js L1207-1211 |
| ESC 021 | 更新快捷命令 | ✅ 已实现 (quickCmds) | main.js L1212-1215 |
| ESC 022 | 更新目标气血条 | ✅ 已实现 (updateTargetBars) | main.js L1217-1218 |
| ESC 023 | 控制描述显示 | ✅ 已实现 (storyVisible) | main.js L1220-1221 |
| ESC 024 | 浮动文字 | ✅ 已实现 (spawnFloatingText) | main.js L1223-1224 |

### ESC 014 补充说明

**用途**: 服务器主动触发客户端执行某个命令(无需用户交互)

**安卓实现**: `mudsocketd.mudout(mudmaind.this.content.substring(4));`

**Web端实现**:
```
case '014':
  // 直接发送命令(ESC 014)
  if (payload && payload.trim()) {
    this.sendCommand(payload.trim());
  }
  return;
```

**使用场景**: 
- 自动执行某些系统命令
- 强制客户端刷新状态
- 后台任务触发

## 相关文件

- `public/js/protocol.js` - 协议解析
- `public/js/main.js` - mud对象方法
- `src/components/GameScreen.vue` - 游戏界面组件
- `src/components/GameDialogs.vue` - 对话框组件
- `MudZJutf8/app/src/main/java/com/zwyouto/main/mudmaind.java` - 安卓客户端参考源码

## 注意事项

1. **不要移除ESC前缀**:之前的实现错误地移除了 `\u001b020` 前缀,导致无法识别需要弹窗
2. **保持向后兼容**:对于没有ESC前缀的命令,仍然按原有逻辑处理
3. **变量作用域**:在条件分支中使用变量时,必须在函数顶部声明,避免ReferenceError
4. **弹窗关闭**:点击弹窗中的按钮后,会自动关闭弹窗并发送命令

## 更新日志

- 2026-05-03: 修复技能菜单弹窗功能,正确识别和处理 `\u001b020` 前缀
- 2026-05-03: 修复变量作用域问题,解决ReferenceError导致的弹窗异常
