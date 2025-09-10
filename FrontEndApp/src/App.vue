<script setup lang="ts">
import MainMenuView from './Components/Views/MainPage/MainMenuView.vue'
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
  <MainMenuView :user="user" @logout="handleLogout" />
</template>
