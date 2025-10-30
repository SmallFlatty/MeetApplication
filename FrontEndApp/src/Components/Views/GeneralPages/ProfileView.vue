<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CryptoJS from 'crypto-js'

const router = useRouter()
const route = useRoute()

const API = 'http://localhost:8080/api/user'
const user = ref<any>(null)
const avatarUrl = ref('')
const selectedFile = ref<File | null>(null)

const SECRET_KEY = 'MASHONOCKA'

function decryptName(encrypted: string): string | null {
  try {
    const bytes = CryptoJS.AES.decrypt(decodeURIComponent(encrypted), SECRET_KEY)
    return bytes.toString(CryptoJS.enc.Utf8)
  } catch (e) {
    console.error('Decryption failed:', e)
    return null
  }
}

onMounted(() => {
  const encryptedName = route.query.u as string
  if (!encryptedName) return

  const fullName = decryptName(encryptedName)
  if (!fullName) return

  fetch(`${API}/all-info?fullName=${encodeURIComponent(fullName)}`, {
    credentials: 'include'
  })
      .then(res => res.json())
      .then(data => {
        user.value = data
        avatarUrl.value = `http://localhost:8080/UsersAvatar/${user.value.fullName}.png?${Date.now()}`
      })
      .catch(err => console.error('Failed to fetch user info:', err))
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
      body: formData,
      credentials: 'include'
    })
    if (res.ok) {
      avatarUrl.value = `${API.replace('/api/user','')}/UsersAvatar/${user.value.fullName}.png?${Date.now()}`
      alert('✅ Avatar updated!')
    } else alert('❌ Upload failed')
  } catch (e) {
    console.error('Upload error:', e)
  }
}

async function changeName() {
  const newName = prompt('Enter new full name:')
  if (!newName || !user.value) return
  await fetch(`${API}/change-name?oldName=${encodeURIComponent(user.value.fullName)}&newName=${encodeURIComponent(newName)}`, { method: 'PUT' })
  alert('✅ Name changed successfully!')
  location.reload()
}

async function changeEmail() {
  const newEmail = prompt('Enter new email:')
  if (!newEmail || !user.value) return
  await fetch(`${API}/change-email?name=${encodeURIComponent(user.value.fullName)}&newEmail=${encodeURIComponent(newEmail)}`, { method: 'PUT' })
  alert('✅ Email changed successfully!')
  location.reload()
}

async function changePassword() {
  const newPassword = prompt('Enter new password:')
  if (!newPassword || !user.value) return
  await fetch(`${API}/cahnge-password?name=${encodeURIComponent(user.value.fullName)}&newPassword=${encodeURIComponent(newPassword)}`, { method: 'PUT' })
  alert('✅ Password changed successfully!')
}
</script>

<template>
  <div class="profile-page">
    <h1 class="page-title">👤 Profile</h1>

    <div v-if="user" class="profile-card">
      <img :src="avatarUrl" class="avatar" alt="Avatar" />
      <h2 class="user-name">{{ user.fullName }}</h2>
      <p class="user-role">{{ user.role }}</p>

      <hr />

      <div class="info-group">
        <p><strong>Email:</strong> {{ user.email }}</p>
        <div class="buttons-inline">
          <button class="btn small" @click="changeEmail">✉️ Change Email</button>
        </div>
      </div>

      <div class="info-group">
        <p><strong>Full Name:</strong> {{ user.fullName }}</p>
        <div class="buttons-inline">
          <button class="btn small" @click="changeName">📝 Change Name</button>
        </div>
      </div>

      <div class="info-group">
        <p><strong>Password:</strong> ••••••••</p>
        <div class="buttons-inline">
          <button class="btn small danger" @click="changePassword">🔒 Change Password</button>
        </div>
      </div>

      <hr />

      <h3 class="section-title">Update Avatar</h3>

      <div class="avatar-buttons">
        <label class="custom-file-upload">
          <input type="file" accept="image/*" @change="onFileChange" />
          📁 Choose Image
        </label>

        <button class="btn" @click="uploadAvatar">Upload</button>
        <button class="btn back" @click="router.back()">⬅️ Back</button>
      </div>
    </div>

    <p v-else>Loading user info...</p>
  </div>
</template>

<style scoped>
.profile-page {
  padding: 2rem;
  max-width: 720px;
  margin: 0 auto;
  color: #f4f0ff;
}

.profile-card {
  background: linear-gradient(180deg, #1d1733, #161025);
  border-radius: 20px;
  border: 1px solid #352a58;
  padding: 2rem;
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.35);
  text-align: center;
}

.avatar {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 1rem;
  border: 3px solid #7a4fe0;
  box-shadow: 0 0 16px rgba(160, 100, 255, 0.4);
}

.page-title {
  font-size: 2.6rem;
  font-weight: 800;
  text-align: center;
  margin-bottom: 1.5rem;
  background: linear-gradient(to right, #bba1ff, #9f7fff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.user-name {
  font-size: 1.6rem;
  margin-bottom: 0.2rem;
}

.user-role {
  font-size: 0.9rem;
  font-weight: 700;
  color: #bba1ff;
  margin-bottom: 1rem;
}

hr {
  border: none;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 1rem 0;
}

.info-group {
  text-align: left;
  margin-top: 1rem;
}

.buttons-inline {
  margin-top: 0.4rem;
}

.btn,
.custom-file-upload {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;

  height: 46px;
  min-width: 130px;

  border: none;
  color: white;
  font-weight: 700;
  font-size: 0.95rem;
  cursor: pointer;

  border-radius: 12px;
  background: linear-gradient(135deg, #bba1ff, #9f7fff);
  box-shadow: 0 4px 16px rgba(160, 100, 255, 0.3);
  transition: all 0.2s ease;

  text-align: center;
  padding: 0 18px;
}

.btn:hover,
.custom-file-upload:hover {
  transform: translateY(-2px);
  filter: brightness(1.05);
}

.btn:active,
.custom-file-upload:active {
  transform: translateY(0);
}

.custom-file-upload input[type="file"] {
  display: none;
}

.btn.back {
  background: linear-gradient(135deg, #ff738f, #ff466d);
  box-shadow: 0 6px 18px rgba(255, 70, 109, 0.3);
}

.avatar-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 1rem;
}

</style>
