<script setup>
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const productId = route.query.id || ''
const productName = route.query.name || ''

function goToAdmin() {
  router.push('/admin')
}
function goToShop() {
  router.push('/shop')
}
</script>

<template>
  <section class="hero">
    <p class="eyebrow muted">新增成功</p>
    <h1>商品已建立</h1>
    <p class="lede muted">已成功新增商品 <strong>{{ productName }}</strong> ({{ productId }})。</p>
    <div class="actions">
      <button class="cta" @click="goToAdmin">回到管理</button>
      <button class="ghost" @click="goToShop">回到商店</button>
    </div>
  </section>
</template>

<style scoped>
.hero { text-align: center; max-width: 720px; margin: 0 auto; }
.actions { display:flex; gap:1rem; justify-content:center; margin-top:1.5rem }
.cta { padding:0.8rem 1.2rem; background:var(--accent); color:#fff; border-radius:6px }
.ghost { padding:0.8rem 1.2rem; border:1px solid var(--line); border-radius:6px; background:transparent }
</style>
