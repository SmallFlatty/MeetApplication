<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import SockJS from 'sockjs-client';
import { Client, Frame } from '@stomp/stompjs';
import CryptoJS from 'crypto-js'
import '@/Assets/global.css'

const SECRET_KEY = 'MASHONOCKA'


type ChatMessageDTO = {
  content: string;
  senderName: string;
  time?: string;
};

const props = defineProps<{
  senderName?: string;
  wsUrl?: string;
}>();
const router = useRouter();
const route = useRoute();

function goBack() {
  router.back();
}

const senderName = ref(props.senderName || 'Anonymous');

watch(
    () => route.query.senderName,
    (newVal) => {
      if (typeof newVal === 'string' && newVal.trim() !== '') {
        const decrypted = decryptName(newVal)
        if (decrypted) {
          senderName.value = decrypted
          console.log('🔓 Decrypted senderName:', decrypted)
        }
      }
    },
    { immediate: true }
);

const wsUrl = props.wsUrl ?? (import.meta.env.VITE_WS_URL ?? 'http://localhost:8080/wb');

const connected = ref(false);
const messages = ref<ChatMessageDTO[]>([]);
const text = ref('');
const onlineUsers = ref<string[]>([]);

let stompClient: Client | null = null;
let subscription: any = null;

type UserStatus = { fullName: string; status: 'Online' | 'Offline' };

const userStatuses = ref<UserStatus[]>([]);
let statusTimer: ReturnType<typeof setInterval> | null = null;


async function loadStatuses() {
  try {
    const res = await fetch('http://localhost:8080/api/status/users-status', {
      credentials: 'include'
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const rows = await res.json();
    userStatuses.value = Array.isArray(rows)
        ? rows.map((r: any) => ({
          fullName: String(r?.[0] ?? ''),
          status: String(r?.[1] ?? 'Offline') as 'Online' | 'Offline'
        }))
        : [];
  } catch (e) {
    console.warn('Failed to load user statuses:', e);
  }
}

function decryptName(encrypted: string): string | null {
  try {
    const bytes = CryptoJS.AES.decrypt(decodeURIComponent(encrypted), SECRET_KEY)
    return bytes.toString(CryptoJS.enc.Utf8)
  } catch (e) {
    console.error('Decryption failed:', e)
    return null
  }
}

function scrollMessagesToBottom(smooth = true) {
  nextTick(() => {
    const el = document.querySelector('.messages') as HTMLElement | null;
    if (!el) return;
    const isNearBottom = el.scrollHeight - el.scrollTop - el.clientHeight < 20;
    if (!isNearBottom) return;
    el.scrollTo({ top: el.scrollHeight, behavior: smooth ? 'smooth' : 'auto' });
  });
}

function connect() {
  if (stompClient) {
    try {
      stompClient.deactivate();
    } catch {}
    stompClient = null;
  }

  const socketFactory = () => new SockJS(wsUrl);
  stompClient = new Client({
    webSocketFactory: socketFactory,
    reconnectDelay: 5000,
    heartbeatIncoming: 0,
    heartbeatOutgoing: 20000,
    onConnect: () => {
      connected.value = true;
      subscription = stompClient?.subscribe('/topic/chat', (message) => {
        if (!message.body) return;
        const payload = JSON.parse(message.body) as ChatMessageDTO;
        messages.value.push(payload);
        if (payload.senderName && !onlineUsers.value.includes(payload.senderName))
          onlineUsers.value.push(payload.senderName);
        if (messages.value.length > 1000) messages.value.shift();
        scrollMessagesToBottom();
      });
    },
    onWebSocketClose: () => (connected.value = false),
    onDisconnect: () => (connected.value = false),
  });

  stompClient.activate();
}


function disconnect() {
  try {
    subscription?.unsubscribe();
  } catch {}
  subscription = null;
  if (stompClient) {
    try {
      stompClient.deactivate();
    } catch {}
    stompClient = null;
  }
  connected.value = false;
}
async function loadHistory() {
  try {
    const res = await fetch('http://localhost:8080/api/chat/get-all-messages');
    if (!res.ok) throw new Error('HTTP ' + res.status);

    const data = await res.json();
    messages.value = data.map((m: any) => ({
      senderName: m.senderName,
      content: m.context,
      time: m.sendTime
    }));

    scrollMessagesToBottom(false);
    console.log('💾 Loaded history:', messages.value.length, 'messages');
  } catch (e) {
    console.error('❌ Failed to load chat history:', e);
  }
}

async function sendMessage() {
  const msg = text.value.trim();
  if (!msg || !stompClient || !connected.value) return;

  const activeName =
      (typeof route.query.senderName === 'string' && route.query.senderName.trim() !== '')
          ? route.query.senderName
          : senderName.value || 'Anonymous';

  const decryptedName = decryptName(activeName) ?? 'Anonymous';

  const payload: ChatMessageDTO = {
    content: msg,
    senderName: decryptedName,
    time: ''
  };

  try {
    stompClient.publish({
      destination: '/app/chat.send',
      body: JSON.stringify(payload)
    });

    await fetch('http://localhost:8080/api/chat/save-message', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        senderName: decryptedName,
        message: msg
      })
    });

    text.value = '';
  } catch (e) {
    console.error('Failed to send message', e);
  }
}

