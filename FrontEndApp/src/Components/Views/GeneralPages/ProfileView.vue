<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter() // 🟢 Додали це!

const API = 'http://localhost:8080/api/user'
const user = ref(null)
const avatarUrl = ref('')
const selectedFile = ref<File | null>(null)

onMounted(() => {
  fetch(`${API}/all-info`, {
    credentials: 'include'
  })
      .then(res => res.json())
      .then(data => {
        user.value = data
        avatarUrl.value = `http://localhost:8080/UsersAvatar/${user.value.fullName}.png?${Date.now()}`
      })
      .catch(err => {
        console.error('Failed to fetch user info:', err)
      })
})

function onFileChange(event: Event) {
  const files = (event.target as HTMLInputElement).files
  if (files && files.length > 0) {
    selectedFile.value = files[0]
  }
}

async function uploadAvatar() {
  if (!selectedFile.value || !user.value) return

  const formData = new FormData()
  formData.append('file', selectedFile.value)
  formData.append('fullName', user.value.fullName)

  try {
    const res = await fetch(`${API}/upload-avatar`, {
      method: 'POST',
      credentials: 'include',
      body: formData
    })

    if (res.ok) {
      alert('Avatar uploaded successfully!')
      avatarUrl.value = `/UsersAvatar/${user.value.fullName}.png?${Date.now()}`
    } else {
      alert('Upload failed')
    }
  } catch (e) {
    console.error('Upload error:', e)
    alert('Upload error')
  }
}
</script>


<template>
  <div class="profile-page">
    <h1 class="page-title">👤 Profile</h1>

    <div v-if="user" class="card">
      <img :src="avatarUrl" class="avatar" alt="Avatar" />
      <p><strong>Name:</strong> {{ user.fullName }}</p>
      <p><strong>Email:</strong> {{ user.email }}</p>
      <p><strong>Role:</strong> {{ user.role }}</p>
      <hr />

      <h3>Update Avatar</h3>

      <!-- Схований input та стилізована кнопка -->
      <label class="custom-file-upload">
        <input type="file" accept="image/*" @change="onFileChange" />
        📁 Choose Image
      </label>

      <button @click="uploadAvatar" class="btn">Upload</button>
      <button class="back" @click="router.back()">⬅️ Back</button>
    </div>

    <p v-else>Loading user info...</p>
  </div>
</template>


<style scoped>
.profile-page {
  padding: 2rem;
  max-width: 600px;
  margin: auto;
}

.avatar {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  margin-bottom: 1rem;
  border: 2px solid #999;
}

.card {
  background: #1e1e2f;
  padding: 1.5rem;
  border-radius: 12px;
  color: #fff;
}
.btn {
  margin-top: 1rem;
}

.back {
  margin-top: 1rem;
  background-color: #c0392b;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
}

.back:hover {
  background-color: #e74c3c;
}
.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 1rem;
  background: linear-gradient(to right, #9b59b6, #8e44ad);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* Кастомна кнопка для вибору файлу */
.custom-file-upload {
  display: inline-block;
  padding: 0.5rem 1rem;
  margin-top: 1rem;
  cursor: pointer;
  background-color: #6c5ce7;
  color: white;
  border-radius: 8px;
  transition: background-color 0.2s ease-in-out;
  text-align: center;
}

.custom-file-upload:hover {
  background-color: #a29bfe;
}

.custom-file-upload input[type="file"] {
  display: none;
}

</style>
