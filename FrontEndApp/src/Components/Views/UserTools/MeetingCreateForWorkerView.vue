<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "axios";
import HomeButton from "@/Common/HomeButton.vue";
import { useRouter } from "vue-router";

const router = useRouter();

const title = ref("");
const startAt = ref("");
const endAt = ref("");
const customerName = ref("");
const createdBy = ref("");
const createdAt = ref(new Date().toISOString().slice(0, 16));
const userId = ref("");

const users = ref<any[]>([]);
const loadingUsers = ref(true);
const message = ref("");


async function loadUsers() {
  try {
    const res = await axios.get("http://localhost:8080/api/user/get-users");
    users.value = res.data;
  } catch (e) {
    console.error("❌ Failed to load users", e);
  } finally {
    loadingUsers.value = false;
  }
}

async function createMeeting() {
  if (!createdBy.value) {
    message.value = "⚠️ 'Created By' is required.";
    return;
  }
  if (!title.value || !startAt.value || !endAt.value || !customerName.value || !userId.value) {
    message.value = "⚠️ Please fill all required fields.";
    return;
  }

  const payload = {
    title: title.value,
    startAt: startAt.value,
    endAt: endAt.value,
    customerName: customerName.value,
    createdBy: createdBy.value,
    createdAt: createdAt.value,
    user: { id: userId.value }
  };

  try {
    await axios.post("http://localhost:8080/api/meets/create", payload);
    message.value = "✅ Meeting successfully created!";
    title.value = "";
    startAt.value = "";
    endAt.value = "";
    customerName.value = "";
    userId.value = "";
  } catch (e) {
    console.error("❌ Failed to create meeting", e);
    message.value = "❌ Failed to create meeting.";
  }
}

function goHome() {
  router.push({ name: "home" });
}

onMounted(loadUsers);
</script>

<template>
  <div class="page">
    <header class="header">
      <div class="title-wrap">
        <h1 class="title">👨‍💼 Create Meeting for a Worker</h1>
      </div>
      <div class="header-actions">
        <HomeButton />
      </div>
    </header>

    <div class="content">
      <form class="registration" @submit.prevent="createMeeting">
        <h2 class="reg-title">Meeting details</h2>

        <input v-model="title" placeholder="Title *" />
        <input type="datetime-local" v-model="startAt" placeholder="Start At *" />
        <input type="datetime-local" v-model="endAt" placeholder="End At *" />
        <input v-model="customerName" placeholder="Customer Name *" />
        <input v-model="createdBy" placeholder="Created By *" required />

        <label class="role-label">Select User</label>
        <select v-model="userId" class="input-role">
          <option disabled value="">-- choose user --</option>
          <option v-for="u in users" :key="u[0]" :value="u[0]">
            {{ u[0] }} — {{ u[2] }} ({{ u[3] }})
          </option>
        </select>

        <div class="reg-buttons">
          <button type="submit" class="btn">Create</button>
          <button type="button" class="btn ghost" @click="goHome">Cancel</button>
        </div>

        <p v-if="message" style="text-align:center; margin-top:10px;">{{ message }}</p>
      </form>

      <div class="user-table">
        <h2 class="reg-title">Available Users</h2>
        <div v-if="loadingUsers">Loading users...</div>
        <table v-else>
          <thead>
          <tr>
            <th>ID</th>
            <th>Email</th>
            <th>Full Name</th>
            <th>Role</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="u in users" :key="u[0]">
            <td>{{ u[0] }}</td>
            <td>{{ u[1] }}</td>
            <td>{{ u[2] }}</td>
            <td>{{ u[3] }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>

.content {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.user-table {
  flex: 1;
  background: var(--panel);
  padding: 24px;
  border-radius: 22px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.35);
  border: 1px solid #342a57;
  color: var(--text);
  overflow-x: auto;
}

.user-table table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
  font-size: 15px;
}

.user-table th,
.user-table td {
  padding: 8px 12px;
  border-bottom: 1px solid #3a3163;
}

.user-table th {
  color: var(--violet-1);
  font-weight: 600;
}

.user-table tr:hover td {
  background: #1f1735;
}
</style>
