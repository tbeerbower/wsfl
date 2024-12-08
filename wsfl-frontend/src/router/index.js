import { createRouter, createWebHistory } from 'vue-router';
import store from '@/store';
import DraftView from '@/views/DraftView.vue';
import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue';
import DraftPickView from '@/views/DraftPickView.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
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
    component: RegisterView,
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
  },
  {
    path: '/draft/:draftId',
    name: 'draft',
    component: DraftView,
    meta: { requiresAuth: true }
  },
  {
    path: '/draft/:draftId/pick',
    name: 'draft-pick',
    component: DraftPickView,
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
