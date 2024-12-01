<template>
  <div class="register-view">
    <h1>Register for WSFL</h1>
    <form @submit.prevent="handleSubmit" class="register-form">
      <div class="form-group">
        <label for="name">Name</label>
        <input
          type="text"
          id="name"
          v-model="name"
          required
          placeholder="Enter your name"
        >
      </div>

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
        <label for="picture">Profile Picture URL</label>
        <input
          type="url"
          id="picture"
          v-model="picture"
          placeholder="Enter picture URL (optional)"
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

      <div class="form-group">
        <label for="confirmPassword">Confirm Password</label>
        <input
          type="password"
          id="confirmPassword"
          v-model="confirmPassword"
          required
          placeholder="Confirm your password"
        >
      </div>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <button type="submit" :disabled="loading || !isValid">
        {{ loading ? 'Registering...' : 'Register' }}
      </button>

      <div class="login-link">
        Already have an account? 
        <router-link to="/login">Login here</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'RegisterView',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const name = ref('')
    const email = ref('')
    const picture = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const error = ref('')
    const loading = ref(false)

    const isValid = computed(() => {
      return password.value === confirmPassword.value &&
             password.value.length >= 6 &&
             name.value &&
             email.value
    })

    const handleSubmit = async () => {
      if (!isValid.value) {
        error.value = 'Please check your input and try again.'
        return
      }

      try {
        loading.value = true
        error.value = ''
        
        await store.dispatch('auth/register', {
          name: name.value,
          email: email.value,
          picture: picture.value || null,
          password: password.value
        })
        
        router.push('/login')
      } catch (err) {
        error.value = err.response?.data?.message || 'Registration failed. Please try again.'
      } finally {
        loading.value = false
      }
    }

    return {
      name,
      email,
      picture,
      password,
      confirmPassword,
      error,
      loading,
      isValid,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.register-view {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
}

.register-form {
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

.login-link {
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
