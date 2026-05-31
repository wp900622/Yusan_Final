<script setup>
import { provide, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { isLoggedIn, logout } from './api/auth'
import { useAuthStore } from './stores/auth'
import { useCartStore } from './stores/cart'

const toast = ref(null)
const cart = useCartStore()

function showToast(message, type = 'info', ms = 3000) {
  toast.value = { message, type }
  setTimeout(() => { toast.value = null }, ms)
}
provide('toast', showToast)

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()
const authenticated = computed(() => {
  route.fullPath
  return isLoggedIn()
})

function handleLogout() {
  logout()
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <header class="topbar">
    <div class="brand">
      <span class="title">YuShan Shopping Website<span class="accent">.</span></span>
      <span class="sub muted">Only Belong To You</span>
    </div>
    <nav v-if="authenticated">
      <router-link v-if="authStore.userRole === 'admin'" to="/admin">Admin</router-link>
      <router-link to="/shop">Shop</router-link>
      <router-link to="/orders">Order</router-link>
      <router-link to="/cart" class="cart-link">
        Cart<span v-if="cart.count" class="cart-badge">{{ cart.count }}</span>
      </router-link>
      <span class="user-tag muted">Hi, {{ authStore.username }}</span>
      <button class="link-button" type="button" @click="handleLogout">Logout</button>
    </nav>
    <nav v-else>
      <router-link to="/login">Login</router-link>
      <router-link to="/register">Signup</router-link>
      <router-link to="/cart" class="cart-link">
        Cart<span v-if="cart.count" class="cart-badge">{{ cart.count }}</span>
      </router-link>
    </nav>
  </header>

  <main class="shell">
    <router-view />
  </main>

  <Transition name="fade">
    <div v-if="toast" :class="['toast', toast.type]">{{ toast.message }}</div>
  </Transition>

  <footer class="footbar muted">
    <span> Yusan Shopping Website @Copyright 2026. All rights reserved.</span>
  </footer>
</template>

<style scoped>
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  padding: 1.5rem 2.5rem 1.25rem;
  border-bottom: 1px solid var(--line);
  background: var(--bg);
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  display: flex;
  align-items: baseline;
  gap: 0.7rem;
}
.mark {
  color: var(--accent);
  font-size: 1.1rem;
  letter-spacing: -2px;
}
.title {
  font-family: var(--font-serif);
  font-weight: 700;
  font-size: 1.4rem;
  letter-spacing: 0.06em;
}
.sub {
  font-style: italic;
  font-family: var(--font-serif);
  font-size: 0.85rem;
}
.user-tag {
  padding-left: 0.8rem;
  border-left: 1px solid var(--line);
  font-size: 0.85rem;
}
.cart-link { position: relative; }
.cart-badge {
  display: inline-block;
  margin-left: 0.35rem;
  min-width: 1.1rem;
  padding: 0 0.3rem;
  font-size: 0.7rem;
  line-height: 1.1rem;
  text-align: center;
  color: #fff;
  background: var(--accent);
  border-radius: 999px;
  letter-spacing: 0;
}

nav {
  display: flex;
  gap: 1.2rem;
  align-items: center;
}
nav a,
.link-button {
  color: var(--ink-soft);
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  padding-bottom: 0.25rem;
  border-bottom: 1px solid transparent;
  background: none;
  border: none;
  cursor: pointer;
}
nav a:hover,
nav a.router-link-active,
.link-button:hover {
  color: var(--ink);
  border-bottom-color: var(--accent);
  text-decoration: none;
}

.shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2.5rem 2.5rem 5rem;
}

.footbar {
  padding: 1.5rem 2.5rem;
  border-top: 1px solid var(--line);
  font-size: 0.8rem;
  text-align: center;
  background: var(--bg);
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.25s, transform 0.25s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-8px); }
</style>
