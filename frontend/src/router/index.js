import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import LoginForm from '../components/LoginForm.vue'
import RegisterForm from '../components/RegisterForm.vue'
import AdminView from '../components/AdminView.vue'
import ProductCreated from '../components/ProductCreated.vue'
import OrderPage from '../components/OrderPage.vue'
const routes = [
  { path: '/',         redirect: '/shop' },
  { path: '/admin',    component: AdminView },
  { path: '/shop',     component: HomePage },
  { path: '/login',    component: LoginForm },
  { path: '/register', component: RegisterForm },
  { path: '/orders/:id', component: () => import('../components/OrderDetailView.vue'), props: true },
  { path: '/product/success', component: ProductCreated },
  { path: '/orders', component: OrderPage }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
