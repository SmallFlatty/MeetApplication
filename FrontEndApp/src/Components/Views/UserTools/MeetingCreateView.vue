<script setup lang="ts">
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
import HomeButton from "@/Common/HomeButton.vue";

// Router
const router = useRouter();

// 🧠 Поля форми
const title = ref("");
const startAt = ref("");
const endAt = ref("");
const customerName = ref("");
const createdBy = ref("");
const createdAt = ref(new Date().toISOString().slice(0, 16)); // поточна дата/час
const userId = ref(""); // ID користувача, якому створюється зустріч

const loading = ref(false);
const message = ref("");

// 🛰️ Надсилання на бекенд
async function createMeeting() {
  if (!title.value || !startAt.value || !endAt.value || !customerName.value || !userId.value) {
    message.value = "⚠️ Please fill all required fields.";
    return;
  }

  loading.value = true;
  message.value = "";

  try {
    const payload = {
      title: title.value,
      startAt: startAt.value,
      endAt: endAt.value,
      customerName: customerName.value,
      createdBy: createdBy.value || "support",
      createdAt: createdAt.value,
      user: { id: userId.value }
    };

    const res = await axios.post("http://localhost:8080/api/meets/create", payload);
    message.value = `✅ Meeting '${res.data.title}' created successfully.`;

    // Можеш перекинути користувача назад
    // router.push({ name: 'home' })
  } catch (err) {
    console.error(err);
    message.value = "❌ Failed to create meeting. Check console for details.";
  } finally {
    loading.value = false;
  }
}

function goHome() {
  router.push({ name: "home" });
}
</script>

<template>
  <div class="page">
    <header class="header">
      <div class="title-wrap">
        <h1 class="title">➕ Create Meeting</h1>
      </div>
      <div class="header-actions">
        <HomeButton />
      </div>
    </header>

    <form class="registration" @submit.prevent="createMeeting">
      <h2 class="reg-title">Meeting details</h2>

      <input v-model="title" placeholder="Title *" />
      <input type="datetime-local" v-model="startAt" placeholder="Start At *" />
      <input type="datetime-local" v-model="endAt" placeholder="End At *" />
      <input v-model="customerName" placeholder="Customer Name *" />
      <input v-model="userId" placeholder="User ID *" />
      <input v-model="createdBy" placeholder="Created By (optional)" />

      <div class="reg-buttons">
        <button type="submit" class="btn" :disabled="loading">Create</button>
        <button type="button" class="btn ghost" @click="goHome">Cancel</button>
      </div>

      <p v-if="message" style="text-align:center; margin-top:10px;">{{ message }}</p>
    </form>
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

/* 🌙 Фон/інпут дефолт */
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
