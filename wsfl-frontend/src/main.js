import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'

// Configure axios defaults
axios.defaults.baseURL = 'http://localhost:9000'

// Add request interceptor to inject the auth token
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Add response interceptor to handle auth errors
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 403) {
      console.error('Received 403 response:', error.response.config.url)
      // If we get a 403, the token might be invalid
      if (store.getters['auth/isAuthenticated']) {
        console.log('Token appears invalid, logging out')
        store.dispatch('auth/logout')
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

// Initialize auth token if it exists
const token = localStorage.getItem('token')
if (token) {
  console.log('Initializing stored auth token')
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

const app = createApp(App)
app.use(router)
app.use(store)
app.mount('#app')
