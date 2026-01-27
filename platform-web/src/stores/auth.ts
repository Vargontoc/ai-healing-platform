import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

interface User {
  id: string
  email: string
  name: string
}

interface AuthTokens {
  accessToken: string
  refreshToken: string
  expiresIn: number
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const accessToken = ref<string | null>(localStorage.getItem('accessToken'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const tokenExpiry = ref<number | null>(
    localStorage.getItem('tokenExpiry') ? parseInt(localStorage.getItem('tokenExpiry')!) : null
  )

  const isAuthenticated = computed(() => {
    if (!accessToken.value || !tokenExpiry.value) return false
    return Date.now() < tokenExpiry.value
  })

  function setTokens(tokens: AuthTokens) {
    accessToken.value = tokens.accessToken
    refreshToken.value = tokens.refreshToken
    tokenExpiry.value = Date.now() + tokens.expiresIn * 1000

    localStorage.setItem('accessToken', tokens.accessToken)
    localStorage.setItem('refreshToken', tokens.refreshToken)
    localStorage.setItem('tokenExpiry', tokenExpiry.value.toString())
  }

  function setUser(userData: User) {
    user.value = userData
  }

  function clearAuth() {
    user.value = null
    accessToken.value = null
    refreshToken.value = null
    tokenExpiry.value = null

    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('tokenExpiry')
  }

  async function login(email: string, _password: string): Promise<void> {
    // TODO: Implement actual API call to gateway
    // For now, simulate a successful login
    const mockTokens: AuthTokens = {
      accessToken: 'mock-jwt-token',
      refreshToken: 'mock-refresh-token',
      expiresIn: 3600
    }

    setTokens(mockTokens)
    setUser({
      id: '1',
      email,
      name: email.split('@')[0]
    })
  }

  async function logout(): Promise<void> {
    // TODO: Implement actual API call to invalidate tokens
    clearAuth()
  }

  async function refreshAccessToken(): Promise<boolean> {
    if (!refreshToken.value) return false

    try {
      // TODO: Implement actual API call to refresh token
      // For now, simulate successful refresh
      const mockTokens: AuthTokens = {
        accessToken: 'new-mock-jwt-token',
        refreshToken: refreshToken.value,
        expiresIn: 3600
      }

      setTokens(mockTokens)
      return true
    } catch (error) {
      clearAuth()
      return false
    }
  }

  return {
    user,
    accessToken,
    refreshToken,
    isAuthenticated,
    login,
    logout,
    refreshAccessToken,
    setTokens,
    setUser,
    clearAuth
  }
})
