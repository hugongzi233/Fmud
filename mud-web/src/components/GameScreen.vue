  <template>
  <div class="mud-pc-screen" v-if="mud && mud.screen !== 'home'">
    <!-- 左侧边栏 -->
    <div class="pc-left-sidebar">
      <!-- 对象列表 -->
      <div class="pc-objects-section" v-if="mud.targets && mud.targets.length > 0">
        <button class="obj-btn" v-for="target in mud.targets" :key="target.key" @click="handleObjectClick(target)">
          <span v-html="target.labelHtml"></span>
        </button>
      </div>
      
      <!-- 左下角：其他出口 -->
      <div class="pc-left-bottom">
        <div class="custom-exits-section" v-if="customExits.length > 0">
          <button class="obj-btn" v-for="exit in customExits" :key="exit.key" @click="mud.sendCommand(exit.cmd)">
            <span v-html="exit.labelHtml"></span>
          </button>
        </div>
      </div>
    </div>

    <!-- 中间主信息区 -->
    <div class="pc-left-main">
      <!-- 顶部标题栏 -->
      <div class="pc-top-bar">
        <div class="pc-location-name" v-html="mud.locationName || '当前位置'"></div>
        <!-- 快捷菜单按钮（021消息）在位置名称右侧 -->
        <div class="pc-dialog-btns" v-if="mud.quickCmds && mud.quickCmds.length > 0">
          <button 
            class="pc-dialog-btn" 
            v-for="cmd in mud.quickCmds" 
            :key="cmd.key" 
            @click="mud.sendCommand(cmd.cmd)"
          >
            <span v-html="cmd.html || cmd.label || cmd.cmd"></span>
          </button>
        </div>
        <div class="pc-top-actions">
          <button class="pc-top-btn" @click="mud.toggleStory" :title="mud.storyVisible ? '收起描述' : '展开描述'">
            {{ mud.storyVisible ? '-' : '+' }}
          </button>
        </div>
      </div>

      <!-- 故事描述区 -->
      <div class="pc-story-area" :class="{ hidden: !mud.storyVisible }">
        <div class="pc-story-text" v-html="mud.storyHtml"></div>
      </div>

      <!-- 主信息区 -->
      <div class="pc-main-feed">
        <!-- 标签页切换 -->
        <div class="pc-tabs">
          <button class="pc-tab" :class="{ active: mud.feedTab === 'main' }" @click="mud.feedTab = 'main'">主界面</button>
          <button class="pc-tab" :class="{ active: mud.feedTab === 'system' }" @click="mud.feedTab = 'system'">系统信息</button>
        </div>

        <!-- 信息内容区 -->
        <div class="pc-feed-content" ref="leftFeed" v-html="mud.activeFeedHtml"></div>
        
        <!-- 中间底部：命令输入框 -->
        <div class="pc-bottom-command">
          <CommandInput />
        </div>
      </div>
    </div>

    <!-- 右侧面板 -->
    <div class="pc-right-panel">
      <!-- 聊天区域 -->
      <div class="pc-chat-section">
        <div class="pc-chat-header-mini">
          <span>聊天</span>
          <button class="pc-chat-expand-btn" @click="mud.toggleChatRoom" title="打开完整聊天室">⤢</button>
        </div>
        <div class="pc-chat-content-mini" ref="chatFeedMini" v-html="mud.allChatHtml || '<div style=\'color:#666;text-align:center;padding:20px;\'>暂无聊天消息</div>'"></div>
        <ChatInput />
      </div>
      
      <!-- 状态条区域（移到聊天下面） -->
      <StatusBars />
      
      <!-- 右下角：九宫格出口按钮 + 自定义命令 -->
      <div class="pc-right-bottom">
        <ExitButtons />
        
        <!-- 自定义命令按钮（006消息） -->
        <div class="pc-custom-cmds" v-if="mud.customCmds && mud.customCmds.length > 0">
          <!-- 第一行：b1-b5 + 第6个"自定"/"关闭"按钮 -->
          <div class="pc-custom-cmd-row">
            <button 
              class="pc-custom-cmd-btn" 
              v-for="(cmd, idx) in customCmdsRow1" 
              :key="cmd.key" 
              @click="handleCustomCmdClick(cmd, idx + 1)"
              @contextmenu.prevent="handleCustomCmdLongPress($event, cmd, idx + 1)"
            >
              <span v-html="cmd.labelHtml || cmd.label"></span>
            </button>
            <!-- 填充空位以保持布局 -->
            <div class="pc-custom-cmd-placeholder" v-for="n in (5 - customCmdsRow1.length)" :key="'empty1-' + n"></div>
            <!-- 第一行第6个按钮：自定/关闭 -->
            <button 
              class="pc-custom-cmd-btn pc-custom-toggle-btn"
              @click="mud.toggleCustomEditMode()"
            >
              {{ mud.customEditMode ? '关闭' : '自定' }}
            </button>
          </div>
          
          <!-- 第二行：b6-b11 (最多6个按钮) -->
          <div class="pc-custom-cmd-row">
            <button 
              class="pc-custom-cmd-btn" 
              v-for="(cmd, idx) in customCmdsRow2" 
              :key="cmd.key" 
              @click="handleCustomCmdClick(cmd, idx + 6)"
              @contextmenu.prevent="handleCustomCmdLongPress($event, cmd, idx + 6)"
            >
              <span v-html="cmd.labelHtml || cmd.label"></span>
            </button>
            <!-- 填充空位以保持6列布局 -->
            <div class="pc-custom-cmd-placeholder" v-for="n in (6 - customCmdsRow2.length)" :key="'empty2-' + n"></div>
          </div>
          
          <!-- 第三行：b12-b17 (固定6个按钮) -->
          <div class="pc-custom-cmd-row">
            <button 
              class="pc-custom-cmd-btn" 
              v-for="cmd in customCmdsRow3" 
              :key="cmd.key" 
              @click="mud.sendCommand(cmd.cmd)"
            >
              <span v-html="cmd.labelHtml || cmd.label"></span>
            </button>
            <!-- 填充空位以保持6列布局 -->
            <div class="pc-custom-cmd-placeholder" v-for="n in (6 - customCmdsRow3.length)" :key="'empty3-' + n"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 自定义命令面板 -->
    <div class="pc-custom-cmds-panel" v-show="mud.showCustomCmds">
      <div class="pc-cmd-panel-header">
        <h3>自定义命令配置</h3>
        <button class="pc-close-btn" @click="mud.showCustomCmds = false">×</button>
      </div>
      <div class="pc-cmd-grid">
        <div class="pc-cmd-item" v-for="(cmd, idx) in mud.customCmds" :key="idx">
          <label>命令 {{ idx + 1 }}:</label>
          <input type="text" v-model="cmd.cmd" placeholder="输入命令" />
          <label>显示:</label>
          <input type="text" v-model="cmd.label" placeholder="按钮文字" />
          <button class="pc-cmd-save-btn" @click="mud.saveCustomCmd(idx)">保存</button>
        </div>
      </div>
    </div>

    <!-- 聊天室弹窗 -->
    <div class="pc-chat-overlay" v-show="mud.chatRoomVisible">
      <div class="pc-chat-container">
        <div class="pc-chat-header">
          <h2>聊天室</h2>
          <button class="pc-chat-close" @click="mud.toggleChatRoom">×</button>
        </div>
        <div class="pc-chat-tabs">
          <button class="pc-chat-tab" :class="{ active: mud.chatTab === 'all' }" @click="mud.chatTab = 'all'">全部聊天</button>
          <button class="pc-chat-tab" :class="{ active: mud.chatTab === 'system' }" @click="mud.chatTab = 'system'">系统消息</button>
        </div>
        <div class="pc-chat-content">
          <div class="pc-chat-feed" v-html="mud.chatTab === 'all' ? mud.allChatHtml : mud.systemChatHtml"></div>
        </div>
        <ChatInput />
      </div>
    </div>

    <!-- 对象交互对话框 -->
    <div class="pc-object-dialog" v-if="mud.actionBlocks && mud.actionBlocks.length > 0">
      <div class="pc-dialog-header">
        <h3>{{ mud.actionBlocks[0].title }}</h3>
        <button class="pc-dialog-close" @click="closeActionBlocks">×</button>
      </div>
      <div class="pc-dialog-content">
        <!-- 输入框类型（001消息） -->
        <div v-if="mud.actionBlocks[0].kind === '001'" class="pc-text-input-container">
          <div class="pc-text-prompt" v-html="mud.actionBlocks[0].items[0]?.labelHtml"></div>
          <div class="pc-text-input-row">
            <input 
              type="text" 
              class="pc-text-input" 
              v-model="textInputValue" 
              @keyup.enter="handleTextInputSubmit"
              placeholder="请输入内容..."
            />
            <button class="pc-text-submit-btn" @click="handleTextInputSubmit">确定</button>
          </div>
        </div>
        <!-- 普通按钮类型 -->
        <div v-else class="pc-dialog-grid" :class="'cols-' + (mud.actionBlocks[0].cols || 2)">
          <button class="pc-dialog-btn" v-for="item in mud.actionBlocks[0].items" :key="item.key" @click="handleActionItemClick(item)">
            <span v-html="item.labelHtml"></span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { inject, computed, onMounted, onUnmounted, ref, watch, nextTick } from 'vue';
