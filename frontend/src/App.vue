<script setup>
import { provide, ref } from 'vue'

const toast = ref(null)

function showToast(message, type = 'info', ms = 3000) {
  toast.value = { message, type }
  setTimeout(() => { toast.value = null }, ms)
}
provide('toast', showToast)
</script>

<template>
  <header class="topbar">
    <div class="brand">
      <span class="title">YuShan Shopping Website<span class="accent">.</span></span>
      <span class="sub muted">Only Belong To You</span>
    </div>
    <nav>
      <router-link to="/admin">Admin</router-link>
      <router-link to="/shop">Shop</router-link>
      <router-link to="/orders">Order</router-link>
    </nav>
  </header>

  <main class="shell">
    <router-view />
  </main>

  <Transition name="fade">
    <div v-if="toast" :class="['toast', toast.type]">{{ toast.message }}</div>
  </Transition>

  <footer class="footbar muted">
    <span>Three-tier · Vue 3 + Spring Boot + MySQL · Stored Procedures only</span>
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
