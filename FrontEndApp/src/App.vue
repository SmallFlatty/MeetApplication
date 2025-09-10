<script setup lang="ts">
import MainMenu from './Components/MainMenu.vue'
const user = {
  id: 2,
  name: 'Ros Tik',
  role: 'ADMIN'
}
function handleLogout() {
  console.log('Logout clicked!')
}
</script>

<template>
  <MainMenu :user="user" @logout="handleLogout" />
</template>