import ChatInput from './ChatInput.vue';
import ExitButtons from './ExitButtons.vue';
import CommandInput from './CommandInput.vue';
import StatusBars from './StatusBars.vue';

const mud = inject('mud');

// 输入框类型的文本值
const textInputValue = ref('');

// 消息容器的 ref
const leftFeed = ref(null);
const chatFeedMini = ref(null);

// 最大行数限制
const MAX_LINES = 5000;

// 滚动到底部的函数
const scrollToBottom = (element) => {
  if (element) {
    nextTick(() => {
      element.scrollTop = element.scrollHeight;
    });
  }
};

// 监听主信息区消息变化，自动滚动到底部
watch(() => mud.activeFeedHtml, () => {
  scrollToBottom(leftFeed.value);
  
  // 限制最大行数
  if (leftFeed.value) {
    const lines = leftFeed.value.querySelectorAll('.line');
    if (lines.length > MAX_LINES) {
      // 移除多余的行（从顶部开始移除）
      const excessCount = lines.length - MAX_LINES;
      for (let i = 0; i < excessCount; i++) {
        if (lines[i]) {
          lines[i].remove();
        }
      }
    }
  }
});

// 监听聊天消息变化，自动滚动到底部
watch(() => mud.allChatHtml, () => {
  scrollToBottom(chatFeedMini.value);
  
  // 限制最大行数
  if (chatFeedMini.value) {
    const lines = chatFeedMini.value.querySelectorAll('.line');
    if (lines.length > MAX_LINES) {
      // 移除多余的行（从顶部开始移除）
      const excessCount = lines.length - MAX_LINES;
      for (let i = 0; i < excessCount; i++) {
        if (lines[i]) {
          lines[i].remove();
        }
      }
    }
  }
});

