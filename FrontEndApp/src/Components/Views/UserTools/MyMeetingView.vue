<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import CryptoJS from 'crypto-js'

const SECRET_KEY = 'MASHONOCKA'
const API_USER = 'http://localhost:8080/api/user/get-id'
const API_MEETS = 'http://localhost:8080/api/meets'

const router = useRouter()
const route = useRoute()

function goBack() { router.back() }

function decryptName(encrypted: string): string {
  try {
    const bytes = CryptoJS.AES.decrypt(decodeURIComponent(encrypted), SECRET_KEY)
    return bytes.toString(CryptoJS.enc.Utf8)
  } catch (e) {
    console.error('❌ Failed to decrypt name:', e)
    return 'Unknown'
  }
}

const encryptedName = route.query.senderName as string
const userName = ref('')
const userId = ref<number | null>(null)

const loading = ref(false)
const error = ref('')
const meetings = ref<any[]>([])
const q = ref('')

const today = new Date()
const current = ref(new Date(today.getFullYear(), today.getMonth(), 1))

const monthLabel = computed(() =>
    current.value.toLocaleDateString(undefined, { month: 'long', year: 'numeric' })
)
function prevMonth() {
  current.value = new Date(current.value.getFullYear(), current.value.getMonth() - 1, 1)
}
function nextMonth() {
  current.value = new Date(current.value.getFullYear(), current.value.getMonth() + 1, 1)
}
function goToday() {
  current.value = new Date(today.getFullYear(), today.getMonth(), 1)
}

const weeks = computed(() => {
  const start = new Date(current.value.getFullYear(), current.value.getMonth(), 1)
  const end = new Date(current.value.getFullYear(), current.value.getMonth() + 1, 0)
  const shiftToMonday = (d: number) => (d === 0 ? 6 : d - 1)
  const days: { date: Date; inMonth: boolean }[] = []
  const leading = shiftToMonday(start.getDay())
  for (let i = 0; i < leading; i++) {
    const d = new Date(start)
    d.setDate(start.getDate() - (leading - i))
    days.push({ date: d, inMonth: false })
  }
  for (let d = 1; d <= end.getDate(); d++) {
    days.push({ date: new Date(current.value.getFullYear(), current.value.getMonth(), d), inMonth: true })
  }
  const trailing = 7 - (days.length % 7 || 7)
  for (let i = 1; i <= trailing; i++) {
    const d = new Date(end)
    d.setDate(end.getDate() + i)
    days.push({ date: d, inMonth: false })
  }
  const w = []
  for (let i = 0; i < days.length; i += 7) w.push(days.slice(i, i + 7))
  return w
})

const filteredMeetings = computed(() => {
  const term = q.value.trim().toLowerCase()
  if (!term) return meetings.value
  return meetings.value.filter(m =>
      (m.title || '').toLowerCase().includes(term) ||
      (m.customerName || '').toLowerCase().includes(term)
  )
})

function keyFromDate(d: Date) {
  const local = new Date(d)
  local.setHours(0, 0, 0, 0)
  const y = local.getFullYear()
  const m = String(local.getMonth() + 1).padStart(2, '0')
  const day = String(local.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function toDate(isoLike: string) {
  if (!isoLike) return new Date()
  return new Date(isoLike.replace(' ', 'T'))
}

const eventsByDay = computed(() => {
  const map = new Map<string, any[]>()
  for (const m of filteredMeetings.value) {
    const start = toDate(m.startAt)
    const k = keyFromDate(start)
    if (!map.has(k)) map.set(k, [])
    map.get(k)!.push(m)
  }
  for (const arr of map.values()) {
    arr.sort((a, b) => toDate(a.startAt).getTime() - toDate(b.startAt).getTime())
  }
  return map
})

const selected = ref<any | null>(null)
function openDetails(ev: any) { selected.value = ev }
function closeDetails() { selected.value = null }

function fmtTime(isoLike: string) {
  return toDate(isoLike).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })
}
function isTodayDate(d: Date) {
  return d.getFullYear() === today.getFullYear() &&
      d.getMonth() === today.getMonth() &&
      d.getDate() === today.getDate()
}

