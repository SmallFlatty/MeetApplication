<script setup lang="ts">
import { useRouter } from "vue-router";
import HomeButton from "@/Common/HomeButton.vue";

const router = useRouter();

function goHome() {
  router.push({ name: 'home' }) // Явно ведёт на MainMenuView
}
</script>

<template>
  <div class="page">
    <header class="header">
      <div class="title-wrap">
        <h1 class="title">➕ Create Meeting</h1>
      </div>
      <div class="header-actions">
        <HomeButton />
      </div>
    </header>
  </div>
</template>

<style scoped>

</style>