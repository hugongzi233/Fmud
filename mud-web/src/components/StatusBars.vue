<template>
  <div class="status-bars-container" v-if="mud.statusBars && mud.statusBars.length > 0">
    <!-- 调试：显示状态栏数量 -->
    <div style="color: red; font-size: 12px;">DEBUG: {{ mud.statusBars.length }} bars</div>
    <div class="status-bar-row" v-for="(row, rowIndex) in groupedBars" :key="rowIndex">
      <div 
        class="attrbar" 
        v-for="bar in row" 
        :key="bar.key" 
        @click="bar.cmd ? mud.sendCommand(bar.cmd) : null"
      >
        <!-- 背景层C -->
        <div class="attrbar-bg"></div>
        <!-- 总宽度背景B -->
        <div class="attrbar-total" :style="{ width: bar.percentB + '%' }"></div>
        <!-- 当前值进度条A -->
        <div class="attrbar-current" :style="{ width: bar.percentA + '%', backgroundColor: getBarColor(bar) }"></div>
        <!-- 文本标签 -->
        <div class="attrbar-text">{{ bar.label || bar.name }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { inject, computed } from 'vue';

const mud = inject('mud');

// 将状态栏按columns分组
const groupedBars = computed(() => {
  if (!mud.statusBars || mud.statusBars.length === 0) {
    console.log('[StatusBars] No status bars data, current value:', mud.statusBars);
    return [];
  }
  
  const columns = mud.statusBars[0]?.columns || 2;
  const rows = [];
  
  for (let i = 0; i < mud.statusBars.length; i += columns) {
    rows.push(mud.statusBars.slice(i, i + columns));
  }
  
  return rows;
});

// 根据状态信息获取颜色
const getBarColor = (bar) => {
  // 使用服务器提供的颜色
  if (bar.color) {
    // 转换8位颜色格式 #AARRGGBB 到 CSS rgba
    const color = bar.color;
    if (color.startsWith('#') && color.length === 9) {
      // #99FF0000 -> rgba(255, 0, 0, 0.6)
      const alpha = parseInt(color.slice(1, 3), 16) / 255;
      const r = parseInt(color.slice(3, 5), 16);
      const g = parseInt(color.slice(5, 7), 16);
      const b = parseInt(color.slice(7, 9), 16);
      return `rgba(${r}, ${g}, ${b}, ${alpha})`;
    }
    return color;
  }
  
  // 默认颜色映射
  const name = bar.name || '';
  const colors = {
    '气血': '#ff6b6b',
    '内力': '#4ecdc4',
    '精力': '#ffd93d',
    '精神': '#95e1d3',
    '潜能': '#6bcf7f',
    '经验': '#e6bf6b',
    '实战': '#ff9f43',
    '怒气': '#ff4757',
    '忙乱': '#a4b0be',
    '储潜': '#70a1ff',
    '活跃': '#7bed9f'
  };
  
  for (const [key, color] of Object.entries(colors)) {
    if (name.includes(key)) return color;
  }
  
  return '#6bcf7f'; // 默认绿色
};
</script>

<style scoped>
.status-bars-container {
  padding: 4px 6px;
  background: rgba(30, 25, 20, 0.8);
  border-bottom: 1px solid #555;
}

.status-bar-row {
  display: flex;
  gap: 4px;
  margin-bottom: 3px;
}

.status-bar-row:last-child {
  margin-bottom: 0;
}

/* attrbar - 参考安卓客户端布局 */
.attrbar {
  position: relative;
  flex: 1;
  height: 28px;
  cursor: pointer;
  overflow: hidden;
}

.attrbar:hover {
  opacity: 0.9;
}

/* 背景层C（透明） */
.attrbar-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
}

/* 总宽度背景B（深色） */
.attrbar-total {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: rgba(60, 55, 50, 0.6);
  transition: width 0.3s ease;
}

/* 当前值进度条A（彩色，从左到右填充） */
.attrbar-current {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  transition: width 0.3s ease;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

/* 文本标签 */
.attrbar-text {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #dddddd;
  font-size: 11px;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 0 4px;
  z-index: 1;
}
</style>
