<script setup>
import {ref, computed, onMounted} from 'vue'
import { useRouter } from 'vue-router'
// import HomeButton from '@/Common/HomeButton.vue'

/** —— НАЛАШТУВАННЯ API —— */
const API_BASE = 'http://localhost:8080/api/meets'   // ← змінюй під свій бекенд (napр. http://localhost:8080/api/meets)
const USER_ID = 1               // ← тимчасово хардкодимо, потім підставиш реальний

/** —— РОУТЕР / ХЕДЕР —— */
const router = useRouter()
function goHome() { router.push({ name: 'home' }) }

/** — —— СТАН — — — —— */
const loading = ref(false)
const error = ref('')
const meetings = ref([])
/* простий пошук */
const q = ref('')

/** —— ДАТИ / КАЛЕНДАР —— */
const today = new Date()
const current = ref(new Date(today.getFullYear(), today.getMonth(), 1)) // перший день поточного місяця

const monthLabel = computed(() =>
    current.value.toLocaleDateString(undefined, { month: 'long', year: 'numeric' })
)

/* Перехід між місяцями */
function prevMonth() {
  current.value = new Date(current.value.getFullYear(), current.value.getMonth() - 1, 1)
}
function nextMonth() {
  current.value = new Date(current.value.getFullYear(), current.value.getMonth() + 1, 1)
}
function goToday() {
  current.value = new Date(today.getFullYear(), today.getMonth(), 1)
}

/* Будуємо матрицю днів (тиждень починається з понеділка) */
const weeks = computed(() => {
  const start = new Date(current.value.getFullYear(), current.value.getMonth(), 1)
  const end = new Date(current.value.getFullYear(), current.value.getMonth() + 1, 0) // останній день місяця

  // день тижня: 0=нд ... 6=сб → потрібно змістити, щоб 1=пн ... 0=нд
  const shiftToMonday = d => (d === 0 ? 6 : d - 1)

  const days = []
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

  // розбиваємо по тижнях
  const w = []
  for (let i = 0; i < days.length; i += 7) {
    w.push(days.slice(i, i + 7))
  }
  return w
})

/** —— ФІЛЬТРАЦІЯ ПОДІЙ —— */
const filteredMeetings = computed(() => {
  const term = q.value.trim().toLowerCase()
  if (!term) return meetings.value
  return meetings.value.filter(m =>
      (m.title || '').toLowerCase().includes(term) ||
      (m.customerName || '').toLowerCase().includes(term)
  )
})

/** —— ГРУПУВАННЯ ПОДІЙ ПО ДНЯХ —— */
function keyFromDate(d) {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}
const eventsByDay = computed(() => {
  const map = new Map()
  for (const m of filteredMeetings.value) {
    // Прив’язуємо подію до дати startAt (спрощено). За потреби можна розкидати на діапазон днів.
    const start = toDate(m.startAt)
    const k = keyFromDate(start)
    if (!map.has(k)) map.set(k, [])
    map.get(k).push(m)
  }
  // Сортуємо події всередині дня за часом початку
  for (const arr of map.values()) {
    arr.sort((a, b) => toDate(a.startAt).getTime() - toDate(b.startAt).getTime())
  }
  return map
})

/** —— ВИТЯГ З БЕКЕНДУ —— */
async function fetchMeetings() {
  loading.value = true
  error.value = ''
  try {
    const resp = await fetch(`${API_BASE}/${USER_ID}`)
    if (!resp.ok) throw new Error(`HTTP ${resp.status}`)
    const data = await resp.json()
    // Очікуємо поля: title, startAt, endAt, customerName, createdBy, createdAt
    meetings.value = (data || []).map((m, i) => ({
      // якщо id немає — тимчасово штучний
      _id: m.id ?? i,
      title: m.title ?? '',
      startAt: m.startAt,
      endAt: m.endAt,
      customerName: m.customerName ?? '',
      createdBy: m.createdBy ?? '',
      createdAt: m.createdAt
    }))
  } catch (e) {
    error.value = 'Не вдалося завантажити зустрічі. ' + String(e?.message ?? e)
  } finally {
    loading.value = false
  }
}

/** —— ДЕТАЛІ ПОДІЇ (МОДАЛКА) —— */
const selected = ref(null)
function openDetails(eventObj) { selected.value = eventObj }
function closeDetails() { selected.value = null }

/** —— УТИЛІТИ ФОРМАТУВАННЯ —— */
function toDate(isoLike) {
  // Spring зазвичай повертає LocalDateTime без зони (напр. "2025-10-20T14:30:00")
  // new Date('YYYY-MM-DDTHH:mm:ss') інтерпретує як локальний час — це якраз зручно для UI.
  return new Date(isoLike)
}
function fmtTime(isoLike) {
  return toDate(isoLike).toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' })
}
function isTodayDate(d) {
  return d.getFullYear() === today.getFullYear()
      && d.getMonth() === today.getMonth()
      && d.getDate() === today.getDate()
}

onMounted(fetchMeetings)
</script>

