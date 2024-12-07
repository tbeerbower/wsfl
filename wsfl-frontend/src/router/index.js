import { createRouter, createWebHistory } from 'vue-router';
import LoginForm from '@/components/LoginForm.vue';
import RegisterForm from '@/components/RegisterForm.vue';
import store from '@/store';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginForm,
    meta: { requiresAuth: false },
    beforeEnter: (to, from, next) => {
      if (store.getters['auth/isAuthenticated']) {
        next('/dashboard');
      } else {
        next();
      }
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterForm,
    meta: { requiresAuth: false },
    beforeEnter: (to, from, next) => {
      if (store.getters['auth/isAuthenticated']) {
        next('/dashboard');
      } else {
        next();
      }
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/DashboardView.vue'),
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const isAuthenticated = store.getters['auth/isAuthenticated'];

  if (requiresAuth && !isAuthenticated) {
    next('/login');
  } else {
    next();
  }
});

export default router;
