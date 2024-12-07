import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'

// Set base URL for axios
axios.defaults.baseURL = 'http://localhost:9000'

// Set token if exists
const token = localStorage.getItem('token')
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

const app = createApp(App)
app.use(router)
app.use(store)
app.mount('#app')
