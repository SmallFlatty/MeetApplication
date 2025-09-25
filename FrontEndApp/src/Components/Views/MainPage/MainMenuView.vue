<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import MainLogo from '@/Assets/MainLogo.png'
import {onMounted} from "vue";


type Role = 'ADMIN' | 'WORKER'
interface UserInfo { id: number; name: string; role: Role }

const router = useRouter()
const API = 'http://localhost:8080/api/user'
const user = ref<UserInfo | null>(null)
const showLogin = ref(false)
const fullName = ref('')
const password = ref('')
const loginError = ref<string | null>(null)


onMounted(() => {
  checkSession()
})

async function signIn() {
  loginError.value = null

  try {
    // 1) Надсилаємо POST-запит на /login для створення сесії
    const loginRes = await fetch(`${API}/login`, {
      method: 'POST',
      credentials: 'include', // важливо для cookie-сесії
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        fullName: fullName.value,
        password: password.value
      })
    })

    if (!loginRes.ok) {
      loginError.value = 'Invalid name or password'
      return
    }

    const data = await loginRes.json()
    const role = (data.role === 'ADMIN' ? 'ADMIN' : 'WORKER') as Role

    user.value = {
      id: data.id,
      name: data.fullName,
      role: role
    }

    showLogin.value = false
    fullName.value = ''
    password.value = ''
  } catch (err) {
    console.error(err)
    loginError.value = 'Login failed. Check API/CORS/network.'
  }
}

  async function logout() {
    await fetch(`${API}/logout`, {
      method: 'POST',
      credentials: 'include'
    })
    user.value = null
    showLogin.value = true
  }

  async function checkSession() {
    try {
      const res = await fetch(`${API}/checkme`, { credentials: 'include' })
      if (!res.ok) return

      const data = await res.json()
      user.value = {
        id: data.id,
        name: data.fullName,
        role: data.role
      }
    } catch (err) {
      console.warn('Session check failed')
    }
  }

const actions = computed(() => {
  const base = [
    { key:'my-meetings', label:'My Meetings', desc:'View and manage your meetings', to:{ name:'meetings.mine' }, icon:'📅' },
    { key:'create-meeting', label:'Create Meeting', desc:'Schedule a new meeting', to:{ name:'meetings.create' }, icon:'➕' },
  ]

  // для WORKER
  if (user.value?.role === 'WORKER') {
    base.push({
      key:'create-meeting-worker',
      label:'Create Meet for Worker',
      desc:'Submit a request for a meeting',
      to:{ name:'meetings.worker' },
      icon:'👨‍💻'
    })
  }

  // для ADMIN
  if (user.value?.role === 'ADMIN') {
    return [
      ...base,
      { key:'all-meetings', label:'All Meetings', desc:`Browse every user's meetings`, to:{ name:'meetings.all' }, icon:'🗂️' },
      { key:'users', label:'Users', desc:'Manage users and roles', to:{ name:'users' }, icon:'👥' },
      {
        key:'register-person',
        label:'Register new person',
        desc:'Add a new user to the system',
        to:{ name:'users.register' },
        icon:'🆕'
      },
      {
        key:'create-meeting-for-worker',
        label:'Create Meet for a Worker',
        desc:'Schedule a meeting on behalf of a worker',
        to:{ name:'meetings.createForWorker' },
        icon:'🛠️'
      }
    ]
  }

  return base
})

function go(to:any){ if (to?.name) router.push(to) }
</script>

