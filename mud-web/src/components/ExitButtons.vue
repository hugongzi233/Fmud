<template>
  <div class="exit-buttons-container">
    <!-- 九宫格方向按钮 -->
    <div class="exits-grid">
      <button class="exit-btn nw" v-if="hasExit('northwest')" @click="sendExit('northwest')">
        <span v-html="getExitLabel('northwest')"></span>
      </button>
      <div v-else class="exit-placeholder"></div>
      
      <button class="exit-btn n" v-if="hasExit('north')" @click="sendExit('north')">
        <span v-html="getExitLabel('north')"></span>
      </button>
      <div v-else class="exit-placeholder"></div>
      
      <button class="exit-btn ne" v-if="hasExit('northeast')" @click="sendExit('northeast')">
        <span v-html="getExitLabel('northeast')"></span>
      </button>
      <div v-else class="exit-placeholder"></div>
      
      <button class="exit-btn w" v-if="hasExit('west')" @click="sendExit('west')">
        <span v-html="getExitLabel('west')"></span>
      </button>
      <div v-else class="exit-placeholder"></div>
      
      <button class="exit-btn center" @click="mud.sendCommand('look')">
        <span v-html="mud.locationName || '观察'"></span>
      </button>
      
      <button class="exit-btn e" v-if="hasExit('east')" @click="sendExit('east')">
        <span v-html="getExitLabel('east')"></span>
      </button>
      <div v-else class="exit-placeholder"></div>
      
      <button class="exit-btn sw" v-if="hasExit('southwest')" @click="sendExit('southwest')">
        <span v-html="getExitLabel('southwest')"></span>
      </button>
      <div v-else class="exit-placeholder"></div>
      
      <button class="exit-btn s" v-if="hasExit('south')" @click="sendExit('south')">
        <span v-html="getExitLabel('south')"></span>
      </button>
      <div v-else class="exit-placeholder"></div>
      
      <button class="exit-btn se" v-if="hasExit('southeast')" @click="sendExit('southeast')">
        <span v-html="getExitLabel('southeast')"></span>
      </button>
      <div v-else class="exit-placeholder"></div>
    </div>
  </div>
</template>

<script setup>
import { inject } from 'vue';

const mud = inject('mud');

// 检查方向是否存在
const hasExit = (direction) => {
  return mud.exits && mud.exits.some(e => e.key === direction && e.visible);
};

// 获取出口命令
const getExitCmd = (direction) => {
  const exit = mud.exits && mud.exits.find(e => e.key === direction);
  return exit ? (exit.cmd || direction) : direction;
};

// 获取出口标签（优先使用labelHtml）
const getExitLabel = (direction) => {
  const exit = mud.exits && mud.exits.find(e => e.key === direction);
  if (!exit) return '';
  
  // 优先使用labelHtml（带颜色）
  if (exit.labelHtml && exit.labelHtml.trim()) {
    return exit.labelHtml;
  }
  
  // 其次使用label（纯文本）
  if (exit.label && exit.label.trim()) {
    return exit.label;
  }
  
  return '';
};

// 发送出口命令
const sendExit = (direction) => {
  const cmd = getExitCmd(direction);
  mud.sendCommand(cmd);
};
</script>

<style scoped>
.exit-buttons-container {
  padding: 0;
}

.exits-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(3, 45px);
  gap: 4px;
}

.exit-btn {
  background: linear-gradient(135deg, #4a3a2a 0%, #3a2f25 100%);
  border: 1px solid #666;
  color: #ddbb99;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 4px;
  font-weight: 500;
  padding: 4px;
}

.exit-btn:hover {
  background: linear-gradient(135deg, #6a5a4a 0%, #5a4f45 100%);
  border-color: #e6bf6b;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.exit-btn.center {
  background: linear-gradient(135deg, #5a4a3a 0%, #4a3f35 100%);
  font-weight: bold;
  font-size: 14px;
}

.exit-placeholder {
  background: transparent;
}

.exit-btn.nw { grid-column: 1; grid-row: 1; }
.exit-btn.n { grid-column: 2; grid-row: 1; }
.exit-btn.ne { grid-column: 3; grid-row: 1; }
.exit-btn.w { grid-column: 1; grid-row: 2; }
.exit-btn.center { grid-column: 2; grid-row: 2; }
.exit-btn.e { grid-column: 3; grid-row: 2; }
.exit-btn.sw { grid-column: 1; grid-row: 3; }
.exit-btn.s { grid-column: 2; grid-row: 3; }
.exit-btn.se { grid-column: 3; grid-row: 3; }
</style>
