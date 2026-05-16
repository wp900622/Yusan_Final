<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api/order'

const router = useRouter()
const checkout = ref(null)
const loading = ref(false)
const error = ref('')

const totalAmount = computed(() => checkout.value?.items?.reduce((sum, item) => sum + item.subtotal, 0) || 0)

function loadCheckout() {
  try {
    const raw = localStorage.getItem('shopping_web_checkout')
    checkout.value = raw ? JSON.parse(raw) : null
  } catch {
    checkout.value = null
  }
  if (!checkout.value || !checkout.value.items?.length) {
    router.push('/shop')
  }
}

async function placeOrder() {
  const items = Array.isArray(checkout.value?.items) ? checkout.value.items : []
  if (!items.length) {
    error.value = '訂單內容不完整，請重新挑選商品'
    return
  }

  const payload = {
    customerName: checkout.value.customerName || null,
    items: items.map(item => ({
      productId: item.productId,
      quantity: Number(item.quantity) || 0
    }))
  }

  if (!payload.items.length) {
    error.value = '訂單內容不完整，請重新挑選商品'
    return
  }

  loading.value = true
  error.value = ''
  try {
    console.log('checkout payload', payload)
    const created = await orderApi.create(payload)
    localStorage.removeItem('shopping_web_checkout')
    router.push(`/orders/${created.orderId}`)
  } catch (e) {
    error.value = e.message || '結帳失敗'
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

onMounted(loadCheckout)
</script>

<template>
  <section class="hero">
    <p class="eyebrow muted">結帳頁面</p>
    <h1>確認您的訂單</h1>
    <p class="lede muted">請檢查商品明細與總金額，然後完成結帳。</p>
  </section>

  <div class="card receipt" v-if="checkout">
    <table>
      <thead>
        <tr>
          <th>商品</th>
          <th class="right">單價</th>
          <th class="right">數量</th>
          <th class="right">小計</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in checkout.items" :key="item.productId">
          <td>
            <div class="serif">{{ item.productName }}</div>
            <div class="muted small num">{{ item.productId }}</div>
          </td>
          <td class="right num">{{ fmt(item.price) }}</td>
          <td class="right num">{{ item.quantity }}</td>
          <td class="right num">{{ fmt(item.subtotal) }}</td>
        </tr>
      </tbody>
    </table>

    <div class="divider"></div>

    <div class="footer-rows">
      <div class="row">
        <span class="muted">顧客名稱</span>
        <span>{{ checkout.customerName || '未填寫' }}</span>
      </div>
      <div class="row total">
        <span class="muted">預估總金額</span>
        <span class="total-num serif">{{ fmt(totalAmount) }}</span>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="actions">
      <button class="ghost" @click="router.push('/shop')">返回購物</button>
      <button @click="placeOrder" :disabled="loading">{{ loading ? '處理中…' : '完成結帳' }}</button>
    </div>
  </div>
</template>

<style scoped>
.footer-rows { display: flex; flex-direction: column; gap: 0.5rem; margin-bottom: 1.5rem; }
.row { display: flex; justify-content: space-between; align-items: baseline; }
.row.total { margin-top: 0.5rem; }
.total-num { font-size: 1.7rem; color: var(--accent); }
.error { color: var(--danger); margin: 1rem 0; }
.actions { display: flex; justify-content: space-between; gap: 0.75rem; }
</style>
