<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '../api/order'

const route  = useRoute()
const router = useRouter()
const toast  = inject('toast')

const order = ref(null)
const loading = ref(false)

async function load () {
  loading.value = true
  try {
    order.value = await orderApi.getOne(route.params.id)
  } catch (e) {
    toast(e.message, 'error')
    router.push('/shop')
  } finally {
    loading.value = false
  }
}

function fmt (n) {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(n)
}

function fmtDate (s) {
  if (!s) return ''
  return new Date(s).toLocaleString('zh-TW')
}

onMounted(load)
</script>

<template>
  <section class="hero">
    <p class="eyebrow muted">訂單收據 / Receipt</p>
    <h1>訂單<span class="serif"><em>已建立</em></span></h1>
    <p class="lede muted" v-if="order">
      編號 <span class="num">{{ order.orderNo }}</span> · {{ fmtDate(order.createdAt) }}
    </p>
  </section>

  <div class="card receipt" v-if="order">
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
        <tr v-for="item in order.items" :key="item.itemId">
          <td>
            <div class="serif">{{ item.productName }}</div>
            <div class="muted small num">{{ item.productId }}</div>
          </td>
          <td class="right num">{{ fmt(item.unitPrice) }}</td>
          <td class="right num">{{ item.quantity }}</td>
          <td class="right num">{{ fmt(item.subtotal) }}</td>
        </tr>
      </tbody>
    </table>

    <div class="divider"></div>

    <div class="footer-rows">
      <div class="row">
        <span class="muted">顧客名稱</span>
        <span>{{ order.customerName || '—' }}</span>
      </div>
      <div class="row total">
        <span class="muted">總金額</span>
        <span class="total-num serif">{{ fmt(order.totalAmount) }}</span>
      </div>
    </div>

    <div class="actions">
      <button class="ghost" @click="router.push('/shop')">繼續購物</button>
      <button @click="router.push('/admin')">回到 Admin</button>
    </div>
  </div>

  <div v-else-if="loading" class="muted center" style="padding:2rem">載入中…</div>
</template>

<style scoped>
.hero      { margin-bottom: 2rem; max-width: 720px; }
.eyebrow   { font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.18em; margin-bottom: 0.4rem; }
.hero h1   { margin-bottom: 0.6rem; }
.hero h1 em { font-style: italic; color: var(--accent); }
.lede      { font-size: 1.05rem; }

.receipt   { max-width: 800px; }

.footer-rows { display: flex; flex-direction: column; gap: 0.5rem; margin-bottom: 1.5rem; }
.row         { display: flex; justify-content: space-between; align-items: baseline; }
.row.total   { margin-top: 0.5rem; }
.total-num   { font-size: 1.7rem; color: var(--accent); }
.actions     { display: flex; gap: 0.75rem; }
</style>