<template>
  <div class="page">
    <header class="header">
      <!-- LEFT: user card -->
      <div class="title-wrap">
        <div v-if="user" class="user-card">
          <div class="user-card__icon">👋</div>
          <div class="user-card__meta">
            <div class="user-card__line">
              Welcome, <strong>{{ user.name }}</strong>
            </div>
            <span class="role" :class="user.role.toLowerCase()">{{ user.role }}</span>
          </div>
        </div>
        <p v-else class="subtitle">Welcome, Guest</p>
      </div>

      <div class="logo-bar">
        <img :src="MainLogo" alt="RPS Meet logo" class="logo-img" />
      </div>

      <div class="header-actions">
        <button v-if="user" class="btn ghost" @click="router.push({ name: 'report' })">Report Problem</button>
        <button v-if="user" class="btn ghost" @click="router.push({ name: 'support' })">Tech Support</button>
        <button
            v-if="user"
            class="btn ghost"
            @click="router.push({ name: 'profile', query: {fullName: user.name } })">Profile
        </button>
        <button v-if="user" class="btn danger" @click="logout">Logout</button>
        <button v-if="!user && !showLogin" class="btn" @click="showLogin = true">Sign In</button>
      </div>
    </header>



    <!-- Login panel -->
    <div v-if="!user && showLogin" class="login">
      <input v-model="fullName" placeholder="Full name" autocomplete="username" />
      <input v-model="password" type="password" placeholder="Password" autocomplete="current-password" />
      <button class="btn" @click="signIn">Sign In</button>
      <p v-if="loginError" class="error">{{ loginError }}</p>
    </div>

    <!-- Role-aware actions -->
    <section v-if="user" class="grid">
      <article v-for="item in actions" :key="item.key" class="card" @click="go(item.to)">
        <div class="icon">{{ item.icon }}</div>
        <h2 class="card-title">{{ item.label }}</h2>
        <p class="card-desc">{{ item.desc }}</p>
        <div class="cta">Open</div>
      </article>
    </section>

  </div>
</template>

<style>
:root {
  --bg-1: #0e0b18;
  --bg-2: #1a1430;
  --panel: #241d3e;
  --text: #f4f0ff;
  --muted: #c1b6e6;

  --violet-1: #bba1ff;
  --violet-2: #9f7fff;
  --violet-3: #7a4fe0;

  --danger-1: #ff738f;
  --danger-2: #ff466d;

  --ring: rgba(160, 100, 255, 0.35);
}

/* Фон сторінки */
.page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 28px;
  color: var(--text);
  background:
      radial-gradient(1000px 600px at -10% -10%, #2d234f 0%, transparent 60%),
      radial-gradient(1000px 800px at 120% 20%, #1c1335 0%, transparent 60%),
      linear-gradient(180deg, var(--bg-2), var(--bg-1));
}

/* Шапка */
.header{
  position: relative;           /* нове */
  display:flex; align-items:center; justify-content:space-between;
  gap:16px;
  min-height: 96px;             /* щоб хедер не “стрибав” по висоті */
}
.title {
  margin: 0;
  font-size: 28px;
  letter-spacing: 0.2px;
}
.title-wrap{ display:flex; align-items:center; gap:12px; }
.subtitle {
  margin: 0;
  color: var(--muted);
}

.role {
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, var(--violet-1), var(--violet-2));
  box-shadow: 0 0 0 2px rgba(160, 100, 255, 0.2) inset;
}
.header-actions {
  display: flex;
  gap: 10px;
}

/* Кнопки */
.btn {
  border: none;
  color: white;
  padding: 10px 16px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  background: linear-gradient(135deg, var(--violet-1), var(--violet-2));
  box-shadow: 0 4px 16px rgba(160, 100, 255, 0.3);
  transition: all 0.2s ease;
}
.btn:hover {
  transform: translateY(-2px);
  filter: brightness(1.05);
}
.btn:active {
  transform: translateY(0);
}

.btn.ghost {
  background: transparent;
  color: var(--violet-1);
  border: 1px solid #44356b;
}
.btn.danger {
  background: linear-gradient(135deg, var(--danger-1), var(--danger-2));
  box-shadow: 0 6px 18px rgba(255, 70, 109, 0.3);
}

