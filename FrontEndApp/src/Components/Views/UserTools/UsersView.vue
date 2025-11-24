<script setup lang="ts">
import { ref, onMounted } from "vue"

interface User {
  id: number
  email: string
  full_name: string
  role: "ADMIN" | "WORKER"
}

const users = ref<User[]>([])

onMounted(async () => {
  await loadUsers()
})

async function loadUsers() {
  try {
    const res = await fetch("http://localhost:8080/api/user/get-users", {
      credentials: "include"
    })
    if (!res.ok) throw new Error("❌ Помилка завантаження")

    const raw = await res.json()
    users.value = raw.map((row: any[]) => ({
      id: row[0],
      email: row[1],
      full_name: row[2],
      role: row[3]
    }))
  } catch (err) {
    console.error(err)
  }
}

async function deleteUser(userId: number) {
  if (!confirm("Точно видалити цього користувача?")) return
  try {
    const res = await fetch(
        `http://localhost:8080/api/user/delete-user?userId=${userId}`,
        {
          method: "DELETE",
          credentials: "include"
        }
    )
    if (!res.ok) throw new Error("❌ Не вдалося видалити")

    await loadUsers()
  } catch (err) {
    console.error(err)
    alert("Помилка при видаленні користувача")
  }
}
</script>

<template>
  <div class="page">
    <header class="header">
      <div class="title-wrap">
        <h1 class="title">👥 Users List</h1>
      </div>
      <div class="header-actions">
        <button class="btn danger" @click="$router.back()">⬅ Back</button>
      </div>
    </header>

    <div class="table-wrap">
      <table class="user-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>Full Name</th>
          <th>Email</th>
          <th>Role</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="u in users" :key="u.id">
          <td>{{ u.id }}</td>
          <td>{{ u.full_name }}</td>
          <td>{{ u.email }}</td>
          <td>
              <span class="role" :class="u.role.toLowerCase()">
                {{ u.role }}
              </span>
          </td>
          <td>
            <button
                v-if="u.role === 'WORKER'"
                class="btn danger small"
                @click="deleteUser(u.id)"
            >
              🗑 Delete
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.table-wrap {
  overflow-x: auto;
  border-radius: 18px;
  box-shadow: 0 6px 22px rgba(0, 0, 0, 0.4);
}

.user-table {
  width: 100%;
  border-collapse: collapse;
  background: var(--panel);
  color: var(--text);
  border: 1px solid #342a57;
  border-radius: 18px;
}

.user-table th,
.user-table td {
  padding: 14px 18px;
  text-align: left;
}

.user-table th {
  background: #1a1430;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--muted);
}

.user-table tr:nth-child(even) {
  background: #1d1738;
}

.user-table tr:hover {
  background: #241d3e;
  transition: 0.2s ease;
}

.role {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.role.admin {
  background: linear-gradient(135deg, var(--violet-1), var(--violet-2));
}

.role.worker {
  background: linear-gradient(135deg, var(--danger-1), var(--danger-2));
}

/* Кнопки */
.btn {
  border: none;
  padding: 8px 14px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn.danger {
  background: linear-gradient(135deg, var(--danger-1), var(--danger-2));
  color: white;
  box-shadow: 0 6px 18px rgba(255, 70, 109, 0.3);
}

.btn.danger:hover {
  transform: translateY(-2px);
  filter: brightness(1.05);
}

.btn.small {
  padding: 6px 10px;
  font-size: 12px;
}
</style>
