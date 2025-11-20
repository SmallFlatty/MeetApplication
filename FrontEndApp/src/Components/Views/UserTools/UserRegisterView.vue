<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import HomeButton from '@/Common/HomeButton.vue'

const router = useRouter()
function goHome() {
  router.push({ name: 'home' })
}

// ---- Форма ----
const email = ref('')
const password = ref('')
const fullName = ref('')
const role = ref('USER')
const loading = ref(false)
const message = ref('')

// ---- Реєстрація ----
async function registerUser() {
  if (!email.value || !password.value || !fullName.value || !role.value) {
    message.value = '⚠️ Введіть усі поля'
    return
  }

  loading.value = true
  message.value = ''
  try {
    const params = new URLSearchParams({
      email: email.value,
      password: password.value,
      fullName: fullName.value,
      role: role.value
    })

    const res = await fetch(`http://localhost:8080/api/user/create-user?${params.toString()}`, {
      method: 'POST'
    })

    if (res.ok) {
      message.value = '✅ Користувача створено успішно'
      email.value = ''
      password.value = ''
      fullName.value = ''
      role.value = 'USER'
    } else {
      const text = await res.text()
      message.value = '❌ ' + text
    }
  } catch (err) {
    message.value = '❌ Помилка з’єднання з сервером'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <header class="header">
      <div class="title-wrap">
        <h1 class="title">🆕 Register new person</h1>
      </div>
      <div class="header-actions">
        <HomeButton />
      </div>
    </header>

    <section class="registration">
      <h2 class="reg-title">Create new user</h2>

      <input type="text" v-model="fullName" placeholder="Full name" />
      <input type="email" v-model="email" placeholder="Email" />
      <input type="password" v-model="password" placeholder="Password" />

      <label class="role-label">Role:</label>
      <select class="input-role" v-model="role">
        <option value="USER">USER</option>
        <option value="ADMIN">ADMIN</option>
      </select>

      <div class="reg-buttons">
        <button class="btn" :disabled="loading" @click="registerUser">
          {{ loading ? '⏳ Creating...' : 'Register' }}
        </button>
        <button class="btn ghost" @click="goHome">Cancel</button>
      </div>

      <p v-if="message" style="text-align:center; margin-top:10px;">{{ message }}</p>
    </section>
  </div>
</template>

<style scoped>
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

/* 🌌 Основний фон сторінки */
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

/* 🧭 Хедер */
.header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-height: 96px;
}
.title {
  margin: 0;
  font-size: 28px;
  letter-spacing: 0.2px;
}
.title-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-actions {
  display: flex;
  gap: 10px;
}

/* 🧩 Кнопки */
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
.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 🧾 Форма реєстрації */
.registration {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: stretch;
  max-width: 360px;
  margin: 0 auto;
  background: var(--panel);
  padding: 32px;
  border-radius: 22px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.35);
  border: 1px solid #342a57;
}

.reg-title {
  text-align: center;
  color: var(--text);
  font-size: 1.5rem;
  font-weight: 800;
  margin-bottom: 10px;
}

/* 🔤 Поля вводу */
.registration input {
  height: 20px;
  padding: 12px 14px;
  border-radius: 12px;
  background-color: #1d1738;
  color: var(--text);
  border: none;
  outline: none;
  box-shadow: none;
  transition: background 0.3s ease;
}
.registration input:focus {
  background-color: #231b3f;
  box-shadow: 0 0 0 3px var(--ring);
}
.registration input::placeholder {
  color: #cfc8ea88;
}

/* 🎭 Select для ролі */
.role-label {
  color: #cfc8ea;
  font-weight: 500;
  margin-top: 4px;
}
.input-role {
  height: 42px;
  padding: 10px 14px;
  border-radius: 12px;
  border: 1px solid #443c6a;
  background-color: #231b3f;
  color: var(--text);
  font-size: 1rem;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8'%3E%3Cpath fill='%23cfc8ea' d='M1 1l5 5 5-5'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 12px;
  cursor: pointer;
  transition: border 0.3s ease, background 0.3s ease;
}
.input-role:hover {
  border-color: #7066a2;
  background-color: #2c2350;
}
.input-role:focus {
  outline: none;
  border-color: var(--ring);
  box-shadow: 0 0 0 3px var(--ring);
}

/* 🔘 Кнопки внизу */
.reg-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
}

/* 📱 Адаптив */
@media (max-width: 900px) {
  .registration {
    padding: 24px;
    width: 90%;
  }
  .btn {
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

</style>
