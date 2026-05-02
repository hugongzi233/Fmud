<template>
  <div class="quick-cmds-container">
    <button 
      class="quick-cmd-btn" 
      v-for="(cmd, idx) in (mud.customCmds || []).slice(0, 5)" 
      :key="idx" 
      @click="mud.sendCommand(cmd.cmd)"
    >
      <span v-html="cmd.label || cmd.cmd"></span>
    </button>
  </div>
</template>

<script setup>
import { inject } from 'vue';

const mud = inject('mud');
</script>

<style scoped>
.quick-cmds-container {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.quick-cmd-btn {
  background: rgba(50, 45, 40, 0.7);
  border: 1px solid #555;
  color: #ddbb99;
  padding: 3px 8px;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 3px;
  min-width: 50px;
  text-align: center;
}

.quick-cmd-btn:hover {
  background: rgba(80, 70, 60, 0.8);
  border-color: #e6bf6b;
}
</style>
