<template>
  <div class="register-view">
    <div class="auth-container">
      <div class="auth-header">
        <h1>Create Account</h1>
        <p>Join Winter Series Fantasy League today</p>
      </div>
      <RegisterForm />
      <div class="auth-footer">
        <p>Already have an account? <router-link to="/login">Sign in</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import RegisterForm from '@/components/RegisterForm.vue';
</script>

<style scoped>
.register-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-primary);
  padding: var(--spacing-lg);
}

.auth-container {
  width: 100%;
  max-width: 400px;
  background-color: var(--bg-secondary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
}

.auth-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
}

.auth-header h1 {
  color: var(--text-primary);
  font-size: 1.875rem;
  font-weight: 700;
  margin-bottom: var(--spacing-xs);
}

.auth-header p {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.auth-footer {
  margin-top: var(--spacing-xl);
  text-align: center;
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.auth-footer a {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
}

.auth-footer a:hover {
  text-decoration: underline;
}
</style>
