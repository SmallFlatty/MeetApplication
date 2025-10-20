
import { createApp } from 'vue'
import {createPinia} from "pinia";
import App from './App.vue'
import { router } from './router'
import piniaPersist from 'pinia-plugin-persistedstate'

const app = createApp(App)
app.use(createPinia)
pinia.use(piniaPersist)
app.use(router)
app.mount('#app')
