<template>
    <div>
        <div class="overlay" v-if="mud.showGui">
            <div class="overlay-card gui-card">
                <div class="overlay-body gui-body">
                    <button class="gui-close-btn" @click="mud.closeGui">×</button>
                    <div class="gui-content" v-if="mud.guiHtml" v-html="mud.guiHtml"></div>
                    <div class="gui-actions" v-if="mud.guiActions1.length || mud.guiActions2.length" :style="{ gridTemplateColumns: `repeat(${mud.guiColumns || 3}, minmax(0, 1fr))` }">
                        <div class="action-section" v-if="mud.guiActions1.length" :style="{ gridTemplateColumns: `repeat(${mud.guiColumns || 3}, minmax(0, 1fr))` }">
                            <button class="command-btn" v-for="item in mud.guiActions1" :key="item.key" @click="mud.sendCommand(item.cmd)">
                                <div v-html="item.labelHtml"></div>
                                <div class="caption" v-if="item.captionHtml" v-html="item.captionHtml"></div>
                                <div class="caption" v-else>{{ item.caption || item.cmd }}</div>
                            </button>
                        </div>
                        <div class="action-section" v-if="mud.guiActions2.length" :style="{ gridTemplateColumns: `repeat(${mud.guiColumns || 3}, minmax(0, 1fr))` }">
                            <button class="command-btn" v-for="item in mud.guiActions2" :key="item.key" @click="mud.sendCommand(item.cmd)">
                                <div v-html="item.labelHtml"></div>
                                <div class="caption" v-if="item.captionHtml" v-html="item.captionHtml"></div>
                                <div class="caption" v-else>{{ item.caption || item.cmd }}</div>
                            </button>
                        </div>
                    </div>
                    <div class="compact-item" v-if="!mud.guiHtml && !mud.guiActions1.length && !mud.guiActions2.length">无</div>
                </div>
            </div>
        </div>

        <div class="overlay" v-if="mud.dialog.visible">
            <div class="overlay-card">
                <div class="overlay-header">
                    <div class="card-title">{{ mud.dialog.title }}</div>
                    <button class="icon-btn" @click="mud.closeDialog">×</button>
                </div>
                <div class="overlay-body card-body">
                    <div class="dialog-content" v-html="mud.dialog.contentHtml"></div>
                    <div class="dialog-actions" v-if="mud.dialog.actions.length">
                        <button class="dialog-btn" v-for="action in mud.dialog.actions" :key="action.key" @click="mud.sendCommand(action.cmd)">
                            {{ action.label }}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
  name: 'GameDialogs',
  inject: ['mud']
}
</script>

<style scoped>
</style>
