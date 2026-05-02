<template>
    <div>
        <div class="overlay" v-if="mud.showGui">
            <div class="overlay-card gui-card">
                <div class="overlay-header">
                    <div>
                        <div class="card-title">{{ mud.guiTitle || 'GUI' }}</div>
                    </div>
                    <button class="icon-btn" @click="mud.closeGui">×</button>
                </div>
                <div class="overlay-body gui-body">
                    <div class="gui-tabs" v-if="mud.guiHtml || mud.guiActions1.length || mud.guiActions2.length">
                        <button class="gui-tab" :class="{ active: mud.guiTab !== 'actions' }" @click="mud.guiTab = 'main'" v-if="mud.guiHtml">窗口内容</button>
                        <button class="gui-tab" :class="{ active: mud.guiTab === 'actions' }" @click="mud.guiTab = 'actions'" v-if="mud.guiActions1.length || mud.guiActions2.length">按钮区</button>
                    </div>
                    <div class="gui-main" v-if="mud.guiTab !== 'actions' && mud.guiHtml" v-html="mud.guiHtml"></div>
                    <div class="gui-actions" v-if="mud.guiTab === 'actions'">
                        <div class="action-section" v-if="mud.guiActions1.length">
                            <button class="command-btn" v-for="item in mud.guiActions1" :key="item.key" @click="mud.sendCommand(item.cmd)">
                                <div v-html="item.labelHtml"></div>
                                <div class="caption" v-if="item.captionHtml" v-html="item.captionHtml"></div>
                                <div class="caption" v-else>{{ item.caption || item.cmd }}</div>
                            </button>
                        </div>
                        <div class="action-section" v-if="mud.guiActions2.length">
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
                    <div>
                        <div class="card-title">对话框</div>
                        <div class="card-sub">数字输入、物品、经验和确认/取消。</div>
                    </div>
                    <button class="icon-btn" @click="mud.closeDialog">×</button>
                </div>
                <div class="overlay-body">
                    <div class="dialog-grid">
                        <div class="dialog-body">
                            <div class="mini-list">
                                <div class="mini-item" v-for="(block, index) in mud.dialog.blocks" :key="index" v-html="mud.renderBlock(block)"></div>
                            </div>
                            <div class="dialog-items" v-if="mud.dialog.items.length">
                                <button class="item-card" :class="mud.itemQualityClass(item.bg)" v-for="item in mud.dialog.items" :key="item.key" @click="mud.sendCommand('litem ' + item.key)">
                                    <img class="item-image" :src="item.image" :alt="item.label" @error="item.image = mud.fallbackItemImage" />
                                    <div class="item-label">{{ item.label }}</div>
                                </button>
                            </div>
                        </div>
                        <div class="stack">
                            <div class="compact-item" v-if="mud.dialog.exp">
                                <div class="row between"><strong>经验</strong><span class="tag green">EXP</span></div>
                                <div class="mini-note">{{ mud.dialog.exp }}</div>
                            </div>
                            <div class="compact-item" v-if="mud.dialog.money">
                                <div class="row between"><strong>金钱</strong><span class="tag orange">GOLD</span></div>
                                <div class="mini-note">{{ mud.dialog.money }}</div>
                            </div>
                            <div class="compact-item" v-if="mud.dialog.hasQuantity">
                                <div class="row between"><strong>数量</strong><span class="tag gray">$N</span></div>
                                <div class="mini-note">将替换确认指令中的 $N</div>
                            </div>
                            <div class="field" v-if="mud.dialog.numberNeeded">
                                <div class="label">数字输入</div>
                                <input class="input" type="number" v-model="mud.dialog.input" placeholder="请输入数字" />
                            </div>
                            <div class="compact-item" v-if="mud.dialog.okCommands.length">
                                <div class="row between"><strong>确认指令</strong><span class="tag gray">ok11</span></div>
                                <div class="mini-note" style="white-space:pre-wrap">{{ mud.dialog.okCommands.join(' / ') }}</div>
                            </div>
                            <div class="row end" style="margin-top:auto;">
                                <button class="soft-btn" @click="mud.cancelDialog">取消</button>
                                <button class="primary-btn" @click="mud.submitDialog">确定</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="overlay" v-if="mud.popup.visible">
            <div class="overlay-card">
                <div class="overlay-header">
                    <div>
                        <div class="card-title">快捷菜单</div>
                        <div class="card-sub">服务器推送的弹出菜单。</div>
                    </div>
                    <button class="icon-btn" @click="mud.popup.visible = false">×</button>
                </div>
                <div class="overlay-body">
                    <div class="command-grid" :style="{ gridTemplateColumns: 'repeat(' + mud.popup.columns + ', minmax(0, 1fr))' }">
                        <button class="command-btn" v-for="item in mud.popup.items" :key="item.key" @click="mud.sendCommand(item.cmd)">
                            <div v-html="item.labelHtml"></div>
                            <div class="caption" v-if="item.captionHtml" v-html="item.captionHtml"></div>
                            <div class="caption" v-else>{{ item.caption || item.cmd }}</div>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <div class="overlay" v-if="mud.showMap">
            <div class="overlay-card">
                <div class="overlay-header">
                    <div>
                        <div class="card-title">地图</div>
                        <div class="card-sub">服务器推送的地图或文字视图。</div>
                    </div>
                    <button class="icon-btn" @click="mud.showMap = false">×</button>
                </div>
                <div class="overlay-body"><div class="dialog-body" v-html="mud.mapHtml"></div></div>
            </div>
        </div>

        <div class="overlay" v-if="mud.showMoreText">
            <div class="overlay-card">
                <div class="overlay-header">
                    <div>
                        <div class="card-title">更多文本</div>
                        <div class="card-sub">分页文本与附加描述。</div>
                    </div>
                    <button class="icon-btn" @click="mud.showMoreText = false">×</button>
                </div>
                <div class="overlay-body"><div class="dialog-body" v-html="mud.moreHtml"></div></div>
            </div>
        </div>

        <div class="overlay" v-if="mud.showWeb">
            <div class="overlay-card">
                <div class="overlay-header">
                    <div>
                        <div class="card-title">网页</div>
                        <div class="card-sub">服务器下发的网页内容。</div>
                    </div>
                    <button class="icon-btn" @click="mud.showWeb = false">×</button>
                </div>
                <div class="overlay-body"><div class="web-frame"><iframe :src="mud.webUrl"></iframe></div></div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { inject } from 'vue';

const mud = inject('mud');
</script>