// 监听 actionBlocks 变化，初始化输入框默认值
watch(() => mud.actionBlocks, (newBlocks) => {
  if (!newBlocks || newBlocks.length === 0) {
    // 关闭弹窗时清空输入框
    textInputValue.value = '';
    return;
  }
  
  const block = newBlocks[0];
  if (block.kind === '001' && block.items && block.items.length > 0) {
    const item = block.items[0];
    
    // 检查是否是自定义按钮编辑
    if (item.cmdPrefix === 'CUSTOM_EDIT' && item.customData) {
      // 设置初始值为 "名称,指令" 格式
      const { currentLabel, currentCmd } = item.customData;
      textInputValue.value = `${currentLabel || ''},${currentCmd || ''}`;
    } else {
      // 普通输入框，清空或根据 item 设置默认值
      textInputValue.value = '';
    }
  } else {
    // 非 001 类型，清空输入框
    textInputValue.value = '';
  }
}, { deep: true });

// 计算自定义出口（非标准方向的出口）
const customExits = computed(() => {
  if (!mud.exits) return [];
  
  const standardDirections = ['north', 'south', 'west', 'east', 'northwest', 'northeast', 'southwest', 'southeast'];
  
  return mud.exits.filter(exit => {
    return !standardDirections.includes(exit.key) && exit.visible;
  });
});

