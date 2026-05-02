<template>
  <div class="screen" v-if="mud && mud.screen === 'home'">
    <div class="home-grid">
      <section class="card hero">
        <div class="card-header"><div class="card-title">登录</div></div>
        <div class="card-body stack">
          <div class="field-grid">
            <div class="field"><div class="label">用户名</div><input class="input" v-model="mud.profile.id" placeholder="请输入账号" /></div>
            <div class="field"><div class="label">密码</div><input class="input" type="password" v-model="mud.profile.pass" placeholder="请输入密码" /></div>
            <div class="field"><div class="label">编码</div><select class="select" v-model="mud.settings.encoding"><option value="utf8">utf8</option><option value="gbk">gbk</option><option value="gb2312">gb2312</option></select></div>
            <div class="field"><div class="label">记住账号</div><label class="switch"><input type="checkbox" v-model="mud.settings.rememberAccount" @change="mud.persist" /><span class="switch-track"><span class="switch-thumb"></span></span></label></div>
            <div class="field full"><div class="row end"><button class="soft-btn" @click="mud.showRegister = true">注册</button><button class="primary-btn" @click="mud.loginAndLoadServers">登录</button></div></div>
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
import { inject } from 'vue';

const mud = inject('mud');
</script>
