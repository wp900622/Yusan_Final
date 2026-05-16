<script setup>
import { ref, reactive, computed, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '../api/product'

const router = useRouter()
const toast  = inject('toast')

const products   = ref([])
const selection  = reactive({})
const customer   = ref('')
const loading    = ref(false)

async function loadProducts () {
  loading.value = true
  try {
    const data = await productApi.listAvailable()
    const items = Array.isArray(data) ? data : []
    products.value = items.map(p => ({
      ...p,
      price: Number(p.price),
      stock: Number(p.stock)
    }))
    for (const p of products.value) {
      if (!selection[p.productId]) {
        selection[p.productId] = { selected: false, quantity: 1 }
      }
    }
  } catch (e) {
    toast(e.message, 'error')
  } finally {
    loading.value = false
  }
}

function clampQty (p) {
  const s = selection[p.productId]
  if (s.quantity < 1) s.quantity = 1
  if (s.quantity > p.stock) {
    s.quantity = p.stock
    toast(`「${p.productName}」最多 ${p.stock} 件`, 'error')
  }
}

const orderLines = computed(() => {
  return products.value
    .filter(p => selection[p.productId]?.selected)
    .map(p => {
      const qty = Number(selection[p.productId].quantity) || 0
      const subtotal = +(Number(p.price) * qty).toFixed(2)
      return { ...p, quantity: qty, subtotal }
    })
    .filter(l => l.quantity > 0)
})

const totalAmount = computed(() =>
  orderLines.value.reduce((sum, l) => sum + l.subtotal, 0)
)

function goToCheckout () {
  if (orderLines.value.length === 0) {
    toast('請至少勾選一項商品', 'error')
    return
  }
  const payload = {
    customerName: customer.value.trim() || null,
    items: orderLines.value.map(l => ({
      productId: l.productId,
      productName: l.productName,
      price: l.price,
      quantity: l.quantity,
      subtotal: l.subtotal
    }))
  }
  localStorage.setItem('shopping_web_checkout', JSON.stringify(payload))
  router.push('/checkout')
}

function fmt (n) {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(n)
}

onMounted(loadProducts)
</script>

<template>
  <section class="hero">
    <p class="eyebrow muted">顧客介面 / Shop</p>
    <h1>商品<span class="serif"><em>選購</em></span></h1>
    <p class="lede muted">
      下方僅顯示庫存大於 0 的商品。勾選並調整數量,即可送出訂單。
    </p>
  </section>

  <div class="grid">
    <!-- ────────── Product chooser ────────── -->
    <div class="card">
      <div class="card-head">
        <h2>商品清單</h2>
        <button class="ghost small" @click="loadProducts" :disabled="loading">
          {{ loading ? '載入中…' : '重新整理' }}
        </button>
      </div>
      <div class="divider"></div>

      <table v-if="products.length">
        <thead>
          <tr>
            <th style="width:40px"></th>
            <th>商品</th>
            <th class="right">售價</th>
            <th class="right">庫存</th>
            <th class="right" style="width:120px">數量</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.productId">
            <td>
              <input type="checkbox" v-model="selection[p.productId].selected" />
            </td>
            <td>
              <div class="serif">{{ p.productName }}</div>
              <div class="muted small num">{{ p.productId }}</div>
            </td>
            <td class="right num">{{ fmt(p.price) }}</td>
            <td class="right num">{{ p.stock }}</td>
            <td class="right">
              <input type="number"
                     min="1"
                     :max="p.stock"
                     :disabled="!selection[p.productId].selected"
                     v-model.number="selection[p.productId].quantity"
                     @input="clampQty(p)"
                     style="text-align:right" />
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="muted center" style="padding:2rem 0">目前無可購買商品</p>
    </div>

    <!-- ────────── Cart summary ────────── -->
    <aside class="card cart">
      <h2>訂單明細</h2>
      <div class="divider"></div>

      <div class="field">
        <label for="cn">顧客名稱</label>
        <input id="cn" v-model="customer" type="text" placeholder="(選填)" maxlength="64" />
      </div>

      <div class="divider"></div>

      <template v-if="orderLines.length">
        <div class="line" v-for="l in orderLines" :key="l.productId">
          <div class="line-main">
            <div class="serif">{{ l.productName }}</div>
            <div class="muted small num">{{ fmt(l.price) }} × {{ l.quantity }}</div>
          </div>
          <div class="num">{{ fmt(l.subtotal) }}</div>
        </div>

        <div class="divider"></div>

        <div class="total">
          <span class="muted">訂單總金額</span>
          <span class="total-num serif">{{ fmt(totalAmount) }}</span>
        </div>

        <button class="cta" @click="goToCheckout">
          前往結帳
        </button>
      </template>
      <p v-else class="muted center" style="padding:1rem 0">
        勾選左側商品以開始
      </p>
    </aside>
  </div>
</template>

<style scoped>
.hero      { margin-bottom: 2.5rem; max-width: 720px; }
.eyebrow   { font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.18em; margin-bottom: 0.4rem; }
.hero h1   { margin-bottom: 0.6rem; }
.hero h1 em { font-style: italic; color: var(--accent); }
.lede      { font-size: 1.05rem; }

.grid {
  display: grid;
  grid-template-columns: 2fr minmax(320px, 1fr);
  gap: 1.5rem;
  align-items: start;
}
@media (max-width: 980px) { .grid { grid-template-columns: 1fr; } }

.card-head { display: flex; justify-content: space-between; align-items: center; }
.small { font-size: 0.8rem; padding: 0.4rem 0.8rem; }

.cart   { position: sticky; top: 100px; }
.field  { margin-bottom: 0.5rem; }

.line {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  padding: 0.65rem 0;
}
.line-main { flex: 1; }

.total {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 1.1rem;
}
.total-num { font-size: 1.6rem; font-weight: 500; color: var(--accent); }

.cta {
  width: 100%;
  padding: 0.85rem;
  font-size: 1rem;
  letter-spacing: 0.04em;
}
</style>
