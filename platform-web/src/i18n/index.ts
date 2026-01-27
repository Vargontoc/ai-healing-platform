import { createI18n } from 'vue-i18n'
import en from './locales/en.json'
import es from './locales/es.json'
import fr from './locales/fr.json'
import pt from './locales/pt.json'

export type MessageSchema = typeof en

const i18n = createI18n<[MessageSchema], 'en' | 'es' | 'fr' | 'pt'>({
  legacy: false,
  locale: localStorage.getItem('locale') || 'en',
  fallbackLocale: 'en',
  messages: {
    en,
    es,
    fr,
    pt
  }
})

export default i18n