async function fetchUserIdByName(name: string): Promise<number | null> {
  try {
    const res = await fetch(`${API_USER}?fullName=${encodeURIComponent(name)}`)
    if (!res.ok) throw new Error('User not found')
    return await res.json()
  } catch (e) {
    error.value = 'Failed to load user ID: ' + String(e)
    return null
  }
}

async function fetchMeetingsByUserId(id: number) {
  loading.value = true
  error.value = ''
  try {
    const res = await fetch(`${API_MEETS}/${id}`)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    const data = await res.json()

    console.log('📦 Raw API response:', data)

    const unique = new Map<number, any>()
    for (const m of data || []) {
      unique.set(m.id, m)
    }

    meetings.value = Array.from(unique.values()).map(m => ({
      _id: m.id,
      title: m.title || 'Untitled Meeting',
      startAt: m.startAt,
      endAt: m.endAt,
      customerName: m.customerName || 'No client',
      createdBy: m.createdBy,
      createdAt: m.createdAt
    }))

    console.log('✅ Processed meetings:', meetings.value)
  } catch (e) {
    error.value = 'Failed to load meetings: ' + String(e)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  console.log('🔐 Encrypted from URL:', route.query.senderName)
  console.log('🔓 Decrypted:', decryptName(route.query.senderName))
  if (!encryptedName) return
  userName.value = decryptName(encryptedName)
  const id = await fetchUserIdByName(userName.value)
  if (id) {
    userId.value = id
    await fetchMeetingsByUserId(id)
  }
})