function handleAvatarError(e: Event) {
  const img = e.target as HTMLImageElement
  img.style.display = "none"
  const fallback = img.parentElement?.querySelector(".fallback") as HTMLElement
  if (fallback) fallback.style.display = "flex"
}

function formatOnlyTime(iso?: string) {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

function formatDate(iso?: string) {
  if (!iso) return '';
  const d = new Date(iso);
  const now = new Date();

  const isToday = d.toDateString() === now.toDateString();
  if (isToday) return "Today";

  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  if (d.toDateString() === yesterday.toDateString()) return "Yesterday";

  return d.toLocaleDateString('en-US', { day: 'numeric', month: 'long' }); // e.g. 25 June
}

function shouldShowDate(index: number) {
  if (index === 0) return true;

  const prev = messages.value[index - 1].time;
  const curr = messages.value[index].time;

  const prevDate = new Date(prev).toDateString();
  const currDate = new Date(curr).toDateString();

  return prevDate !== currDate;
}

function scrollToPickedDate(event: Event) {
  const input = event.target as HTMLInputElement;
  if (!input.value) return;

  const picked = new Date(input.value).toDateString();

  nextTick(() => {
    const blocks = document.querySelectorAll(".msg-block");

    for (const block of blocks) {
      const time = block.getAttribute("data-time");
      if (!time) continue;

      const same = new Date(time).toDateString() === picked;
      if (same) {
        block.scrollIntoView({ behavior: "smooth", block: "start" });
        return;
      }
    }
  });
}


onMounted(async () => {
  await nextTick();

  const currentName = route.query.senderName;
  if (typeof currentName === 'string' && currentName.trim() !== '') {
    const decrypted = decryptName(currentName);
    if (decrypted) {
      senderName.value = decrypted;
      console.log('🔓 Initialized senderName:', senderName.value);
    }
  }

  await loadHistory();

  connect();
  loadStatuses();
  statusTimer = setInterval(loadStatuses, 5000);
});


onUnmounted(() => {
  disconnect();
  if (statusTimer) {
    clearInterval(statusTimer);
    statusTimer = null;
  }
});
</script>

<template>
  <div class="app-chat-page">
    <div class="topbar">
      <button class="btn btn-back" @click="goBack">← Back</button>
      <h1 class="page-title"></h1>
    </div>

    <div class="content-grid">
      <main class="left-col">
        <div class="chat-wrapper">
          <div class="messages" role="log" aria-live="polite">
            <div
                v-for="(m, i) in messages"
                :key="i"
                class="msg-block"
                :data-time="m.time"
            >
              <div v-if="shouldShowDate(i)" class="date-separator">
                {{ formatDate(messages[i].time) }}
              </div>
              <div
                  class="message"
                  :class="{ mine: m.senderName === senderName }"
              >
                <div class="bubble">
                  <div class="meta">
                    <span class="sender">{{ m.senderName }}</span>

                    <div class="avatar-wrapper">
                      <img
                          class="avatar"
                          :src="`http://localhost:8080/UsersAvatar/${m.senderName.replaceAll(' ', '')}.png`"
                          @error="handleAvatarError($event)"
                      />
                      <div class="fallback">?</div>
                    </div>
                  </div>

                  <div class="text">{{ m.content }}</div>
                  <div class="time-after">
                    {{ formatOnlyTime(m.time) }}
                  </div>

                </div>
              </div>
            </div>


          </div>

          <div class="composer">
            <input
                v-model="text"
                @keyup.enter="sendMessage"
                placeholder="Type your message..."
                class="input"
            />
            <button class="btn send" @click="sendMessage" :disabled="!connected || !text.trim()">Send</button>
          </div>
        </div>
      </main>

      <aside class="right-col">
        <div class="panel">
          <div class="date-switcher">
            <h3 class="panel-title">Jump to date</h3>

            <input
                type="date"
                class="date-picker"
                @change="scrollToPickedDate($event)"
            />
          </div>

          <div v-if="userStatuses.length === 0" class="empty">No data yet</div>
        <div class="users-scroll">
          <div v-for="u in userStatuses" :key="u.fullName" class="status-row">
            <span class="dot" :class="u.status === 'Online' ? 'green' : 'red'"></span>
            <div class="user-info">
              <div class="avatar-wrapper small">
                <img
                    class="avatar small"
                    :src="`http://localhost:8080/UsersAvatar/${u.fullName.replaceAll(' ', '')}.png`"
                    @error="handleAvatarError($event)"
                />
                <div class="fallback small">?</div>
              </div>

              <span class="name">{{ u.fullName }}</span>
            </div>
            <span class="badge" :class="u.status === 'Online' ? 'online' : 'offline'">
        {{ u.status }}
      </span>
          </div>
        </div>
      </div>
      </aside>

    </div>
  </div>
</template>

<style scoped>

.app-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  gap: 16px;
  padding: 12px 12px 20px 8px;
  box-sizing: border-box;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 16px;
  align-items: stretch;
  flex: 1 1 auto;
  min-height: 0;
  height: auto;
}

