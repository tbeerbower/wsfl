<template>
  <div class="login-view">
    <h1>Login to WSFL</h1>
    <form @submit.prevent="handleSubmit" class="login-form">
      <div class="form-group">
        <label for="email">Email</label>
        <input
          type="email"
          id="email"
          v-model="email"
          required
          placeholder="Enter your email"
        >
      </div>
      
      <div class="form-group">
        <label for="password">Password</label>
        <input
          type="password"
          id="password"
          v-model="password"
          required
          placeholder="Enter your password"
        >
      </div>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <button type="submit" :disabled="loading">
        {{ loading ? 'Logging in...' : 'Login' }}
      </button>

      <div class="register-link">
        Don't have an account? 
        <router-link to="/register">Register here</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'LoginView',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const email = ref('')
    const password = ref('')
    const error = ref('')
    const loading = ref(false)

    const handleSubmit = async () => {
      try {
        loading.value = true
        error.value = ''
        
        await store.dispatch('auth/login', {
          email: email.value,
          password: password.value
        })
        
        router.push('/dashboard')
      } catch (err) {
        error.value = err.response?.data?.message || 'Login failed. Please try again.'
      } finally {
        loading.value = false
      }
    }

    return {
      email,
      password,
      error,
      loading,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.login-view {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  font-weight: bold;
}

input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

button {
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.error-message {
  color: #f44336;
  text-align: center;
}

.register-link {
  text-align: center;
}

a {
  color: #2196F3;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}
</style>
