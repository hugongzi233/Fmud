<template>
  <div class="screen" v-if="mud && mud.screen === 'home'">
    <!-- Toast 提示 -->
    <transition name="toast-fade">
      <div v-if="toastMessage" class="toast-container">
        <div class="toast-message">{{ toastMessage }}</div>
      </div>
    </transition>

    <div class="home-grid">
      <section class="card hero">
        <div class="card-header"><div class="card-title">登录</div></div>
        <div class="card-body stack">
          <div class="field-grid">
            <div class="field"><div class="label">用户名</div><input class="input" v-model="loginForm.id" placeholder="请输入账号" /></div>
            <div class="field"><div class="label">密码</div><input class="input" type="password" v-model="loginForm.pass" placeholder="请输入密码" /></div>
            <div class="field"><div class="label">编码</div><select class="select" v-model="mud.settings.encoding"><option value="utf8">utf8</option><option value="gbk">gbk</option><option value="gb2312">gb2312</option></select></div>
            <div class="field"><div class="label">记住账号</div><label class="switch"><input type="checkbox" v-model="mud.settings.rememberAccount" @change="mud.persist" /><span class="switch-track"><span class="switch-thumb"></span></span></label></div>
            <div class="field full"><div class="row end"><button class="soft-btn" @click="mud.showRegister = true">注册</button><button class="primary-btn" @click="handleLogin">登录</button></div></div>
          </div>
        </div>
      </section>

      <section class="card">
        <div class="card-header"><div class="card-title">服务器分区</div><span class="status-pill">{{ mud.servers.length }} 个分区</span></div>
        <div class="card-body">
          <div class="server-scroll">
            <div class="server-list" v-if="mud.servers.length">
              <div class="server-card" v-for="server in mud.servers" :key="server.id">
                <div class="server-meta">
                  <div class="server-name">{{ server.name }}</div>
                  <div class="server-desc"><span class="tag">{{ server.host }}:{{ server.port }}</span><span class="tag gray">{{ server.encoding || mud.settings.encoding }}</span><span class="tag green" v-if="server.online">在线 {{ server.online }}</span><span class="tag gray" v-if="server.status">{{ server.status }}</span></div>
                </div>
                <div class="server-actions"><button class="primary-btn" @click="mud.connectServer(server)">进入</button></div>
              </div>
            </div>
            <div v-else class="compact-item">无</div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { inject, ref } from 'vue';
import { parseLoginServers } from '../../public/js/protocol.js';

const mud = inject('mud');

// Toast 状态（本地管理）
const toastMessage = ref('');
let toastTimer = null;

// 登录表单
const loginForm = ref({ id: '', pass: '' });

// 显示 Toast（本地实现）
function showToast(message) {
  if (toastTimer) {
    clearTimeout(toastTimer);
  }
  toastMessage.value = message;
  toastTimer = setTimeout(() => {
    toastMessage.value = '';
  }, 3000);
}

// 处理登录
function handleLogin() {
  const form = loginForm.value;
  
  // 验证输入
  if (!form.id || !form.pass) {
    showToast('请先输入账号和密码');
    return;
  }
  
  showToast('正在登录...');
  
  fetch(`/api/login?id=${encodeURIComponent(form.id)}&pass=${encodeURIComponent(form.pass)}&page=1`)
    .then((r) => {
      if (!r.ok) {
        throw new Error(`HTTP ${r.status}: ${r.statusText}`);
      }
      return r.text();
    })
    .then((text) => {
      const result = parseLoginServers(text);
      
      // 检查是否有错误
      if (result.error) {
        showToast(result.error);
        return;
      }
      
      // 检查是否获取到服务器列表
      if (!result.servers || result.servers.length === 0) {
        showToast('登录失败');
        return;
      }
      
      // 更新 mud 对象的状态
      mud.servers = result.servers;
      if (result.email) mud.profile.email = result.email;
      if (mud.servers.length) mud.recentServer = mud.servers[0];
      
      // 先保存账号密码到 profile
      mud.profile.id = form.id;
      mud.profile.pass = form.pass;
      
      // 根据设置决定是否保留登录信息
      if (!mud.settings.rememberAccount) {
        // 如果不记住账号，清空 profile 中的敏感信息
        mud.profile.id = '';
        mud.profile.pass = '';
        loginForm.value = { id: '', pass: '' };
      } else {
        // 如果记住账号，同步到表单
        loginForm.value = { id: form.id, pass: form.pass };
      }
      
      mud.persist();
      showToast(`成功获取 ${mud.servers.length} 个服务器分区`);
    })
    .catch((error) => {
      showToast(`登录失败: ${error.message || '未知错误'}`);
    });
}

</script>

<style scoped>
/* Toast 提示样式 */
.toast-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  pointer-events: none;
}

.toast-message {
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* Toast 淡入淡出动画 */
.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: opacity 0.3s ease;
}

.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
}
</style>
