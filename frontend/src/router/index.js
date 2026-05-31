import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import LoginForm from '../components/LoginForm.vue'
import RegisterForm from '../components/RegisterForm.vue'
import AdminView from '../components/AdminView.vue'
import ProductCreated from '../components/ProductCreated.vue'
import OrderPage from '../components/OrderPage.vue'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/',         redirect: '/shop' },
  { path: '/admin',    component: AdminView,      meta: { requiresAdmin: true } },
  { path: '/shop',     component: HomePage },
  { path: '/login',    component: LoginForm },
  { path: '/register', component: RegisterForm },
  { path: '/orders/:id', component: () => import('../components/OrderDetailView.vue'), props: true },
  { path: '/product/success', component: ProductCreated, meta: { requiresAdmin: true } },
  { path: '/orders', component: OrderPage },
  { path: '/cart', component: () => import('../components/CartView.vue') },
  { path: '/checkout', component: () => import('../components/CheckoutView.vue'), meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (!to.meta.requiresAdmin && !to.meta.requiresAuth) return true
  const auth = useAuthStore()
  if (!auth.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && auth.userRole !== 'admin') {
    return { path: '/shop' }
  }
  return true
})

export default router
