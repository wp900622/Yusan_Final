<script setup>
import { ref, onMounted, inject, computed } from 'vue'
import { productApi } from '../api/product'
import { isLoggedIn, getCurrentUser } from '../api/auth'

const products = ref([])
const loading = ref(false)
const toast = inject('toast')

const authenticated = computed(() => isLoggedIn())
const user = computed(() => getCurrentUser())

async function loadProducts() {
  loading.value = true
  try {
    const data = await productApi.listAvailable()
    products.value = Array.isArray(data)
      ? data.map(p => ({
          ...p,
          price: Number(p.price),
          stock: Number(p.stock)
        }))
      : []
  } catch (e) {
    toast?.(e.message || '載入商品失敗', 'error')
  } finally {
    loading.value = false
  }
}

function fmt(n) {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(n)
}

onMounted(loadProducts)
</script>

<template>
  <section class="hero home-hero">
    <p class="muted small" v-if="authenticated">目前登入：{{ user?.username }}</p>
  </section>

  <section class="product-list">
    <div class="list-head">
      <h2>商品列表</h2>
      <span class="muted">共 {{ products.length }} 項</span>
    </div>

    <table v-if="products.length">
      <thead>
        <tr>
          <th>編號</th>
          <th>名稱</th>
          <th class="right">售價</th>
          <th class="right">庫存</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.productId">
        
          <td class="num muted">{{ product.productId }}</td>
          <td>{{ product.productName }}</td>
          <td class="right num">{{ fmt(product.price) }}</td>
          <td class="right num" :class="{ accent: product.stock === 0 }">{{ product.stock }}</td>
        </tr>
      </tbody>
    </table>

    <div v-else class="empty-state">
      <p class="muted">目前沒有可顯示的商品。</p>
    </div>
  </section>
</template>

<style scoped>
.hero.home-hero {
  max-width: 740px;
  margin: 0 auto 2rem;
  text-align: center;
}
.home-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem;
  margin: 2rem 0;
}
.cta,
.ghost {
  padding: 0.95rem 1.5rem;
  border-radius: 999px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}
.cta {
  background: var(--accent);
  color: white;
}
.ghost {
  border: 1px solid currentColor;
  background: transparent;
  color: var(--ink);
}
.product-list {
  max-width: 980px;
  margin: 0 auto 4rem;
}
.list-head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 1rem;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 1rem 0.75rem;
  text-align: left;
}
th.right, td.right {
  text-align: right;
}
thead {
  border-bottom: 1px solid var(--line);
}
tbody tr {
  border-bottom: 1px solid #ece9e4;
}
.empty-state {
  padding: 2rem;
  text-align: center;
  color: var(--ink-soft);
}
</style>
