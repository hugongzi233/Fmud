<template>
  <div class="object-list-container">
    <!-- 对象列表 -->
    <div class="objects-section" v-if="mud.targets && mud.targets.length > 0">
      <!-- <div class="section-header">当前场景</div> -->
      <button class="obj-btn" v-for="target in mud.targets" :key="target.key" @click="handleObjectClick(target)">
        <span v-html="target.labelHtml"></span>
        <!-- 血条容器 -->
        <div class="hp-bar-container" v-if="target.qiXue">
          <div class="hp-bar-fill" :style="{ width: getHpPercent(target.qiXue) + '%' }"></div>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup>
import { inject } from 'vue';

const mud = inject('mud');

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
  position: relative;
  width: 100%;
  display: block;
}

.obj-btn:hover {
  background: rgba(80, 70, 60, 0.8);
  border-color: #e6bf6b;
}

/* 血条容器 */
.hp-bar-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

/* 血条填充 */
.hp-bar-fill {
  height: 100%;
  background-color: #ff4500;
  transition: width 0.3s ease;
}
</style>