// 计算自定义命令按钮分组（按安卓客户端布局）
// 根据按钮的 key（bxx）来决定放在哪一行
const customCmdsRow1 = computed(() => {
  if (!mud.customCmds || mud.customCmds.length === 0) return [];
  
  // 如果在编辑模式，返回空的"长按"按钮
  if (mud.customEditMode) {
    const buttons = [];
    for (let i = 1; i <= 5; i++) {
      buttons.push({
        key: `b${i}-longpress`,
        cmd: '',
        label: '长按',
        labelHtml: ''
      });
    }
    return buttons;
  }
  
  // 正常模式：过滤 b1-b5
  return mud.customCmds.filter(cmd => {
    const match = cmd.key.match(/^b(\d+)-/);
    if (match) {
      const num = parseInt(match[1]);
      return num >= 1 && num <= 5;
    }
    return false;
  });
});

const customCmdsRow2 = computed(() => {
  if (!mud.customCmds || mud.customCmds.length === 0) return [];
  
  // 如果在编辑模式，返回空的"长按"按钮
  if (mud.customEditMode) {
    const buttons = [];
    for (let i = 6; i <= 11; i++) {
      buttons.push({
        key: `b${i}-longpress`,
        cmd: '',
        label: '长按',
        labelHtml: ''
      });
    }
    return buttons;
  }
  
  // 正常模式：过滤 b6-b11
  return mud.customCmds.filter(cmd => {
    const match = cmd.key.match(/^b(\d+)-/);
    if (match) {
      const num = parseInt(match[1]);
      return num >= 6 && num <= 11;
    }
    return false;
  });
});

const customCmdsRow3 = computed(() => {
  if (!mud.customCmds || mud.customCmds.length === 0) return [];
  // 第三行：b12-b17（key 以 b12-b17 开头的按钮，固定在这一行）
  return mud.customCmds.filter(cmd => {
    const match = cmd.key.match(/^b(\d+)-/);
    if (match) {
      const num = parseInt(match[1]);
      return num >= 12 && num <= 17;
    }
    return false;
  });
});

// 处理对象点击 - 弹出交互窗口
const handleObjectClick = (target) => {
  if (target.cmd) {
    mud.sendCommand(target.cmd);
  } else if (target.key) {
    mud.sendCommand(target.key);
  }
};

// 处理动作项点击
const handleActionItemClick = (item) => {
  if (item.cmd) {
    mud.sendCommand(item.cmd);
  } else if (item.key) {
    mud.sendCommand(item.key);
  }
};

// 处理自定义命令按钮点击
const handleCustomCmdClick = (cmd, buttonIndex) => {
  // 如果在编辑模式且按钮为空，不响应点击
  if (mud.customEditMode && (!cmd.cmd || cmd.cmd.trim() === '')) {
    return;
  }
  
  // 正常发送命令
  if (cmd.cmd) {
    mud.sendCommand(cmd.cmd);
  }
};

// 处理自定义命令按钮长按（右键点击）
const handleCustomCmdLongPress = (event, cmd, buttonIndex) => {
  if (!mud.customEditMode) return; // 只在编辑模式响应长按
  
  // 使用现有的 pc-object-dialog 显示编辑表单
  mud.actionBlocks = [{
    title: '编辑快捷键',
    kind: '001',
    cols: 1,
    items: [{
      key: `edit-b${buttonIndex}`,
      labelHtml: '<div style="margin-bottom:10px;">请输入快捷键名称和指令：</div>',
      cmdPrefix: 'CUSTOM_EDIT',
      customData: { buttonIndex, currentLabel: cmd.label || '', currentCmd: cmd.cmd || '' }
    }]
  }];
  
  // textInputValue 会通过 watch 自动初始化
};

