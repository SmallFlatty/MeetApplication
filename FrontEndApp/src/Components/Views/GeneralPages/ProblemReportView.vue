<script setup lang="ts">
import { ref } from 'vue'
import {useRoute, useRouter} from 'vue-router'

const router = useRouter()

const API = 'http://localhost:8080/api/support'
const usersProblem = ref('')
const sent = ref(false)

async function sendProblem() {
  if (!usersProblem.value.trim()) {
    alert('Please describe your problem')
    return
  }

  try {
    const res = await fetch(API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ usersProblem: usersProblem.value })
    })

    if (res.ok) {
      sent.value = true
      usersProblem.value = ''

      setTimeout(() => {
        router.back()
      }, 1200)

    } else {
      const msg = await res.text()
      alert('Error: ' + msg)
    }
  } catch (e) {
    console.error('Error sending problem:', e)
    alert('Failed to send problem')
  }
}

</script>

<template>
  <div class="report-page">
    <h1 class="page-title">🛠 Report a Problem</h1>

    <div class="card">
      <div v-if="!sent">
  <textarea
      v-model="usersProblem"
      placeholder="Describe the issue here..."
  ></textarea>
        <div class="buttons">
          <button class="btn btn-send" @click="sendProblem">Send</button>
          <button class="btn btn-back" @click="router.back()">⬅️ Back</button>
        </div>
      </div>

      <div v-else class="thank-you">
        ✅ Thank you for your report! <br />
        We will check it soon.
      </div>

    </div>
  </div>
</template>

<style scoped>
.report-page {
  padding: 2rem;
  max-width: 600px;
  margin: auto;
}

.card {
  background: #1e1e2f;
  padding: 1.5rem;
  border-radius: 12px;
  color: #fff;
  text-align: center;
}

textarea {
  width: 100%;
  height: 120px;
  padding: 0.5rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  border: 1px solid #666;
  background: #2c2c3c;
  color: white;
  resize: vertical;
}

.btn {
  display: inline-block;
  min-width: 120px;
  padding: 0.5rem 1.2rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 1rem;
}

.btn-send {
  background-color: #6c5ce7;
  color: white;
}
.btn-send:hover {
  background-color: #a29bfe;
}

.btn-back {
  background-color: #c0392b;
  color: white;
}
.btn-back:hover {
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

.thank-you {
  font-size: 1.2rem;
  color: #2ecc71;
  font-weight: bold;
}
.buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1rem;
}
</style>
