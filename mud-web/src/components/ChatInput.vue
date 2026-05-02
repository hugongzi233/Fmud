<template>
  <div class="chat-input-container">
    <!-- 频道选择 -->
    <div class="chat-channels">
      <button 
        class="chat-channel-btn" 
        :class="{ active: mud.chatChannel === 'chat' }"
        @click="mud.chatChannel = 'chat'"
      >
        聊天
      </button>
      <button 
        class="chat-channel-btn" 
        :class="{ active: mud.chatChannel === 'rumor' }"
        @click="mud.chatChannel = 'rumor'"
      >
        传闻
      </button>
      <button 
        class="chat-channel-btn" 
        :class="{ active: mud.chatChannel === 'party' }"
        @click="mud.chatChannel = 'party'"
      >
        帮派
      </button>
      <button 
        class="chat-channel-btn" 
        :class="{ active: mud.chatChannel === 'family' }"
        @click="mud.chatChannel = 'family'"
      >
        家族
      </button>
    </div>
    
    <!-- 输入框 -->
    <div class="chat-input-row">
      <input 
        type="text" 
        class="chat-input-field"
        v-model="mud.chatInputText"
        @keyup.enter="sendChatMessage"
        placeholder="输入聊天内容..."
      />
      <button class="chat-send-btn" @click="sendChatMessage">发送</button>
    </div>
  </div>
</template>

<script setup>
import { inject } from 'vue';

const mud = inject('mud');

// 发送聊天消息
const sendChatMessage = () => {
  if (!mud.chatInputText || !mud.chatInputText.trim()) return;
  
  const channelCmds = {
    chat: 'chat',
    rumor: 'rumor',
    party: 'party',
    family: 'family'
  };
  
  const cmd = channelCmds[mud.chatChannel] || 'chat';
  mud.sendCommand(`${cmd} ${mud.chatInputText}`);
  mud.chatInputText = '';
};
</script>

<style scoped>
.chat-input-container {
  padding: 6px;
  background: rgba(30, 25, 20, 0.8);
  border-top: 1px solid #555;
}

.chat-channels {
  display: flex;
  gap: 4px;
  margin-bottom: 6px;
  overflow-x: auto;
}

.chat-channel-btn {
  flex-shrink: 0;
  background: rgba(50, 45, 40, 0.7);
  border: 1px solid #555;
  color: #ddbb99;
  padding: 3px 8px;
  font-size: 11px;
  cursor: pointer;
  border-radius: 3px;
  transition: all 0.2s;
}

.chat-channel-btn:hover {
  background: rgba(80, 70, 60, 0.8);
  border-color: #e6bf6b;
}

.chat-channel-btn.active {
  background: rgba(230, 191, 107, 0.3);
  border-color: #e6bf6b;
  color: #e6bf6b;
  font-weight: bold;
}

.chat-input-row {
  display: flex;
  gap: 4px;
}

.chat-input-field {
  flex: 1;
  background: rgba(30, 25, 20, 0.8);
  border: 1px solid #555;
  color: #ddbb99;
  padding: 5px 8px;
  font-size: 12px;
  border-radius: 3px;
}

.chat-input-field:focus {
  outline: none;
  border-color: #e6bf6b;
}

.chat-send-btn {
  background: linear-gradient(135deg, #5a4a3a 0%, #4a3f35 100%);
  border: 1px solid #666;
  color: #e6bf6b;
  padding: 5px 12px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 3px;
  transition: all 0.2s;
}

.chat-send-btn:hover {
  background: linear-gradient(135deg, #7a6a5a 0%, #6a5f55 100%);
  border-color: #e6bf6b;
}
</style>
