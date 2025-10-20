import { defineStore } from 'pinia'

interface UserState {
    fullName: string
}

export const useUserStore = defineStore<'user', UserState, {}, {
    setUser(user: Partial<UserState>): void
    clearUser(): void
}>('user', {
    state: (): UserState => ({
        fullName: '',
    }),

    actions: {
        setUser(user: Partial<UserState>) {
            this.fullName = user.fullName ?? ''
        },
        clearUser() {
            this.fullName = ''
        }
    },

    persist: true as any
})