/* Логін */
.login {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 12px;
  align-items: center;
}
.login input {
  height: 42px;
  padding: 10px 14px;
  border-radius: 12px;
  background-color: #1d1738; /* 🔄 Однотонне, без градієнта */
  color: var(--text);
  border: none;
  outline: none;
  box-shadow: none;
  transition: background 0.3s ease;
}
.login input:focus {
  background-color: #231b3f;
  box-shadow: 0 0 0 3px var(--ring);
}

.login input::placeholder {
  color: #cfc8ea88;
}
.error {
  color: #ff9b9b;
  font-weight: 700;
  grid-column: 1 / -1;
}

/* Сітка */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
}
.card {
  background: var(--panel);
  border: 1px solid #342a57;
  border-radius: 22px;
  padding: 24px;
  display: grid;
  grid-template-rows: auto auto 1fr auto;
  gap: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.card:hover {
  transform: translateY(-4px);
  border-color: var(--violet-2);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.35), 0 0 0 3px var(--ring);
}
.user-card{
  display:inline-flex; align-items:center; gap:12px;
  padding:10px 14px; border-radius:14px;
  background: linear-gradient(180deg, #231b3f, #1c1632);
  border:1px solid #3c2f5e;
  box-shadow: 0 6px 18px rgba(0,0,0,.25), 0 0 0 3px var(--ring);
}
.user-card__icon{
  width:36px; height:36px; border-radius:10px;
  display:grid; place-items:center; font-size:20px; line-height:1;
  background: linear-gradient(135deg, var(--violet-1), var(--violet-2));
  box-shadow: 0 6px 16px rgba(160,100,255,.35);
}
.user-card__meta{ display:flex; align-items:center; gap:10px; }
.user-card__line{ color:var(--text); font-weight:600; }
.role{
  padding:3px 10px; border-radius:999px; font-size:12px; font-weight:800; color:white;
  background:linear-gradient(135deg, var(--violet-1), var(--violet-2));
  box-shadow:0 0 0 2px rgba(160,100,255,.20) inset;
}
.icon {
  font-size: 40px;
  line-height: 1;
}
.card-title {
  font-size: 22px;
  font-weight: 800;
  margin: 6px 0 2px;
}
.card-desc {
  font-size: 15px;
  color: var(--muted);
}
.cta {
  margin-top: 8px;
  align-self: start;
  background: linear-gradient(135deg, var(--violet-1), var(--violet-2));
  color: #fff;
  font-weight: 800;
  padding: 8px 12px;
  border-radius: 10px;
}
.tips h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #d8cffb;
}
.tips ul {
  margin: 0;
  padding-left: 18px;
  color: #b7aedf;
  font-size: 13px;
}

@media (max-width: 900px) {
  .login {
    grid-template-columns: 1fr;
  }
  .btn-primary {
    width: 100%;
  }
}
body, html, .page {
  background-color: #0e0b18 !important;
  background-image: none !important;
}

input,
input:focus,
input:focus-visible,
input:focus-within,
input:-webkit-autofill,
input:-webkit-autofill:focus {
  all: unset;
  appearance: none;
  background-color: #1d1738 !important;
  color: var(--text) !important;
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  border-radius: 12px !important;
  padding: 10px 14px;
  font: inherit;
  transition: none !important;
}

.logo-bar{
  position:absolute;            /* нове */
  left:50%; top:50%;
  transform: translate(-50%, -50%);
  display:flex; align-items:center; justify-content:center;
  pointer-events:none;          /* щоб не перекривало клікабельні елементи */
  z-index:0;                    /* під кнопками/юзером, якщо треба */
}

/* Лого 2× (було 54px) */
.logo-img{
  height:108px;                 /* 2 рази більше */
  max-width: 70vw;
  object-fit: contain;
  filter: drop-shadow(0 10px 22px rgba(160,100,255,.28));
  user-select:none; pointer-events:none;
}

.page { position: relative; }


</style>

