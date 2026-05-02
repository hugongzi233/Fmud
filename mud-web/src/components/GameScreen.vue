<template>
  <div class="mud-pc-screen" v-if="mud && mud.screen !== 'home'">
    <!-- 左侧边栏 -->
    <div class="pc-left-sidebar">
      <!-- 对象列表 -->
      <div class="pc-objects-section" v-if="mud.targets && mud.targets.length > 0">
        <div class="section-header">当前场景</div>
        <button class="obj-btn" v-for="target in mud.targets" :key="target.key" @click="handleObjectClick(target)">
          <span v-html="target.labelHtml"></span>
        </button>
      </div>
      
      <!-- 左下角：其他出口 -->
      <div class="pc-left-bottom">
        <div class="custom-exits-section" v-if="customExits.length > 0">
          <div class="section-header">其他出口</div>
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
            {{ mud.storyVisible ? '▼' : '▶' }}
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
          <button class="pc-tab" :class="{ active: mud.feedTab === 'fight' }" @click="mud.feedTab = 'fight'">战斗</button>
        </div>

        <!-- 信息内容区 -->
        <div class="pc-feed-content" ref="leftFeed" @click="mud.handleFeedClick" v-html="mud.activeFeedHtml"></div>
        
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
          <span>💬 聊天</span>
          <button class="pc-chat-expand-btn" @click="mud.toggleChatRoom" title="打开完整聊天室">⤢</button>
        </div>
        <div class="pc-chat-content-mini" v-html="mud.allChatHtml || '<div style=\'color:#666;text-align:center;padding:20px;\'>暂无聊天消息</div>'"></div>
        <ChatInput />
      </div>
      
      <!-- 状态条区域（移到聊天下面） -->
      <StatusBars />
      
      <!-- 右下角：九宫格出口按钮 + 自定义命令 -->
      <div class="pc-right-bottom">
        <ExitButtons />
        
        <!-- 自定义命令按钮（006消息） -->
        <div class="pc-custom-cmds" v-if="mud.customCmds && mud.customCmds.length > 0">
          <button 
            class="pc-custom-cmd-btn" 
            v-for="cmd in mud.customCmds" 
            :key="cmd.key" 
            @click="mud.sendCommand(cmd.cmd)"
          >
            <span v-html="cmd.label || cmd.html || cmd.cmd"></span>
          </button>
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

    <!-- 对象交互窗口 -->
    <div class="pc-object-dialog" v-if="mud.actionBlocks && mud.actionBlocks.length > 0">
      <div class="pc-dialog-header">
        <h3>{{ mud.actionBlocks[0].title }}</h3>
        <button class="pc-dialog-close" @click="closeActionBlocks">×</button>
      </div>
      <div class="pc-dialog-content">
        <div class="pc-dialog-grid" :class="'cols-' + (mud.actionBlocks[0].cols || 2)">
          <button class="pc-dialog-btn" v-for="item in mud.actionBlocks[0].items" :key="item.key" @click="handleActionItemClick(item)">
            <span v-html="item.html"></span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { inject, computed } from 'vue';
import ChatInput from './ChatInput.vue';
import ExitButtons from './ExitButtons.vue';
import CommandInput from './CommandInput.vue';
import StatusBars from './StatusBars.vue';

const mud = inject('mud');

// 计算自定义出口（非标准方向的出口）
const customExits = computed(() => {
  if (!mud.exits) return [];
  
  const standardDirections = ['north', 'south', 'west', 'east', 'northwest', 'northeast', 'southwest', 'southeast'];
  
  return mud.exits.filter(exit => {
    return !standardDirections.includes(exit.key) && exit.visible;
  });
});

// 处理对象点击 - 弹出交互窗口
const handleObjectClick = (target) => {
  console.log('点击对象:', target);
  if (target.cmd) {
    mud.sendCommand(target.cmd);
  } else if (target.key) {
    mud.sendCommand(target.key);
  }
};

// 处理动作项点击
const handleActionItemClick = (item) => {
  console.log('点击动作项:', item);
  if (item.cmd) {
    mud.sendCommand(item.cmd);
  } else if (item.key) {
    mud.sendCommand(item.key);
  }
};

// 关闭动作块
const closeActionBlocks = () => {
  if (mud.actionBlocks) {
    mud.actionBlocks = [];
  }
};
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
  background: rgba(30, 25, 20, 0.5);
  border-radius: 4px;
  border: 1px solid #444;
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
  background: rgba(30, 25, 20, 0.5);
  border-radius: 4px;
  border: 1px solid #444;
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
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 4px;
  margin-top: 6px;
  padding-top: 6px;
  border-top: 1px solid #555;
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
}

.pc-custom-cmd-btn:hover {
  background: linear-gradient(135deg, #5a4f45 0%, #4a4540 100%);
  border-color: #e6bf6b;
  transform: translateY(-1px);
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
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 450px;
  max-width: 90vw;
  max-height: 70vh;
  background: #2a2015;
  border: 2px solid #666;
  border-radius: 6px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  z-index: 10002;
  display: flex;
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