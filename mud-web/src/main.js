import { createApp } from 'vue';
import App from './App.vue';
import '../public/styles/app.css';

const app = createApp(App);
const vm = app.mount('#app');
try { window.__mud = vm; } catch (e) {}
