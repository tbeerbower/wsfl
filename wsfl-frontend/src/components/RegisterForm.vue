<template>
  <div class="register-container">
    <form @submit.prevent="handleSubmit" class="register-form">
      <h2>Create Account</h2>
      
      <div class="form-group">
        <label for="name">Name</label>
        <input
          type="text"
          id="name"
          v-model="name"
          required
          class="form-control"
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
          class="form-control"
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
          class="form-control"
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
          class="form-control"
          placeholder="Confirm your password"
          :class="{ 'error': passwordMismatch }"
        >
        <span v-if="passwordMismatch" class="error-text">
          Passwords do not match
        </span>
      </div>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <button 
        type="submit" 
        :disabled="loading || passwordMismatch || !isFormValid" 
        class="register-button"
      >
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
import { ref, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

export default {
  name: 'RegisterForm',
  setup() {
    const store = useStore();
    const router = useRouter();
    
    const name = ref('');
    const email = ref('');
    const password = ref('');
    const confirmPassword = ref('');
    const error = ref('');
    const loading = ref(false);

    const passwordMismatch = computed(() => {
      return password.value && confirmPassword.value && password.value !== confirmPassword.value;
    });

    const isFormValid = computed(() => {
      return name.value && 
             email.value && 
             password.value && 
             confirmPassword.value && 
             !passwordMismatch.value;
    });

    const handleSubmit = async () => {
      if (passwordMismatch.value) {
        return;
      }

      loading.value = true;
      error.value = '';

      try {
        const userData = {
          name: name.value,
          email: email.value,
          password: password.value
        };

        await store.dispatch('auth/register', userData);
        router.push('/dashboard');
      } catch (err) {
        error.value = err.response?.data?.message || 'Registration failed. Please try again.';
      } finally {
        loading.value = false;
      }
    };

    return {
      name,
      email,
      password,
      confirmPassword,
      error,
      loading,
      passwordMismatch,
      isFormValid,
      handleSubmit,
    };
  },
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: #333;
}

.form-control {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.form-control:focus {
  outline: none;
  border-color: #4CAF50;
}

.form-control.error {
  border-color: #dc3545;
}

.error-text {
  color: #dc3545;
  font-size: 14px;
}

.register-button {
  background-color: #4CAF50;
  color: white;
  padding: 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: background-color 0.2s;
}

.register-button:hover:not(:disabled) {
  background-color: #45a049;
}

.register-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  color: #dc3545;
  text-align: center;
  font-size: 14px;
}

.login-link {
  text-align: center;
  font-size: 14px;
}

.login-link a {
  color: #4CAF50;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