// 处理输入框提交（001消息类型）
const handleTextInputSubmit = () => {
  if (!mud.actionBlocks || mud.actionBlocks.length === 0) return;
  
  const item = mud.actionBlocks[0].items[0];
  
  // 检查是否是自定义按钮编辑
  if (item.cmdPrefix === 'CUSTOM_EDIT' && item.customData) {
    const { buttonIndex, currentLabel, currentCmd } = item.customData;
    const input = textInputValue.value.trim();
    
    // 解析输入：格式为 "名称,指令"
    const parts = input.split(',');
    if (parts.length >= 2) {
      const label = parts[0].trim();
      const cmd = parts.slice(1).join(',').trim(); // 支持指令中包含逗号
      
      if (label && cmd) {
        mud.saveCustomButton(buttonIndex, label, cmd);
      } else {
        mud.pushToast('请填写快捷键名称和指令');
      }
    } else {
      mud.pushToast('格式错误，请使用：名称,指令');
    }
    
    closeActionBlocks();
    return;
  }
  
  // 普通的 001 消息处理
  if (!item || !item.cmdPrefix) return;
  
  // 发送命令：命令前缀 + 用户输入的内容
  const fullCmd = `${item.cmdPrefix} ${textInputValue.value.trim()}`;
  mud.sendCommand(fullCmd);
  
  // 清空输入框并关闭对话框
  textInputValue.value = '';
  closeActionBlocks();
};

// 关闭动作块
const closeActionBlocks = () => {
  if (mud.actionBlocks) {
    mud.actionBlocks = [];
  }
};

// 处理全局点击事件 - 支持所有区域的 mud-link 点击
const handleGlobalClick = (event) => {
  const anchor = event.target.closest('[data-mud-cmd]');
  if (!anchor) return;
  
  event.preventDefault();
  event.stopPropagation();
  let cmd = anchor.getAttribute('data-mud-cmd');
  // 移除 cmds: 前缀（如果存在）
  if (cmd && cmd.startsWith('cmds:')) {
    cmd = cmd.slice(5);
  }
  if (cmd) {
    mud.sendCommand(cmd);
  }
};

// 在组件挂载时注册全局点击事件监听器
onMounted(() => {
  document.addEventListener('click', handleGlobalClick);
});

// 在组件卸载时移除事件监听器
onUnmounted(() => {
  document.removeEventListener('click', handleGlobalClick);
});
</script>

<style scoped>
/* PC端MUD游戏界面 */
.mud-pc-screen {
  display: flex !important;
  height: 100vh !important;
  background: #221505 !important;
  overflow: hidden !important;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  z-index: 9999 !important;
}

/* 左侧边栏 */
.pc-left-sidebar {
  width: 180px;
  min-width: 160px;
  background: #221505;
  border-right: 1px solid #666;
  display: flex;
  flex-direction: column;
  padding: 6px;
}

/* 左侧对象列表区域 */
.pc-objects-section {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: 6px;
  border-radius: 4px;
  overflow-y: auto;
  max-height: calc(50vh - 100px);
}

/* 左下角区域 */
.pc-left-bottom {
  margin-top: auto;
  padding-top: 10px;
}

/* 其他出口区域 */
.custom-exits-section {
  display: flex;
  flex-direction: column;
  gap: 3px;
  padding: 6px;
  border-radius: 4px;
  overflow-y: auto;
  max-height: 200px;
}

.section-header {
  color: #e6bf6b;
  font-size: 12px;
  font-weight: bold;
  padding: 4px 0;
  border-bottom: 1px solid #555;
}

.obj-btn {
  background: rgba(50, 45, 40, 0.7);
  border: 1px solid #555;
  color: #ddbb99;
  padding: 6px 4px;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 2px;
  text-align: center;
  min-height: 32px;
}

.obj-btn:hover {
  background: rgba(80, 70, 60, 0.8);
  border-color: #e6bf6b;
}

/* 左侧主信息区 */
.pc-left-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: #221505;
}

