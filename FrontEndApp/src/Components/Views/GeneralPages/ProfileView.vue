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

const showNameModal = ref(false)
const showEmailModal = ref(false)
const showPasswordModal = ref(false)
const newName = ref('')
const newEmail = ref('')
const newPassword = ref('')

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
        avatarUrl.value = `http://localhost:8080/UsersAvatar/${user.value.fullName.replaceAll(" ","")}.png?${Date.now()}`
        console.log(user.value.fullName.replaceAll(" ",""))
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
      avatarUrl.value = `${API.replace('/api/user','')}/UsersAvatar/${user.value.fullName.replaceAll(" ","")}.png?${Date.now()}`
      alert('✅ Avatar updated!')
    } else alert('❌ Upload failed')
  } catch (e) {
    console.error('Upload error:', e)
  }
}

function openNameModal() {
  newName.value = user.value?.fullName || ''
  showNameModal.value = true
}

function openEmailModal() {
  newEmail.value = user.value?.email || ''
  showEmailModal.value = true
}

function openPasswordModal() {
  newPassword.value = ''
  showPasswordModal.value = true
}

async function changeName() {
  if (!newName.value || !user.value) return
  await fetch(`${API}/change-name?oldName=${encodeURIComponent(user.value.fullName)}&newName=${encodeURIComponent(newName.value)}`, { method: 'PUT' })
  showNameModal.value = false
  alert('✅ Name changed successfully!')
  location.reload()
}

async function changeEmail() {
  if (!newEmail.value || !user.value) return
  await fetch(`${API}/change-email?name=${encodeURIComponent(user.value.fullName)}&newEmail=${encodeURIComponent(newEmail.value)}`, { method: 'PUT' })
  showEmailModal.value = false
  alert('✅ Email changed successfully!')
  location.reload()
}

async function changePassword() {
  if (!newPassword.value || !user.value) return
  await fetch(`${API}/cahnge-password?name=${encodeURIComponent(user.value.fullName)}&newPassword=${encodeURIComponent(newPassword.value)}`, { method: 'PUT' })
  showPasswordModal.value = false
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
          <button class="btn small" @click="openEmailModal">✉️ Change Email</button>
        </div>
      </div>

      <div class="info-group">
        <p><strong>Full Name:</strong> {{ user.fullName }}</p>
        <div class="buttons-inline">
          <button class="btn small" @click="openNameModal">📝 Change Name</button>
        </div>
      </div>

      <div class="info-group">
        <p><strong>Password:</strong> ••••••••</p>
        <div class="buttons-inline">
          <button class="btn small danger" @click="openPasswordModal">🔒 Change Password</button>
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

    <!-- Modal for Name Change -->
    <div v-if="showNameModal" class="modal-overlay" @click="showNameModal = false">
      <div class="modal-content" @click.stop>
        <h3>📝 Change Full Name</h3>
        <input v-model="newName" type="text" placeholder="Enter new full name" class="modal-input" />
        <div class="modal-buttons">
          <button class="btn" @click="changeName">Save</button>
          <button class="btn back" @click="showNameModal = false">Cancel</button>
        </div>
      </div>
    </div>

    <!-- Modal for Email Change -->
    <div v-if="showEmailModal" class="modal-overlay" @click="showEmailModal = false">
      <div class="modal-content" @click.stop>
        <h3>✉️ Change Email</h3>
        <input v-model="newEmail" type="email" placeholder="Enter new email" class="modal-input" />
        <div class="modal-buttons">
          <button class="btn" @click="changeEmail">Save</button>
          <button class="btn back" @click="showEmailModal = false">Cancel</button>
        </div>
      </div>
    </div>

    <!-- Modal for Password Change -->
    <div v-if="showPasswordModal" class="modal-overlay" @click="showPasswordModal = false">
      <div class="modal-content" @click.stop>
        <h3>🔒 Change Password</h3>
        <input v-model="newPassword" type="password" placeholder="Enter new password" class="modal-input" />
        <div class="modal-buttons">
          <button class="btn" @click="changePassword">Save</button>
          <button class="btn back" @click="showPasswordModal = false">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  padding: 2rem;
  max-width: 720px;
  margin: 0 auto;
  color: #f4f0ff;
  min-height: 100vh;
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

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: linear-gradient(180deg, #1d1733, #161025);
  border: 1px solid #352a58;
  border-radius: 20px;
  padding: 2rem;
  max-width: 420px;
  width: 90%;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.5);
  text-align: center;
}

.modal-content h3 {
  font-size: 1.4rem;
  margin-bottom: 1.2rem;
  background: linear-gradient(to right, #bba1ff, #9f7fff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.modal-input {
  width: 100%;
  padding: 14px 18px;
  margin-bottom: 1.2rem;
  border: 1px solid #352a58;
  border-radius: 12px;
  background: #0d0817;
  color: #f4f0ff;
  font-size: 1rem;
  font-family: inherit;
  outline: none;
  transition: border 0.2s ease;
  box-sizing: border-box;
}

.modal-input:focus {
  border-color: #7a4fe0;
  box-shadow: 0 0 8px rgba(122, 79, 224, 0.4);
}

.modal-input::placeholder {
  color: #7a7a7a;
}

.modal-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.modal-buttons .btn {
  flex: 1;
  max-width: 150px;
}
</style>

<style>
body {
  background: linear-gradient(180deg, #0d0817, #1a0f2e);
  min-height: 100vh;
  margin: 0;
}

html {
  background: #0d0817;
}
</style>