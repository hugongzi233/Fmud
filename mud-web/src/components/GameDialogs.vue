<template>
    <div>
        <!-- GUI 弹窗（007/008/009 消息） -->
        <div class="overlay" v-if="safeMud.showGui">
            <div class="overlay-card gui-card">
                <button class="gui-close-btn" @click="safeMud.closeGui">×</button>
                <div class="overlay-body gui-body">
                    <div class="gui-content" v-if="safeMud.guiHtml" v-html="safeMud.guiHtml"></div>
                    <div class="gui-actions" v-if="safeMud.guiActions1.length || safeMud.guiActions2.length" style="display: flex; flex-direction: column; gap: 10px; max-width: 800px; width: 100%;">
                        <div class="action-section" v-if="safeMud.guiActions1.length" :style="{ display: 'grid', gridTemplateColumns: `repeat(${safeMud.guiColumns || 3}, minmax(10px, 1fr))`, gap: '10px' }">
                            <button class="command-btn" v-for="item in safeMud.guiActions1" :key="item.key" @click="handleGuiActionClick(item)">
                                <div v-html="item.labelHtml"></div>
                                <div class="caption" v-if="item.captionHtml" v-html="item.captionHtml"></div>
                                <div class="caption" v-else>{{ item.caption || item.cmd }}</div>
                            </button>
                        </div>
                        <div class="action-section" v-if="safeMud.guiActions2.length" :style="{ display: 'grid', gridTemplateColumns: `repeat(${safeMud.guiColumns || 3}, minmax(10px, 1fr))`, gap: '10px' }">
                            <button class="command-btn" v-for="item in safeMud.guiActions2" :key="item.key" @click="handleGuiActionClick(item)">
                                <div v-html="item.labelHtml"></div>
                                <div class="caption" v-if="item.captionHtml" v-html="item.captionHtml"></div>
                                <div class="caption" v-else>{{ item.caption || item.cmd }}</div>
                            </button>
                        </div>
                    </div>
                    <div class="compact-item" v-if="!safeMud.guiHtml && !safeMud.guiActions1.length && !safeMud.guiActions2.length">无</div>
                </div>
            </div>
        </div>

        <!-- 动作块弹窗（001/006/007/008/009 消息等） -->
        <div class="overlay" v-if="safeMud.actionBlocks && safeMud.actionBlocks.length > 0" @click="handleOverlayClick">
            <div class="overlay-card" @click.stop>
                <button class="dialog-close-btn" @click="closeActionBlocks">×</button>
                <div class="overlay-header">
                    <div>
                        <div class="card-title">{{ safeMud.actionBlocks[0].title }}</div>
                    </div>
                </div>
                <div class="overlay-body">
                    <!-- 输入框类型（001消息） -->
                    <div v-if="safeMud.actionBlocks[0].kind === '001'" class="pc-text-input-container">
                        <div class="pc-text-prompt" v-html="safeMud.actionBlocks[0].items[0]?.labelHtml"></div>
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
                        <!-- 007 消息附加的 HTML 内容 -->
                        <div v-if="safeMud.actionBlocks[0].guiHtml" class="gui-content" v-html="safeMud.actionBlocks[0].guiHtml" @click="handleGuiContentClick"></div>
                        <!-- 008 消息附加的按钮组 -->
                        <div v-if="safeMud.actionBlocks[0].guiActions1 && safeMud.actionBlocks[0].guiActions1.length" class="gui-actions" :style="{ gridTemplateColumns: `repeat(${safeMud.actionBlocks[0].guiColumns || 3}, minmax(140px, 1fr))`, maxWidth: '800px', width: '100%' }">
                            <button class="command-btn" v-for="item in safeMud.actionBlocks[0].guiActions1" :key="item.key" @click="handleActionItemClick(item)">
                                <div v-html="item.labelHtml"></div>
                            </button>
                        </div>
                        <!-- 009 消息附加的按钮组 -->
                        <div v-if="safeMud.actionBlocks[0].guiButtons && safeMud.actionBlocks[0].guiButtons.length" class="gui-actions" :style="{ gridTemplateColumns: `repeat(${safeMud.actionBlocks[0].guiColumns || 3}, minmax(140px, 1fr))`, maxWidth: '800px', width: '100%' }">
                            <button class="command-btn" v-for="item in safeMud.actionBlocks[0].guiButtons" :key="item.key" @click="handleActionItemClick(item)">
                                <div v-html="item.labelHtml"></div>
                            </button>
                        </div>
                    </div>
                    <!-- GUI 内容类型（007/008/009 消息） -->
                    <div v-else-if="safeMud.actionBlocks[0].kind === 'gui'" class="gui-body">
                        <div class="gui-content" v-if="safeMud.actionBlocks[0].guiHtml" v-html="safeMud.actionBlocks[0].guiHtml" @click="handleGuiContentClick"></div>
                        <div class="gui-actions" v-if="safeMud.actionBlocks[0].guiActions1 && safeMud.actionBlocks[0].guiActions1.length" :style="{ gridTemplateColumns: `repeat(${safeMud.actionBlocks[0].guiColumns || 3}, minmax(140px, 1fr))`, maxWidth: '800px', width: '100%' }">
                            <button class="command-btn" v-for="item in safeMud.actionBlocks[0].guiActions1" :key="item.key" @click="handleActionItemClick(item)">
                                <div v-html="item.labelHtml"></div>
                            </button>
                        </div>
                        <div class="gui-actions" v-if="safeMud.actionBlocks[0].guiButtons && safeMud.actionBlocks[0].guiButtons.length" :style="{ gridTemplateColumns: `repeat(${safeMud.actionBlocks[0].guiColumns || 3}, minmax(140px, 1fr))`, maxWidth: '800px', width: '100%' }">
                            <button class="command-btn" v-for="item in safeMud.actionBlocks[0].guiButtons" :key="item.key" @click="handleActionItemClick(item)">
                                <div v-html="item.labelHtml"></div>
                            </button>
                        </div>
                        <div class="gui-actions" v-if="safeMud.actionBlocks[0].items && safeMud.actionBlocks[0].items.length" :style="{ gridTemplateColumns: `repeat(${safeMud.actionBlocks[0].cols || 3}, minmax(140px, 1fr))`, maxWidth: '800px', width: '100%' }">
                            <button class="command-btn" v-for="item in safeMud.actionBlocks[0].items" :key="item.key" @click="handleActionItemClick(item)">
                                <div v-html="item.labelHtml"></div>
                            </button>
                        </div>
                    </div>
                    <!-- 普通按钮类型（006 消息等） -->
                    <div v-else class="action-grid" :class="'cols-' + (safeMud.actionBlocks[0].cols || 2)">
                        <button class="command-btn" v-for="item in safeMud.actionBlocks[0].items" :key="item.key" @click="handleActionItemClick(item)">
                            <span v-html="item.labelHtml"></span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 对话框弹窗 -->
        <div class="overlay" v-if="safeMud.dialog.visible">
            <div class="overlay-card">
                <button class="dialog-close-btn" @click="safeMud.closeDialog">×</button>
                <div class="overlay-header">
                    <div class="card-title">{{ safeMud.dialog.title }}</div>
                </div>
                <div class="overlay-body card-body">
                    <div class="dialog-content" v-html="safeMud.dialog.contentHtml"></div>
                    <div class="dialog-actions" v-if="safeMud.dialog.actions.length">
                        <button class="dialog-btn" v-for="action in safeMud.dialog.actions" :key="action.key" @click="safeMud.sendCommand(action.cmd)">
                            {{ action.label }}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, inject } from 'vue';

