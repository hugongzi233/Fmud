<template>
  <div class="shell" :class="{ 'android-mode': screen === 'game' }">
    <div class="appbar" v-if="screen === 'home'">
      <div class="brand"><div class="brand-title">指尖 MUD</div></div>
      <div class="toolbar-actions">
        <button class="soft-btn" @click="openUserCenter">用户中心</button>
        <button class="soft-btn" @click="openSettings">设置</button>
        <button class="primary-btn" @click="loginAndLoadServers">登录并获取分区</button>
      </div>
    </div>

    <HomeScreen />
    <GameScreen />
    <AccountDialogs />
    <GameDialogs />
  </div>
</template>

<script>
import mudAppOptions from '../public/js/main.js';
import HomeScreen from './components/HomeScreen.vue';
import GameScreen from './components/GameScreen.vue';
import AccountDialogs from './components/AccountDialogs.vue';
import GameDialogs from './components/GameDialogs.vue';

export default {
  components: {
    HomeScreen,
    GameScreen,
    AccountDialogs,
    GameDialogs
  },
  provide() {
    return { mud: this };
  },
  ...mudAppOptions
};
</script>
