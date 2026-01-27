import { ref } from 'vue'

export type Theme = 'light' | 'dark'

const theme = ref<Theme>('light')
let initialized = false

export function useTheme() {
  const setTheme = (newTheme: Theme) => {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)

    if (newTheme === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  const toggleTheme = () => {
    setTheme(theme.value === 'light' ? 'dark' : 'light')
  }

  const initTheme = () => {
    if (initialized) return

    const savedTheme = localStorage.getItem('theme') as Theme | null
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

    const initialTheme = savedTheme || (prefersDark ? 'dark' : 'light')
    setTheme(initialTheme)
    initialized = true
  }

  // Auto-initialize on first use
  if (!initialized) {
    initTheme()
  }

  return {
    theme,
    setTheme,
    toggleTheme,
    initTheme
  }
}
