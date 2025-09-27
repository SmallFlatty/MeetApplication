<template>
  <div class="header-actions">
    <button class="btn danger" @click="goHome">⬅ Back</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push({ name: 'home' }) // всегда возвращает на главную страницу
}
</script>

<style scoped>
.header-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 1rem;
}
</style>