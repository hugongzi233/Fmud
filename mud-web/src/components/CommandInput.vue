<template>
  <div class="command-input-container">
    <input 
      type="text" 
      class="command-input-field"
      v-model="mud.commandInputText"
      @keyup.enter="sendCommand"
      placeholder="输入命令..."
    />
    <button class="command-send-btn" @click="sendCommand">执行</button>
  </div>
</template>

<script setup>
import { inject } from 'vue';

const mud = inject('mud');

// 发送命令
const sendCommand = () => {
  if (!mud.commandInputText || !mud.commandInputText.trim()) return;
  
  mud.sendCommand(mud.commandInputText);
  mud.commandInputText = '';
};
</script>

<style scoped>
.command-input-container {
  display: flex;
  gap: 4px;
  padding: 6px;
  background: rgba(30, 25, 20, 0.8);
  border-top: 1px solid #555;
}

.command-input-field {
  flex: 1;
  background: rgba(30, 25, 20, 0.8);
  border: 1px solid #555;
  color: #ddbb99;
  padding: 6px 10px;
  font-size: 13px;
  border-radius: 3px;
}

.command-input-field:focus {
  outline: none;
  border-color: #e6bf6b;
}

.command-send-btn {
  background: linear-gradient(135deg, #5a4a3a 0%, #4a3f35 100%);
  border: 1px solid #666;
  color: #e6bf6b;
  padding: 6px 16px;
  font-size: 13px;
  cursor: pointer;
  border-radius: 3px;
  transition: all 0.2s;
  font-weight: bold;
}

.command-send-btn:hover {
  background: linear-gradient(135deg, #7a6a5a 0%, #6a5f55 100%);
  border-color: #e6bf6b;
}
</style>