<template>
  <div class="page">
    <header class="header">
      <div class="title-wrap">
        <h1 class="title">📅 My Meetings</h1>
      </div>
      <div class="header-actions">
        <HomeButton />
      </div>
    </header>

    <div class="toolbar">
      <div class="nav">
        <button class="btn" @click="prevMonth">⟵</button>
        <button class="btn" @click="goToday">Сьогодні</button>
        <button class="btn" @click="nextMonth">⟶</button>
        <div class="month-label">{{ monthLabel }}</div>
      </div>
      <input
          class="search"
          type="search"
          v-model="q"
          placeholder="Пошук: назва або клієнт…" />
    </div>

    <div v-if="error" class="error">{{ error }}</div>
    <div v-else class="calendar">
      <div class="week-head">
        <div>Пн</div><div>Вт</div><div>Ср</div><div>Чт</div><div>Пт</div><div>Сб</div><div>Нд</div>
      </div>

      <div v-if="loading" class="skeleton">
        <div v-for="n in 42" :key="n" class="day skeleton-cell"></div>
      </div>

      <div v-else class="weeks">
        <div class="week" v-for="(week, wi) in weeks" :key="wi">
          <div
              class="day"
              :class="[{ 'out': !d.inMonth, 'today': isTodayDate(d.date)}]"
              v-for="(d, di) in week"
              :key="di"
          >
            <div class="day-head">
              <span class="num">{{ d.date.getDate() }}</span>
            </div>

            <div class="events">
              <div
                  v-for="ev in (eventsByDay.get(d.date.toISOString().slice(0,10)) || [])"
                  :key="ev._id"
                  class="event"
                  @click="openDetails(ev)"
                  title="Натисни, щоб відкрити"
              >
                <div class="event-time">{{ fmtTime(ev.startAt) }}–{{ fmtTime(ev.endAt) }}</div>
                <div class="event-title">{{ ev.title }}</div>
                <div class="event-sub">{{ ev.customerName }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Модалка деталей -->
    <div v-if="selected" class="modal-backdrop" @click.self="closeDetails">
      <div class="modal">
        <h3 class="modal-title">{{ selected.title }}</h3>
        <div class="row"><strong>Клієнт:</strong> <span>{{ selected.customerName || '—' }}</span></div>
        <div class="row"><strong>Час:</strong> <span>{{ fmtTime(selected.startAt) }} – {{ fmtTime(selected.endAt) }}</span></div>
        <div class="row"><strong>Створив:</strong> <span>{{ selected.createdBy || '—' }}</span></div>
        <div class="row"><strong>Створено:</strong> <span>{{ new Date(selected.createdAt).toLocaleString() }}</span></div>
        <div class="modal-actions">
          <button class="btn" @click="closeDetails">Закрити</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { padding: 20px; display: flex; flex-direction: column; gap: 16px; }
.header { display: flex; align-items: center; justify-content: space-between; }
.title { margin: 0; }
.toolbar { display: flex; align-items: center; justify-content: space-between; gap: 12px; flex-wrap: wrap; }
.nav { display: flex; align-items: center; gap: 8px; }
.month-label { font-weight: 600; margin-left: 8px; }

.search {
  min-width: 260px;
  padding: 8px 12px;
  border: 1px solid var(--c-border, #ddd);
  border-radius: 10px;
  outline: none;
}

.calendar { display: grid; gap: 8px; }
.week-head {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  font-weight: 600;
  opacity: 0.8;
}
.weeks { display: grid; grid-auto-rows: 1fr; gap: 8px; }
.week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}
.day {
  border: 1px solid var(--c-border, #e5e7eb);
  border-radius: 12px;
  min-height: 120px;
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  background: var(--c-bg, #fff);
}
.day.out { opacity: 0.45; }
.day.today { outline: 2px solid #3b82f6; outline-offset: 0; }
.day-head { display: flex; justify-content: space-between; align-items: center; }
.day-head .num { font-weight: 700; }

.events { display: flex; flex-direction: column; gap: 6px; overflow: auto; }
.event {
  border: 1px solid var(--c-border, #e5e7eb);
  border-radius: 10px;
  padding: 6px 8px;
  cursor: pointer;
  transition: transform .1s ease, box-shadow .2s ease;
}
.event:hover { transform: translateY(-1px); box-shadow: 0 4px 18px rgba(0,0,0,.06); }
.event-time { font-size: 12px; opacity: 0.8; }
.event-title { font-weight: 600; line-height: 1.25; }
.event-sub { font-size: 12px; opacity: 0.8; }

/* Кнопки */
.btn {
  padding: 8px 12px;
  border-radius: 10px;
  border: 1px solid var(--c-border, #d1d5db);
  background: var(--c-bg, #fff);
  cursor: pointer;
}
.btn:hover { background: #f3f4f6; }

/* Скелетон */
.skeleton {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}
.skeleton-cell {
  min-height: 120px;
  background: linear-gradient(90deg, #f0f2f5 25%, #eceff3 37%, #f0f2f5 63%);
  background-size: 400% 100%;
  animation: shimmer 1.4s ease infinite;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}
@keyframes shimmer {
  0% { background-position: 100% 0; }
  100% { background-position: 0 0; }
}

/* Модалка */
.modal-backdrop {
  position: fixed; inset: 0; background: rgba(0,0,0,.35);
  display: grid; place-items: center;
}
.modal {
  width: min(520px, 92vw);
  background: #fff;
  color: #111;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,.25);
}
.modal-title { margin: 4px 0 12px; }
.row { display: grid; grid-template-columns: 110px 1fr; gap: 8px; padding: 6px 0; }
.modal-actions { margin-top: 16px; display: flex; justify-content: flex-end; }
.error { color: #b91c1c; font-weight: 600; }
</style>