/* 顶部标题栏 */
.pc-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 12px;
  background: rgba(40, 30, 20, 0.8);
  border-bottom: 1px solid #666;
  min-height: 40px;
  gap: 10px;
}

.pc-location-name {
  color: #e6bf6b;
  font-size: 15px;
  font-weight: bold;
  flex: 0 0 auto;
}

/* 快捷菜单按钮容器 */
.pc-dialog-btns {
  display: flex;
  gap: 4px;
  flex: 1;
  margin-left: 10px;
}

.pc-dialog-btn {
  background: rgba(50, 45, 40, 0.7);
  border: 1px solid #555;
  color: #ddbb99;
  padding: 4px 8px;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 3px;
  text-align: center;
  white-space: nowrap;
}

.pc-dialog-btn:hover {
  background: rgba(80, 70, 60, 0.8);
  border-color: #e6bf6b;
}

.pc-top-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.pc-top-btn {
  background: rgba(60, 60, 60, 0.6);
  border: 1px solid #666;
  color: #ddbb99;
  padding: 3px 8px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 3px;
  transition: all 0.2s;
}

.pc-top-btn:hover {
  background: rgba(100, 90, 80, 0.8);
  border-color: #e6bf6b;
}

/* 故事描述区 */
.pc-story-area {
  max-height: 120px;
  overflow-y: auto;
  padding: 6px 10px;
  background: rgba(30, 25, 20, 0.5);
  border-bottom: 1px solid #666;
}

.pc-story-area.hidden {
  max-height: 0;
  padding: 0;
  overflow: hidden;
  border: none;
}

.pc-story-text {
  color: #ddbb99;
  font-size: 13px;
  line-height: 1.6;
}

/* 主信息区 */
.pc-main-feed {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

/* 标签页 */
.pc-tabs {
  display: flex;
  background: rgba(40, 35, 30, 0.8);
  border-bottom: 1px solid #666;
  padding: 0 8px;
}

.pc-tab {
  flex: 1;
  background: transparent;
  border: none;
  color: #bbbbbb;
  padding: 7px 8px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}

.pc-tab:hover {
  background: rgba(60, 55, 50, 0.5);
  color: #ddbb99;
}

.pc-tab.active {
  background: rgba(230, 191, 107, 0.15);
  color: #e6bf6b;
  border-bottom-color: #e6bf6b;
  font-weight: 600;
}

/* 信息内容区 */
.pc-feed-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px 10px;
  color: #ddbb99;
  font-size: 13px;
  line-height: 1.6;
  background: rgba(26, 21, 16, 0.5);
}

/* 底部命令输入框 */
.pc-bottom-command {
  background: rgba(30, 25, 20, 0.95);
  border-top: 2px solid #666;
  padding: 8px;
}

/* 右侧面板 */
.pc-right-panel {
  width: 300px;
  min-width: 260px;
  background: #221505;
  border-left: 1px solid #666;
  display: flex;
  flex-direction: column;
  padding: 6px;
  gap: 6px;
}

/* 右下角区域 */
.pc-right-bottom {
  margin-top: auto;
  background: rgba(30, 25, 20, 0.95);
  border-radius: 4px;
  border: 1px solid #444;
  padding: 6px;
}

/* 自定义命令按钮区域（006消息） */
.pc-custom-cmds {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 6px;
  padding-top: 6px;
  border-top: 1px solid #555;
}

/* 自定义命令按钮行 - 固定6列 */
.pc-custom-cmd-row {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 4px;
}

/* 占位符（用于第一行第6个空位） */
.pc-custom-cmd-placeholder {
  visibility: hidden;
  min-height: 36px;
}