watch(filteredMeetings, async (newList) => {
  if (!newList.length) return
  const first = newList[0]
  if (!first?.startAt) return

  const startDate = new Date(first.startAt)
  current.value = new Date(startDate.getFullYear(), startDate.getMonth(), 1)
  await nextTick()
  const key = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}-${String(startDate.getDate()).padStart(2, '0')}`
  const dayCell = document.querySelector(`[data-date="${key}"]`)
  if (dayCell) {
    dayCell.scrollIntoView({ behavior: 'smooth', block: 'center' })
    dayCell.classList.add('highlight')
    setTimeout(() => dayCell.classList.remove('highlight'), 2000)
  }
})
</script>


<template>
  <div class="page">
    <header class="header">
      <div class="title-wrap">
        <h1 class="title">📅 My Meetings</h1>
        <p v-if="userName" class="subtitle">for <strong>{{ userName }}</strong></p>
      </div>
      <div class="header-actions">
        <button class="btn btn-back" @click="goBack">⬅ Back</button>
      </div>
    </header>

    <div class="toolbar">
      <div class="nav">
        <button class="btn" @click="prevMonth">⟵</button>
        <button class="btn" @click="goToday">Today</button>
        <button class="btn" @click="nextMonth">⟶</button>
        <div class="month-label">{{ monthLabel }}</div>
      </div>
      <input class="search" type="search" v-model="q" placeholder="Search meetings…" />
    </div>

    <div v-if="error" class="error">{{ error }}</div>

    <div v-else class="calendar">
      <div class="week-head">
        <div>Mon</div><div>Tue</div><div>Wed</div><div>Thu</div><div>Fri</div><div>Sat</div><div>Sun</div>
      </div>

      <div v-if="loading" class="skeleton">
        <div v-for="n in 42" :key="n" class="day skeleton-cell"></div>
      </div>

      <div v-else class="weeks">
        <div class="week" v-for="(week, wi) in weeks" :key="wi">
          <div
              class="day"
              :data-date="keyFromDate(d.date)"
              :class="[{ 'out': !d.inMonth, 'today': isTodayDate(d.date)}]"
              v-for="(d, di) in week"
              :key="di">
            <div class="day-head">
              <span class="num">{{ d.date.getDate() }}</span>
            </div>

            <div class="events">
              <div
                  v-for="ev in (eventsByDay.get(keyFromDate(d.date)) || [])"
                  :key="ev._id"
                  class="event"
                  @click="openDetails(ev)"
                  title="Click to open">
                <div class="event-time">{{ fmtTime(ev.startAt) }}–{{ fmtTime(ev.endAt) }}</div>
                <div class="event-title">{{ ev.title }}</div>
                <div class="event-sub">{{ ev.customerName }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="selected" class="modal-backdrop" @click.self="closeDetails">
      <div class="modal">
        <h3 class="modal-title">{{ selected.title }}</h3>
        <div class="row"><strong>Client:</strong> <span>{{ selected.customerName || '—' }}</span></div>
        <div class="row"><strong>Time:</strong> <span>{{ fmtTime(selected.startAt) }} – {{ fmtTime(selected.endAt) }}</span></div>
        <div class="row"><strong>Created by:</strong> <span>{{ selected.createdBy || '—' }}</span></div>
        <div class="row"><strong>Created at:</strong> <span>{{ new Date(selected.createdAt).toLocaleString() }}</span></div>
        <div class="modal-actions">
          <button class="btn" @click="closeDetails">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #150f22;
  color: #fff;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  margin: 0;
  color: #d5c1ff;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.month-label {
  font-weight: 600;
  margin-left: 8px;
  color: #bba8ff;
}

.search {
  min-width: 260px;
  padding: 8px 12px;
  border: 2px solid #b089ff;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.85);
  color: #222;
  transition: 0.3s;
}
.search:focus {
  background: #fff;
  border-color: #8b5cff;
  box-shadow: 0 0 8px #8b5cff;
}

.calendar { display: grid; gap: 8px; }
.week-head {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  font-weight: 600;
  opacity: 0.8;
  color: #c6b4ff;
}
.weeks { display: grid; grid-auto-rows: 1fr; gap: 8px; }
.week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}
.day {
  border: 1px solid rgba(150, 120, 255, 0.4);
  border-radius: 12px;
  min-height: 120px;
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  background: rgba(210, 190, 255, 0.15);
  backdrop-filter: blur(6px);
  color: #fff;
  transition: 0.2s ease;
}
.day:hover {
  transform: scale(1.03);
  box-shadow: 0 4px 14px rgba(160, 120, 255, 0.4);
}
.day.out { opacity: 0.3; background: rgba(100, 80, 150, 0.2); }
.day.today { outline: 2px solid #a58aff; background: rgba(180, 140, 255, 0.25); }

.day-head .num { font-weight: 700; color: #e0d0ff; }

.events { display: flex; flex-direction: column; gap: 6px; overflow: auto; }
.event {
  border: 1px solid #c7afff;
  border-radius: 10px;
  padding: 6px 8px;
  background: rgba(160, 100, 255, 0.25);
  color: #fff;
  cursor: pointer;
  transition: 0.2s ease;
}
.event:hover {
  background: rgba(170, 130, 255, 0.35);
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(150, 100, 255, 0.5);
}

.event-time { font-size: 12px; opacity: 0.8; }
.event-title { font-weight: 600; }
.event-sub { font-size: 12px; opacity: 0.8; }

.btn {
  padding: 8px 14px;
  border-radius: 10px;
  border: 1px solid #b69cff;
  background: linear-gradient(180deg, #d5c0ff, #b69cff);
  color: #1a102b;
  cursor: pointer;
  transition: all 0.25s ease;
  font-weight: 600;
}
.btn:hover {
  background: linear-gradient(180deg, #c9a9ff, #a98cff);
  transform: scale(1.05);
}

.btn-back {
  background: linear-gradient(180deg, #ff7b7b, #ff3d3d);
  border-color: #ff3d3d;
  color: #fff;
}
.btn-back:hover {
  background: linear-gradient(180deg, #ff5a5a, #e42525);
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(30, 15, 60, 0.7);
  display: grid;
  place-items: center;
  backdrop-filter: blur(8px);
}
.modal {
  width: min(520px, 92vw);
  background: #f6f3ff;
  color: #111;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 20px 60px rgba(120, 60, 200, 0.4);
}

.day.highlight {
  box-shadow: 0 0 15px 5px rgba(180, 120, 255, 0.9);
  transition: box-shadow 0.5s ease;
}

.modal-title { margin: 4px 0 12px; color: #4a2f85; }
.row { display: grid; grid-template-columns: 120px 1fr; gap: 8px; padding: 6px 0; }
.modal-actions { margin-top: 16px; display: flex; justify-content: flex-end; }
.error { color: #ff4d6d; font-weight: 600; }

</style>
