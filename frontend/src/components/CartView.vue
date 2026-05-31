<script setup>
import { inject } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useCartStore } from '../stores/cart'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const toast  = inject('toast')

const cart = useCartStore()
const auth = useAuthStore()
const { items, customerName, totalAmount, isEmpty } = storeToRefs(cart)

function onQtyInput (item, e) {
  cart.setQuantity(item.productId, e.target.value)
}

function remove (item) {
  cart.removeItem(item.productId)
  toast(`已從購物車移除「${item.productName}」`, 'info')
}

function clearAll () {
  cart.clear()
  toast('購物車已清空', 'info')
}

function goToCheckout () {
  if (isEmpty.value) {
    toast('購物車是空的', 'error')
    return
  }
  if (!auth.isLoggedIn) {
    toast('請先登入再結帳', 'error')
    router.push({ path: '/login', query: { redirect: '/checkout' } })
    return
  }
  router.push('/checkout')
}

function fmt (n) {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(n)
}
</script>

<template>
  <section class="hero">
    <p class="eyebrow muted">購物車 / Cart</p>
    <h1>您的<span class="serif"><em>購物車</em></span></h1>
    <p class="lede muted">確認商品與數量後，即可前往結帳。</p>
  </section>

  <div class="grid">
    <!-- ────────── Cart items ────────── -->
    <div class="card">
      <div class="card-head">
        <h2>購物車商品</h2>
        <button v-if="!isEmpty" class="ghost small" @click="clearAll">清空購物車</button>
      </div>
      <div class="divider"></div>

      <table v-if="!isEmpty">
        <thead>
          <tr>
            <th>商品</th>
            <th class="right">單價</th>
            <th class="right" style="width:120px">數量</th>
            <th class="right">小計</th>
            <th style="width:48px"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in items" :key="item.productId">
            <td>
              <div class="serif">{{ item.productName }}</div>
              <div class="muted small num">{{ item.productId }}</div>
            </td>
            <td class="right num">{{ fmt(item.price) }}</td>
            <td class="right">
              <input type="number"
                     min="1"
                     :max="item.stock || undefined"
                     :value="item.quantity"
                     @input="onQtyInput(item, $event)"
                     style="text-align:right" />
            </td>
            <td class="right num">{{ fmt(item.price * item.quantity) }}</td>
            <td class="right">
              <button class="link-danger" title="移除" @click="remove(item)">✕</button>
            </td>
          </tr>
        </tbody>
      </table>

      <p v-else class="muted center" style="padding:2rem 0">
        購物車是空的，<router-link to="/shop">去逛逛商品</router-link>吧。
      </p>
    </div>

    <!-- ────────── Summary ────────── -->
    <aside class="card cart">
      <h2>結帳資訊</h2>
      <div class="divider"></div>

      <div class="field">
        <label for="cn">顧客名稱</label>
        <input id="cn"
               :value="customerName"
               @input="cart.setCustomerName($event.target.value)"
               type="text" placeholder="(選填)" maxlength="64" />
      </div>

      <div class="divider"></div>

      <div class="total">
        <span class="muted">訂單總金額</span>
        <span class="total-num serif">{{ fmt(totalAmount) }}</span>
      </div>

      <button class="cta" :disabled="isEmpty" @click="goToCheckout">
        前往結帳
      </button>
      <button class="ghost" style="width:100%; margin-top:0.6rem" @click="router.push('/shop')">
        繼續購物
      </button>
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

.link-danger {
  background: none;
  border: none;
  color: var(--danger);
  cursor: pointer;
  font-size: 1rem;
}

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