.pc-custom-cmd-btn {
  background: linear-gradient(135deg, #3a2f25 0%, #2a2520 100%);
  border: 1px solid #555;
  color: #ddbb99;
  padding: 6px 4px;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 3px;
  text-align: center;
  min-height: 36px;
}

.pc-custom-cmd-btn:hover {
  background: linear-gradient(135deg, #5a4f45 0%, #4a4540 100%);
  border-color: #e6bf6b;
  transform: translateY(-1px);
}

/* 自定/关闭切换按钮特殊样式 */
.pc-custom-toggle-btn {
  background: linear-gradient(135deg, #4a3f35 0%, #3a2f25 100%) !important;
  border-color: #e6bf6b !important;
  color: #e6bf6b !important;
  font-weight: 600;
}

.pc-custom-toggle-btn:hover {
  background: linear-gradient(135deg, #5a4f45 0%, #4a3f35 100%) !important;
  border-color: #f0cf7b !important;
}

/* 聊天区域 */
.pc-chat-section {
  display: flex;
  flex-direction: column;
  background: rgba(30, 25, 20, 0.5);
  border-radius: 4px;
  border: 1px solid #444;
  max-height: 250px;
  min-height: 180px;
}

.pc-chat-header-mini {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  background: rgba(40, 35, 30, 0.8);
  border-bottom: 1px solid #555;
  border-radius: 4px 4px 0 0;
  color: #e6bf6b;
  font-size: 12px;
  font-weight: bold;
}

.pc-chat-expand-btn {
  background: transparent;
  border: none;
  color: #ddbb99;
  font-size: 14px;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 3px;
}

.pc-chat-expand-btn:hover {
  background: rgba(100, 90, 80, 0.5);
  color: #e6bf6b;
}

.pc-chat-content-mini {
  flex: 1;
  overflow-y: auto;
  padding: 6px 8px;
  color: #ddbb99;
  font-size: 12px;
  line-height: 1.5;
}

/* 自定义命令面板 */
.pc-custom-cmds-panel {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 550px;
  max-width: 90vw;
  max-height: 80vh;
  background: #2a2015;
  border: 2px solid #666;
  border-radius: 6px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  z-index: 10001;
  display: flex;
  flex-direction: column;
}

.pc-cmd-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: linear-gradient(135deg, #3a2a1a 0%, #2a1f15 100%);
  border-bottom: 2px solid #555;
  border-radius: 4px 4px 0 0;
}

.pc-cmd-panel-header h3 {
  color: #e6bf6b;
  font-size: 15px;
  margin: 0;
}

.pc-close-btn {
  background: transparent;
  border: none;
  color: #ddbb99;
  font-size: 22px;
  cursor: pointer;
  padding: 0 6px;
  line-height: 1;
}

.pc-close-btn:hover {
  color: #ff6b6b;
}

.pc-cmd-grid {
  padding: 14px;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.pc-cmd-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px;
  background: rgba(40, 35, 30, 0.6);
  border: 1px solid #555;
  border-radius: 4px;
}

.pc-cmd-item label {
  color: #bbbbbb;
  font-size: 11px;
  font-weight: 600;
}

.pc-cmd-item input {
  background: rgba(30, 25, 20, 0.8);
  border: 1px solid #666;
  color: #ddbb99;
  padding: 5px 8px;
  font-size: 12px;
  border-radius: 3px;
}

.pc-cmd-item input:focus {
  outline: none;
  border-color: #e6bf6b;
}

.pc-cmd-save-btn {
  background: linear-gradient(135deg, #5a4a3a 0%, #4a3f35 100%);
  border: 1px solid #666;
  color: #e6bf6b;
  padding: 5px 10px;
  font-size: 11px;
  cursor: pointer;
  border-radius: 3px;
  margin-top: 3px;
}

.pc-cmd-save-btn:hover {
  background: linear-gradient(135deg, #7a6a5a 0%, #6a5f55 100%);
  border-color: #e6bf6b;
}

/* 聊天室弹窗 */
.pc-chat-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.85);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pc-chat-container {
  width: 80%;
  max-width: 900px;
  height: 75%;
  max-height: 650px;
  background: #221505;
  border: 2px solid #666;
  border-radius: 6px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
}

.pc-chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: linear-gradient(135deg, #3a2a1a 0%, #2a1f15 100%);
  border-bottom: 2px solid #555;
  border-radius: 4px 4px 0 0;
}

.pc-chat-header h2 {
  color: #e6bf6b;
  font-size: 17px;
  margin: 0;
}

.pc-chat-close {
  background: transparent;
  border: none;
  color: #ddbb99;
  font-size: 26px;
  cursor: pointer;
  padding: 0 6px;
  line-height: 1;
}

.pc-chat-close:hover {
  color: #ff6b6b;
}

.pc-chat-tabs {
  display: flex;
  background: rgba(40, 35, 30, 0.8);
  border-bottom: 2px solid #555;
}

.pc-chat-tab {
  flex: 1;
  background: transparent;
  border: none;
  color: #bbbbbb;
  padding: 9px 10px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 3px solid transparent;
}

.pc-chat-tab.active {
  background: rgba(230, 191, 107, 0.15);
  color: #e6bf6b;
  border-bottom-color: #e6bf6b;
  font-weight: 600;
}

.pc-chat-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.pc-chat-feed {
  color: #ddbb99;
  font-size: 13px;
  line-height: 1.6;
}

/* 对象交互窗口 */
.pc-object-dialog {
  position: fixed !important;
  top: 50% !important;
  left: 50% !important;
  transform: translate(-50%, -50%) !important;
  width: 450px;
  max-width: 90vw;
  max-height: 70vh;
  background: #2a2015;
  border: 2px solid #666;
  border-radius: 6px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  z-index: 10100 !important;
  display: flex !important;
  flex-direction: column;
}

.pc-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background: linear-gradient(135deg, #3a2a1a 0%, #2a1f15 100%);
  border-bottom: 2px solid #555;
  border-radius: 4px 4px 0 0;
}

.pc-dialog-header h3 {
  color: #e6bf6b;
  font-size: 15px;
  margin: 0;
}

.pc-dialog-close {
  background: transparent;
  border: none;
  color: #ddbb99;
  font-size: 22px;
  cursor: pointer;
  padding: 0 6px;
  line-height: 1;
}

.pc-dialog-close:hover {
  color: #ff6b6b;
}

.pc-dialog-content {
  padding: 14px;
  overflow-y: auto;
}

/* 输入框类型容器（001消息） */
.pc-text-input-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pc-text-prompt {
  color: #ddbb99;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.pc-text-input-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.pc-text-input {
  flex: 1;
  min-height: 36px;
  padding: 0 10px;
  color: #ddbb99;
  background: #1a1510;
  border: 1px solid #555;
  border-radius: 4px;
  font-size: 13px;
}

.pc-text-input:focus {
  outline: none;
  border-color: #e6bf6b;
  box-shadow: 0 0 0 1px rgba(230, 191, 107, 0.3);
}

.pc-text-submit-btn {
  min-height: 36px;
  padding: 0 16px;
  background: linear-gradient(180deg, #3a2718, #24170e);
  border: 1px solid #e6bf6b;
  border-radius: 4px;
  color: #ffe7bf;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.pc-text-submit-btn:hover {
  background: linear-gradient(180deg, #4a3728, #34271e);
  border-color: #ffd700;
}

.pc-text-submit-btn:active {
  transform: scale(0.98);
}

/* 普通按钮类型 */
.pc-dialog-grid {
  display: grid;
  gap: 6px;
}

.pc-dialog-grid.cols-2 {
  grid-template-columns: repeat(2, 1fr);
}

.pc-dialog-grid.cols-3 {
  grid-template-columns: repeat(3, 1fr);
}

.pc-dialog-grid.cols-4 {
  grid-template-columns: repeat(4, 1fr);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .pc-right-panel {
    width: 260px;
  }
  
  .pc-left-sidebar {
    width: 160px;
  }
}

@media (max-width: 768px) {
  .mud-pc-screen {
    flex-direction: column;
  }
  
  .pc-left-sidebar {
    width: 100%;
    max-height: 150px;
    border-right: none;
    border-bottom: 1px solid #666;
    flex-direction: row;
    overflow-x: auto;
  }
  
  .pc-left-main {
    flex: 1;
  }
  
  .pc-right-panel {
    width: 100%;
    max-height: 40vh;
    border-left: none;
    border-top: 1px solid #666;
  }
  
  .pc-cmd-grid {
    grid-template-columns: 1fr;
  }
}
</style>