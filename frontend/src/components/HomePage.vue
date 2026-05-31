<script setup>
import { ref, reactive, onMounted, inject, computed } from 'vue'
import { productApi } from '../api/product'
import { isLoggedIn, getCurrentUser } from '../api/auth'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'

const products = ref([])
const loading = ref(false)
const toast = inject('toast')
const authStore = useAuthStore()
const cart = useCartStore()
const qty = reactive({})
const authenticated = computed(() => isLoggedIn())
const user = computed(() => getCurrentUser())

function addToCart (product) {
  if (product.stock === 0) return
  cart.addItem(product, qty[product.productId] || 1)
  toast(`已將「${product.productName}」加入購物車`, 'success')
}

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
    for (const p of products.value) {
      if (!qty[p.productId]) qty[p.productId] = 1
    }
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
          <th class="right" style="width:90px">數量</th>
          <th class="right" style="width:130px"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.productId">

          <td class="num muted">{{ product.productId }}</td>
          <td>{{ product.productName }}</td>
          <td class="right num">{{ fmt(product.price) }}</td>
          <td class="right num" :class="{ accent: product.stock === 0 }">{{ product.stock }}</td>
          <td class="right">
            <input type="number"
                   min="1"
                   :max="product.stock || undefined"
                   :disabled="product.stock === 0"
                   v-model.number="qty[product.productId]"
                   style="text-align:right; width:70px" />
          </td>
          <td class="right">
            <button class="add-btn"
                    :disabled="product.stock === 0"
                    @click="addToCart(product)">
              {{ product.stock === 0 ? '已售完' : '加入購物車' }}
            </button>
          </td>
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
.add-btn {
  font-size: 0.8rem;
  padding: 0.45rem 0.85rem;
  white-space: nowrap;
}
</style>