.left-col {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.chat-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-radius: 12px;
  border: 1.2px solid rgba(255, 255, 255, 0.1);
  background: linear-gradient(135deg, #0b0611 0%, #1b0f26 50%, #2b1650 100%);
  overflow: hidden;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message {
  align-self: flex-start !important;
  max-width: 70%;
  background: none !important;
  border: none !important;
  box-shadow: none !important;
  padding: 0 !important;
  position: relative;
}

.bubble {
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  border-radius: 12px;
  padding: 10px 14px;
}

.message.mine .bubble {
  background: linear-gradient(
      135deg,
      rgba(80, 90, 180, 0.35),
      rgba(120, 140, 220, 0.20)
  );
  border: 1px solid rgba(140, 160, 255, 0.55);
  box-shadow: 0 4px 12px rgba(60, 90, 255, 0.25);
}

.meta {
  display: flex !important;
  justify-content: flex-start !important;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.sender {
  font-weight: 700;
  color: #e2ddff;
  font-size: 13px;
}

.text {
  font-size: 14px;
  color: #f4f0ff;
  white-space: pre-wrap;
  text-align: left !important;
}

.time-after {
  font-size: 9px;
  color: rgba(240, 240, 255, 0.65);
  margin-top: 4px;
  text-align: right;
  line-height: 1;
  padding-right: 2px;
}

.avatar-wrapper {
  width: 26px;
  height: 26px;
  position: relative;
}

.avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgba(255,255,255,0.25);
}

.fallback {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: rgba(255,255,255,0.12);
  border: 1px solid rgba(255,255,255,0.25);
  color: #ddd;
  display: none;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.composer {
  flex: 0 0 auto;
  display:flex;
  gap:8px;
  padding:12px;
  border-top: 1px solid rgba(255,255,255,0.04);
  background: linear-gradient(180deg, rgba(0,0,0,0.18), rgba(0,0,0,0.12));
}

.right-col .panel {
  height: 100%;
  overflow: auto;
  border-radius: 12px;
  padding: 16px;

  background: linear-gradient(
      135deg,
      rgba(40, 20, 70, 0.85),
      rgba(20, 10, 45, 0.9)
  );

  border: 1px solid rgba(180, 150, 255, 0.25);
  box-shadow: 0 0 18px rgba(100, 60, 200, 0.18);

  color: #eee;
}

.btn-back {
  background: linear-gradient(135deg, #ff4c4c 0%, #ff1f3b 100%);
  color: #fff;
  border: 1px solid rgba(255,255,255,0.12);
  padding: 8px 12px;
  border-radius: 10px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(255,60,60,0.18);
  transition: transform .12s ease, box-shadow .12s ease, filter .12s;
}

.btn-back:hover {
  transform: translateY(-2px);
  filter: brightness(1.05);
  box-shadow: 0 12px 26px rgba(255,60,60,0.26);
}

.messages::-webkit-scrollbar { width: 10px; }
.messages::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.25); border-radius: 8px; }

.status-row {
  display: grid;
  grid-template-columns: 16px 1fr auto;
  align-items: center;
  gap: 10px;
  padding: 8px 6px;
  border-bottom: 1px dashed rgba(0,0,0,0.06);
}

.dot {
  width: 10px; height: 10px; border-radius: 50%;
  box-shadow: 0 0 0 2px rgba(0,0,0,0.06) inset;
}
.dot.green { background: #22c55e; }
.dot.red   { background: #ef4444; }

.badge {
  font-size: 12px; padding: 4px 8px; border-radius: 999px;
}

@media (max-width: 900px) {
  .content-grid { grid-template-columns: 1fr; }
  .right-col .panel { min-height: 160px; }
  .chat-wrapper { min-height: 360px; }
}

.panel-title {
  margin: 0 0 12px;
  font-weight: 800;
  font-size: 18px;
  color: #d9ccff;
  text-shadow: 0 0 6px rgba(200, 160, 255, 0.4);
}

.status-row {
  display: grid;
  grid-template-columns: 14px 1fr auto;
  align-items: center;
  gap: 10px;

  padding: 10px 6px;
  border-radius: 10px;

  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);

  transition: 0.18s ease;
}

.status-row:not(:last-child) {
  margin-bottom: 8px;
}

.status-row:hover {
  background: rgba(255,255,255,0.12);
  border-color: rgba(255,255,255,0.18);
  transform: translateX(4px);
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.green {
  background: #22c55e;
  box-shadow: 0 0 6px rgba(34, 197, 94, 0.4);
}

.dot.red {
  background: #ef4444;
  box-shadow: 0 0 6px rgba(239, 68, 68, 0.4);
}

.name {
  font-size: 14px;
  font-weight: 600;
  color: #efe7ff;
}

.badge {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid currentColor;
  font-weight: 600;
}

.badge.online {
  color: #22c55e;
  background: rgba(34,197,94,0.12);
}

.badge.offline {
  color: #ef4444;
  background: rgba(239,68,68,0.12);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar-wrapper.small {
  width: 22px;
  height: 22px;
  position: relative;
}

.avatar.small {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgba(255,255,255,0.25);
}

.fallback.small {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(255,255,255,0.12);
  border: 1px solid rgba(255,255,255,0.25);
  color: #ddd;
  display: none;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
}

.date-separator {
  text-align: center;
  color: rgba(255,255,255,0.55);
  font-size: 12px;
  margin: 12px 0;
  padding: 4px 0;
  letter-spacing: 0.5px;

  text-shadow: 0 0 8px rgba(200,160,255,0.25);
}

.msg-block {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: auto;
  max-width: 100%;
}

.date-separator {
  align-self: center;
  max-width: 100%;
}

.date-switcher {
  margin-bottom: 20px;
  padding: 12px;
  border-radius: 12px;

  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.12);
  box-shadow: 0 0 8px rgba(100,60,200,0.15);
}

.date-item:hover {
  background: rgba(255,255,255,0.12);
  border-color: rgba(255,255,255,0.18);
  transform: translateX(4px);
}

.date-picker {
  width: 100%;
  padding: 8px 10px;
  font-size: 14px;
  border-radius: 8px;

  background: rgba(255,255,255,0.08);
  color: #f4eaff;

  border: 1px solid rgba(255,255,255,0.15);
  outline: none;

  margin-bottom: 14px;

  box-sizing: border-box;

  transition: 0.15s;
}

.date-picker:hover {
  background: rgba(255,255,255,0.12);
  border-color: rgba(255,255,255,0.25);
}

.date-picker::-webkit-calendar-picker-indicator {
  filter: invert(1) brightness(1.3);
  cursor: pointer;
}

.right-col {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

.date-switcher {
  flex: 0 0 auto;
  margin-bottom: 12px;
}

.users-scroll {
  flex: 1 1 auto;
  overflow-y: auto;
  padding-right: 6px;
}

.users-scroll::-webkit-scrollbar {
  width: 8px;
}
.users-scroll::-webkit-scrollbar-thumb {
  background: rgba(255,255,255,0.15);
  border-radius: 10px;
}
</style>