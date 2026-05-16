import { defineStore } from 'pinia'
import { getCurrentUser } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => {
    const session = getCurrentUser()
    return {
      isLoggedIn: Boolean(session),
      userRole: (session?.role || 'user').toLowerCase(),
      username: session?.username || '',
    }
  },
  actions: {
    login({ username = '', role = 'user' } = {}) {
      this.isLoggedIn = true
      this.userRole = role
      this.username = username
    },
    logout() {
      this.isLoggedIn = false
      this.userRole = 'user'
      this.username = ''
    },
  },
})