export default {
  name: 'GameDialogs',
  setup() {
    const mud = inject('mud');
    const textInputValue = ref('');
    
    // ✅ 如果 mud 未定义，提供默认的空对象以避免模板错误
    const safeMud = mud || {
      showGui: false,
      actionBlocks: [],
      dialog: { visible: false },
      closeGui: () => {},
      closeDialog: () => {},
      sendCommand: () => {}
    };
    
    const closeActionBlocks = () => {
      if (safeMud.actionBlocks) {
        safeMud.actionBlocks = [];
      }
    };
    
    const handleActionItemClick = (item) => {
      if (item.cmd) {
        safeMud.sendCommand(item.cmd);
      }
      // 点击按钮后关闭弹窗
      closeActionBlocks();
    };
    
    const handleOverlayClick = (event) => {
      // 检查是否点击了 mud-link
      const anchor = event.target.closest('[data-mud-cmd]');
      if (anchor) {
        const cmd = anchor.getAttribute('data-mud-cmd');
        if (cmd) {
          safeMud.sendCommand(cmd);
          closeActionBlocks();
        }
      }
    };
    
    const handleGuiContentClick = (event) => {
      // 检查是否点击了 mud-link
      const anchor = event.target.closest('[data-mud-cmd]');
      if (anchor) {
        const cmd = anchor.getAttribute('data-mud-cmd');
        if (cmd) {
          safeMud.sendCommand(cmd);
          closeActionBlocks();
        }
      }
    };
    
    const handleGuiActionClick = (item) => {
      if (item.cmd) {
        safeMud.sendCommand(item.cmd);
      }
      // ✅ 点击 GUI 弹窗中的按钮后关闭弹窗
      safeMud.closeGui();
    };
    
    const handleTextInputSubmit = () => {
      if (!safeMud.actionBlocks || safeMud.actionBlocks.length === 0) return;
      
      const item = safeMud.actionBlocks[0].items[0];
      
      // 检查是否是自定义按钮编辑
      if (item.cmdPrefix === 'CUSTOM_EDIT' && item.customData) {
        const { buttonIndex, currentLabel, currentCmd } = item.customData;
        const input = textInputValue.value.trim();
        
        // 解析输入：格式为 "名称,指令"
        const parts = input.split(',');
        if (parts.length >= 2) {
          const label = parts[0].trim();
          const cmd = parts.slice(1).join(',').trim();
          
          if (label && cmd) {
            safeMud.saveCustomButton(buttonIndex, label, cmd);
          } else {
            safeMud.pushToast('请填写快捷键名称和指令');
          }
        } else {
          safeMud.pushToast('格式错误，请使用：名称,指令');
        }
        
        closeActionBlocks();
        return;
      }
      
      // 普通的 001 消息处理
      if (!item || !item.cmdPrefix) return;
      
      // 发送命令：将 cmdPrefix 中的 $txt# 替换为用户输入的内容
      let fullCmd = item.cmdPrefix;
      if (fullCmd.includes('$txt#')) {
        fullCmd = fullCmd.replace('$txt#', textInputValue.value.trim());
      } else {
        fullCmd = `${fullCmd} ${textInputValue.value.trim()}`;
      }
      safeMud.sendCommand(fullCmd);
      
      // 清空输入框并关闭对话框
      textInputValue.value = '';
      closeActionBlocks();
    };
    
    return {
      safeMud,
      textInputValue,
      closeActionBlocks,
      handleActionItemClick,
      handleTextInputSubmit,
      handleOverlayClick,
      handleGuiContentClick,
      handleGuiActionClick
    };
  }
}
</script>