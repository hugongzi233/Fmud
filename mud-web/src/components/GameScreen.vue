  <template>
  <div class="mud-pc-screen" v-if="mud && mud.screen !== 'home'">
    <!--  Mobile模式：聊天区域在最顶部 -->
    <div class="pc-chat-section-mobile-top">
      <div class="pc-chat-content-mini" ref="chatFeedMiniMobile" v-html="mud.allChatHtml || '<div style=\'color:#666;text-align:center;padding:20px;\'>暂无聊天消息</div>'"></div>
    </div>

    <!-- 中间主信息区 -->
    <div class="pc-center-area">
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

      <!-- 左侧边栏 + 右侧内容区 -->
      <div class="pc-content-wrapper">
        <!-- 左侧边栏 -->
        <div class="pc-left-sidebar">
          <!-- 对象列表 -->
          <div class="pc-objects-section" v-if="mud.targets && mud.targets.length > 0">
            <button class="obj-btn" v-for="target in mud.targets" :key="target.key" @click="handleObjectClick(target)">
              <span v-html="target.labelHtml"></span>
              <!-- 血条容器 -->
              <div class="hp-bar-container" v-if="target.qiXue">
                <div class="hp-bar-fill" :style="{ width: getHpPercent(target.qiXue) + '%' }"></div>
              </div>
            </button>
          </div>
          
          <!-- 左下角：其他出口 -->
          <div class="pc-left-bottom">
            <div class="custom-exits-section" v-if="customExits.length > 0">
              <button class="obj-btn" v-for="exit in customExits" :key="exit.key" @click="mud.sendCommand(exit.cmd)">
                <span v-html="exit.labelHtml"></span>
              </button>
            </div>
            <!-- Mobile模式：命令按钮 -->
            <div class="mobile-command-btn-wrapper" v-show="customExits.length === 0">
              <button class="obj-btn mobile-command-btn" @click="showMobileCommandInput = true">
                <span>命令</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 右侧内容区（故事描述区 + 主信息区） -->
        <div class="pc-right-content">
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
      </div>
    </div>

    <!-- 右侧面板 -->
    <div class="pc-right-panel">
      <!-- 聊天区域（PC模式显示） -->
      <div class="pc-chat-section pc-chat-pc-only">
        <div class="pc-chat-header-mini">
          <span>聊天</span>
          <button class="pc-chat-expand-btn" @click="mud.toggleChatRoom" title="打开完整聊天室">⤢</button>
        </div>
        <div class="pc-chat-content-mini" ref="chatFeedMiniPc" v-html="mud.allChatHtml || '<div style=\'color:#666;text-align:center;padding:20px;\'>暂无聊天消息</div>'"></div>
        <ChatInput />
      </div>
      
      <!-- 状态条区域（移到聊天下面） -->
      <StatusBars class="status-bars-pc-only" />
      
      <!-- 右下角：九宫格出口按钮 + 自定义命令 -->
      <div class="pc-right-bottom pc-right-bottom-pc-only">
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
              自定
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

    <!-- Mobile模式：底部出口按钮和自定义命令 -->
    <div class="pc-right-bottom pc-right-bottom-mobile">
      <!-- 第一行：命令按钮 + 出口按钮 -->
      <div class="pc-right-bottom-row-1">
        <!-- 命令按钮（左侧） -->
        <button class="mobile-command-btn-inline" @click="showMobileCommandInput = true">
          <span>命令</span>
        </button>
        
        <!-- 出口按钮（右侧） -->
        <div class="exit-buttons-container-mobile">
          <ExitButtons />
        </div>
      </div>
      
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

    <!-- Mobile模式：底部状态条 -->
    <StatusBars class="status-bars-mobile" />

    <!--  小屏幕命令触发按钮 -->
    <button class="mobile-command-trigger" @click="openMobileCommandInput">⌨</button>

    <!--  小屏幕命令输入弹窗 -->
    <div class="mobile-command-overlay" v-if="showMobileCommandInput" @click="closeMobileCommandInput">
      <div class="mobile-command-dialog" @click.stop>
        <div class="mobile-command-header">
          <h3>输入命令</h3>
          <button class="mobile-close-btn" @click="closeMobileCommandInput">×</button>
        </div>
        <div class="mobile-command-body">
          <input 
            type="text" 
            class="mobile-command-input" 
            v-model="mobileCommandText" 
            @keyup.enter="submitMobileCommand"
            placeholder="请输入命令..."
            autofocus
          />
          <button class="mobile-submit-btn" @click="submitMobileCommand">发送</button>
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

    <!-- 动作块弹窗已移至 GameDialogs.vue 统一管理 -->
  </div>
</template>

<script setup>
import { inject, computed, onMounted, onUnmounted, ref, watch, nextTick } from 'vue';
import ChatInput from './ChatInput.vue';
import ExitButtons from './ExitButtons.vue';
import CommandInput from './CommandInput.vue';
import StatusBars from './StatusBars.vue';

const mud = inject('mud');

//  移动命令输入相关状态
const showMobileCommandInput = ref(false);
const mobileCommandText = ref('');

// 消息容器的 ref
const leftFeed = ref(null);
const chatFeedMini = ref(null); // PC模式
const chatFeedMiniMobile = ref(null); // Mobile模式

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
  // Mobile模式滚动顶部聊天区域
  scrollToBottom(chatFeedMiniMobile.value);
  // PC模式滚动右侧聊天区域
  scrollToBottom(chatFeedMini.value);
  
  // 限制最大行数（Mobile模式）
  if (chatFeedMiniMobile.value) {
    const lines = chatFeedMiniMobile.value.querySelectorAll('.line');
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
  
  // 限制最大行数（PC模式）
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
  
  // 自定义模式下也保持显示已保存按钮，便于直接点击；长按入口仍然可用
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
  
  // 自定义模式下也保持显示已保存按钮，便于直接点击；长按入口仍然可用
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

// 计算血量百分比
const getHpPercent = (qiXue) => {
  if (!qiXue || typeof qiXue !== 'string') return 0;
  
  const parts = qiXue.split('/');
  if (parts.length !== 2) return 0;
  
  const current = parseInt(parts[0], 10);
  const max = parseInt(parts[1], 10);
  
  if (isNaN(current) || isNaN(max) || max <= 0) return 0;
  
  return Math.min(100, Math.max(0, (current / max) * 100));
};

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
  // 检查是否有ESC菜单标记
  if (item.hasEscMenu && item.escPayload) {
    // 触发弹窗而不是发送命令
    console.log('检测到ESC菜单,触发弹窗:', item.escPayload);
    // 调用mud对象的方法来处理ESC 020消息
    mud.handleEscMenu(item.escPayload);
    return;
  }
  
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
      labelHtml: '<div style="margin-bottom:10px;">请输入快捷键名称+,+指令：</div>',
      cmdPrefix: 'CUSTOM_EDIT',
      customData: { buttonIndex, currentLabel: cmd.label || '', currentCmd: cmd.cmd || '' }
    }]
  }];
  
  // textInputValue 会通过 watch 自动初始化
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

//  移动命令输入相关方法
const openMobileCommandInput = () => {
  showMobileCommandInput.value = true;
  mobileCommandText.value = '';
};

const closeMobileCommandInput = () => {
  showMobileCommandInput.value = false;
  mobileCommandText.value = '';
};

const submitMobileCommand = () => {
  const cmd = mobileCommandText.value.trim();
  if (cmd) {
    mud.sendCommand(cmd);
    closeMobileCommandInput();
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
/* 所有样式已移至 public/styles/gamescreen.css */
</style>