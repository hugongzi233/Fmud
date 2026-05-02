<template>
  <div class="object-list-container">
    <!-- 对象列表 -->
    <div class="objects-section" v-if="mud.targets && mud.targets.length > 0">
      <!-- <div class="section-header">当前场景</div> -->
      <button class="obj-btn" v-for="target in mud.targets" :key="target.key" @click="handleObjectClick(target)">
        <span v-html="target.labelHtml"></span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { inject } from 'vue';

const mud = inject('mud');

// 处理对象点击
const handleObjectClick = (target) => {
  console.log('点击对象:', target);
  if (target.cmd) {
    mud.sendCommand(target.cmd);
  } else if (target.key) {
    mud.sendCommand(`look ${target.key}`);
  }
};
</script>

<style scoped>
.object-list-container {
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

.objects-section {
  display: flex;
  flex-direction: column;
  gap: 3px;
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
</style>
