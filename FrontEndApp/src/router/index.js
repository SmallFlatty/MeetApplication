import { createRouter, createWebHistory } from 'vue-router'

// Маршрути (поки що прості заглушки)
const routes = [
    { path: '/', name: 'home', component: () => import('../Components/MainMenu.vue') },
    { path: '/profile', name: 'profile', component: { template: '<div>Profile Page</div>' } },
    { path: '/meetings/mine', name: 'meetings.mine', component: { template: '<div>My Meetings</div>' } },
    { path: '/meetings/create', name: 'meetings.create', component: { template: '<div>Create Meeting</div>' } },
    { path: '/meetings/all', name: 'meetings.all', component: { template: '<div>All Meetings</div>' } },
    { path: '/users', name: 'users.index', component: { template: '<div>Users Management</div>' } }
]

export const router = createRouter({
    history: createWebHistory(),
    routes
})
