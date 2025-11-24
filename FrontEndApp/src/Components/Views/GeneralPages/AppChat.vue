<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import SockJS from 'sockjs-client';
import { Client, Frame } from '@stomp/stompjs';
import CryptoJS from 'crypto-js'


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
    // 📡 Надсилаємо через WebSocket
    stompClient.publish({
      destination: '/app/chat.send',
      body: JSON.stringify(payload)
    });

    // 💾 Одночасно зберігаємо у базу
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

function formatTime(iso?: string) {
  if (!iso) return '';
  const d = new Date(iso);
  const now = new Date();

  const isToday = d.toDateString() === now.toDateString();
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);

  if (isToday)
    return 'сьогодні ' + d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  if (d.toDateString() === yesterday.toDateString())
    return 'вчора ' + d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

  return (
      d.getDate().toString().padStart(2, '0') +
      '.' +
      (d.getMonth() + 1).toString().padStart(2, '0') +
      ' ' +
      d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  );
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
            <div v-for="(m, i) in messages" :key="i" class="message" :class="{ mine: m.senderName === senderName }">
              <div class="meta">
                <span class="sender">{{ m.senderName ?? 'Anonymous' }}</span>
                <span class="time">[{{ formatTime(m.time) }}]</span>
              </div>
              <div class="text">{{ m.content }}</div>
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
          <h3 class="panel-title">Team status</h3>

          <div v-if="userStatuses.length === 0" class="empty">No data yet</div>

          <div v-for="u in userStatuses" :key="u.fullName" class="status-row">
            <span class="dot" :class="u.status === 'Online' ? 'green' : 'red'"></span>
            <span class="name">{{ u.fullName }}</span>
            <span class="badge" :class="u.status === 'Online' ? 'online' : 'offline'">
        {{ u.status }}
      </span>
          </div>
        </div>
      </aside>

    </div>
  </div>
</template>

<style scoped>
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

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
  max-width: 60%;
  align-self: flex-start;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 10px 14px;
  color: #f4f0ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.25);
  word-wrap: break-word;
}

.message .meta {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 4px;
}

.message .sender {
  font-weight: 700;
  color: #a6b3ff;
}

.message .text {
  white-space: pre-wrap;
  font-size: 14px;
  line-height: 1.4;
}

.message .time {
  color: rgba(255,255,255,0.65);
  font-size: 12px;
}
.composer {
  flex: 0 0 auto;
  display:flex;
  gap:8px;
  padding:12px;
  border-top: 1px solid rgba(255,255,255,0.04);
  background: linear-gradient(180deg, rgba(0,0,0,0.18), rgba(0,0,0,0.12));
}

.message.mine {
  align-self: flex-end;
  border-color: rgba(100, 160, 255, 0.6);
  background: linear-gradient(135deg, rgba(40, 70, 180, 0.35), rgba(90, 120, 255, 0.2));
  box-shadow: 0 4px 12px rgba(50, 90, 255, 0.25);
}

.right-col .panel {
  height: 100%;
  min-height: 0;
  overflow: auto;
  border-radius: 12px;
  padding: 12px;
  background: #fff;
  border: 1.2px solid rgba(255,255,255,0.85);
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
.btn-back:active { transform: translateY(0); }
.btn-back:disabled {
  background: linear-gradient(135deg, #ffb6b6 0%, #ff9a9a 100%);
  cursor: not-allowed;
  opacity: 0.75;
}

.messages::-webkit-scrollbar { width: 10px; }
.messages::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.25); border-radius: 8px; }

@media (max-width: 900px) {
  .content-grid { grid-template-columns: 1fr; }
  .right-col .panel { min-height: 160px; }
  .chat-wrapper { min-height: 360px; }
}

.panel-title { margin: 0 0 8px; font-weight: 800; }

.status-row {
  display: grid;
  grid-template-columns: 16px 1fr auto;
  align-items: center;
  gap: 10px;
  padding: 8px 6px;
  border-bottom: 1px dashed rgba(0,0,0,0.06);
}

.status-row:last-child { border-bottom: none; }

.dot {
  width: 10px; height: 10px; border-radius: 50%;
  box-shadow: 0 0 0 2px rgba(0,0,0,0.06) inset;
}
.dot.green { background: #22c55e; }  /* Online */
.dot.red   { background: #ef4444; }  /* Offline */

.name { font-weight: 600; }

.badge {
  font-size: 12px; padding: 4px 8px; border-radius: 999px;
  border: 1px solid transparent; user-select: none;
}
.badge.online  { background: rgba(34,197,94,0.12);  color: #166534; border-color: rgba(34,197,94,0.25); }
.badge.offline { background: rgba(239,68,68,0.12);  color: #7f1d1d; border-color: rgba(239,68,68,0.25); }


</style>
