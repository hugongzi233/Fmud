<template>
    <div>
        <!-- 注册 -->
        <div class="overlay" v-if="mud.showRegister"><div class="overlay-card"><div class="overlay-header"><div><div class="card-title">注册</div><div class="card-sub">提交后会把账号回填到登录框。</div></div><button class="icon-btn" @click="mud.showRegister = false">×</button></div><div class="overlay-body stack"><div class="modal-grid"><div class="stack"><div class="field"><div class="label">ID</div><input class="input" v-model="mud.registerForm.id" /></div><div class="field"><div class="label">密码</div><input class="input" type="password" v-model="mud.registerForm.pass" /></div><div class="field"><div class="label">确认密码</div><input class="input" type="password" v-model="mud.registerForm.pass2" /></div><div class="field"><div class="label">手机号</div><input class="input" v-model="mud.registerForm.phone" /></div></div><div class="stack"><div class="field"><div class="label">邮箱</div><input class="input" v-model="mud.registerForm.email" /></div><div class="field"><div class="label">游戏昵称</div><input class="input" v-model="mud.registerForm.name" /></div><div class="field"><div class="label">性别</div><select class="select" v-model="mud.registerForm.sex"><option>男性</option><option>女性</option></select></div></div></div><div class="row end"><button class="soft-btn" @click="mud.showRegister = false">取消</button><button class="primary-btn" @click="mud.registerAccount">确认注册</button></div></div></div></div>

        <!-- 设置 -->
        <div class="overlay" v-if="mud.showSettings"><div class="overlay-card"><div class="overlay-header"><div><div class="card-title">设置</div><div class="card-sub">编码、界面、聊天高度和快捷命令。</div></div><button class="icon-btn" @click="mud.showSettings = false">×</button></div><div class="overlay-body stack"><div class="modal-grid"><div class="stack"><div class="field"><div class="label">默认编码</div><select class="select" v-model="mud.settings.encoding"><option value="utf8">utf8</option><option value="gbk">gbk</option><option value="gb2312">gb2312</option></select></div><div class="field"><div class="label">界面模式</div><select class="select" v-model="mud.settings.mode"><option value="dark">dark</option><option value="day">day</option></select></div><div class="field"><div class="label">聊天区高度</div><select class="select" v-model.number="mud.settings.chatHeight"><option :value="3">1/3</option><option :value="5">1/5</option><option :value="8">1/8</option></select></div></div><div class="stack"><div class="compact-item"><div class="row between"><strong>快捷命令</strong><span class="tag gray">localStorage</span></div><div class="mini-note">可自定义常用命令，不影响服务器协议。</div></div><div class="field-grid"><div class="field" v-for="slot in mud.quickSlots" :key="slot.id"><div class="label">{{ slot.label }}</div><input class="input" v-model="slot.cmd" /></div></div></div></div><div class="row end"><button class="soft-btn" @click="mud.showSettings = false">关闭</button><button class="primary-btn" @click="mud.saveSettings">保存设置</button></div></div></div></div>

        <!-- 用户中心 -->
        <div class="overlay" v-if="mud.showUserCenter">
            <div class="overlay-card">
                <div class="overlay-header">
                    <div>
                        <div class="card-title">用户中心</div>
                        <div class="card-sub">账号信息管理与自定义服务器。</div>
                    </div>
                    <button class="icon-btn" @click="mud.showUserCenter = false">×</button>
                </div>
                <div class="overlay-body">
                    <!-- 用户中心标签页 -->
                    <div class="tabs" style="margin-bottom:14px">
                        <button class="tab" :class="{ active: mud.ucTab !== 'server' }" @click="mud.ucTab = 'account'">账号信息</button>
                        <button class="tab" :class="{ active: mud.ucTab === 'server' }" @click="mud.ucTab = 'server'">自定义服务器</button>
                    </div>

                    <!-- 账号信息 tab -->
                    <div v-if="mud.ucTab !== 'server'">
                        <div class="modal-grid">
                            <!-- 左：账号信息（只读展示） -->
                            <div class="stack">
                                <div class="compact-item">
                                    <div class="row between"><strong>账号</strong><span class="tag">{{ mud.profile.id || '—' }}</span></div>
                                </div>
                                <div class="compact-item">
                                    <div class="row between"><strong>密码</strong><span class="tag gray">******</span></div>
                                </div>
                                <div class="compact-item">
                                    <div class="row between"><strong>手机号</strong><span class="tag">{{ mud.profile.phone || '未绑定' }}</span></div>
                                </div>
                                <div class="compact-item">
                                    <div class="row between"><strong>邮箱</strong><span class="tag">{{ mud.profile.email || '未绑定' }}</span></div>
                                </div>
                                <div class="compact-item" v-if="mud.profile.myserver">
                                    <div class="row between"><strong>自定义服务器</strong></div>
                                    <div class="mini-note">{{ mud.profile.myserver }}</div>
                                </div>
                            </div>
                            <!-- 右：操作按钮 -->
                            <div class="stack">
                                <button class="primary-btn" style="width:100%" @click="mud.fetchUserInfo().then(() => mud.pushToast('已刷新用户信息'))">刷新用户信息</button>
                                <div class="field">
                                    <div class="label">新密码</div>
                                    <div class="row" style="gap:6px">
                                        <input class="input" type="password" v-model="mud.ucNewPass" placeholder="留空则不修改" />
                                        <button class="soft-btn" style="min-width:64px" @click="mud.updateUcenterPass(mud.ucNewPass); mud.ucNewPass = ''">修改</button>
                                    </div>
                                </div>
                                <div class="field">
                                    <div class="label">新手机号</div>
                                    <div class="row" style="gap:6px">
                                        <input class="input" v-model="mud.ucNewPhone" placeholder="输入新手机号" />
                                        <button class="soft-btn" style="min-width:64px" @click="mud.updateUcenterPhone(mud.ucNewPhone); mud.ucNewPhone = ''">绑定</button>
                                    </div>
                                </div>
                                <div class="field">
                                    <div class="label">新邮箱</div>
                                    <div class="row" style="gap:6px">
                                        <input class="input" v-model="mud.ucNewEmail" placeholder="输入新邮箱" />
                                        <button class="soft-btn" style="min-width:64px" @click="mud.updateUcenterEmail(mud.ucNewEmail); mud.ucNewEmail = ''">绑定</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="separator"></div>
                        <!-- 最近分区 + 服务器列表 -->
                        <div class="stack">
                            <div class="compact-item">
                                <div class="row between"><strong>最近分区</strong><span class="tag gray">last_part</span></div>
                                <div class="mini-note">{{ mud.recentServerLabel }}</div>
                            </div>
                            <div class="server-list" style="max-height:240px;overflow:auto;padding-right:2px">
                                <div class="server-card" v-for="server in mud.servers" :key="'uc-' + server.id">
                                    <div class="server-meta">
                                        <div class="server-name">{{ server.name }}</div>
                                        <div class="server-desc"><span class="tag">{{ server.host }}:{{ server.port }}</span></div>
                                    </div>
                                    <div class="server-actions"><button class="primary-btn" @click="mud.connectServer(server)">进入</button></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 自定义服务器 tab -->
                    <div v-if="mud.ucTab === 'server'">
                        <div class="stack">
                            <div class="compact-item" v-if="mud.profile.myserver">
                                <div class="row between"><strong>当前自定义服务器</strong></div>
                                <div class="mini-note" style="white-space:pre-wrap">{{ mud.profile.myserver }}</div>
                            </div>
                            <div class="compact-item" v-else>
                                <div class="mini-note">暂无自定义服务器，可从右侧添加。</div>
                            </div>
                            <div class="field">
                                <div class="label">服务器名称</div>
                                <input class="input" v-model="mud.ucServerName" placeholder="例如：我的测试服" />
                            </div>
                            <div class="field">
                                <div class="label">IP / 域名</div>
                                <input class="input" v-model="mud.ucServerHost" placeholder="例如：127.0.0.1" />
                            </div>
                            <div class="field">
                                <div class="label">端口</div>
                                <input class="input" v-model="mud.ucServerPort" placeholder="例如：23" />
                            </div>
                            <div class="row end" style="margin-top:8px">
                                <button class="soft-btn" @click="mud.ucTab = 'account'">取消</button>
                                <button class="primary-btn" @click="mud.saveUcenterServer">保存服务器</button>
                            </div>
                        </div>
                    </div>

                    <div class="row end" style="margin-top:14px">
                        <button class="soft-btn" @click="mud.showUserCenter = false">关闭</button>
                    </div>
                </div>
            </div>
        </div>

        <div class="overlay" v-if="mud.showServerEditor"><div class="overlay-card"><div class="overlay-header"><div><div class="card-title">添加分区</div><div class="card-sub">手工录入服务器仅作本地保存，不影响登录获取的官方分区。</div></div><button class="icon-btn" @click="mud.showServerEditor = false">×</button></div><div class="overlay-body stack"><div class="field-grid"><div class="field full"><div class="label">名称</div><input class="input" v-model="mud.serverDraft.name" /></div><div class="field"><div class="label">IP / 域名</div><input class="input" v-model="mud.serverDraft.host" /></div><div class="field"><div class="label">端口</div><input class="input" v-model="mud.serverDraft.port" /></div><div class="field"><div class="label">编码</div><select class="select" v-model="mud.serverDraft.encoding"><option value="utf8">utf8</option><option value="gbk">gbk</option><option value="gb2312">gb2312</option></select></div><div class="field"><div class="label">备注</div><textarea class="textarea" v-model="mud.serverDraft.status"></textarea></div></div><div class="row end"><button class="soft-btn" @click="mud.showServerEditor = false">取消</button><button class="primary-btn" @click="mud.addManualServer">保存</button></div></div></div></div>
    </div>
</template>

<script setup>
import { inject } from 'vue';

const mud = inject('mud');
</script>
