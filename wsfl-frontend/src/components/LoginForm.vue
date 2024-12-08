<template>
  <div class="login-container">
    <img src="@/assets/wsfl-logo.webp" alt="WSFL Logo" class="logo">
    <form @submit.prevent="handleSubmit" class="login-form">
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

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <button type="submit" :disabled="loading" class="login-button">
        {{ loading ? 'Logging in...' : 'Login' }}
      </button>
    </form>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import { useRouter } from 'vue-router';

export default {
  name: 'LoginForm',
  setup() {
    const store = useStore();
    const router = useRouter();
    const email = ref('');
    const password = ref('');
    const error = ref('');
    const loading = ref(false);

    const handleSubmit = async () => {
      loading.value = true;
      error.value = '';
      
      try {
        await store.dispatch('auth/login', {
          email: email.value,
          password: password.value,
        });
        router.push('/dashboard');
      } catch (err) {
        error.value = err.response?.data?.message || 'Login failed. Please try again.';
      } finally {
        loading.value = false;
      }
    };

    return {
      email,
      password,
      error,
      loading,
      handleSubmit,
    };
  },
};
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
  text-align: center;
}

.logo {
  max-width: 200px;
  margin-bottom: 30px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.login-button {
  background-color: #4CAF50;
  color: white;
  padding: 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.login-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-message {
  color: #dc3545;
  margin-bottom: 10px;
}
</style>
