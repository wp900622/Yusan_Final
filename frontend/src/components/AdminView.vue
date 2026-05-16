<script setup>
import { ref, reactive, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { productApi } from '../api/product'

const toast = inject('toast')
const router = useRouter()

const products = ref([])
const loading = ref(false)
const submitting = ref(false)

const form = reactive({
  productId: '',
  productName: '',
  price: null,
  stock: null
})

async function loadProducts () {
  loading.value = true
  try {
    const data = await productApi.listAll()
    const items = Array.isArray(data) ? data : []
    products.value = items.map(p => ({
      ...p,
      price: Number(p.price),
      stock: Number(p.stock)
    }))
  } catch (e) {
    toast(e.message, 'error')
  } finally {
    loading.value = false
  }
}

function resetForm () {
  form.productId = ''
  form.productName = ''
  form.price = null
  form.stock = null
}

async function submit () {
  if (!form.productId || !form.productName || form.price == null || form.stock == null) {
    toast('請填寫所有必填欄位', 'error')
    return
  }
  submitting.value = true
  try {
    const payload = {
      productId: String(form.productId).trim(),
      productName: String(form.productName).trim(),
      price: Number(form.price),
      stock: Number(form.stock)
    }
    const created = await productApi.create(payload)
    // refresh list then navigate to success page with created product info
    resetForm()
    await loadProducts()
    router.push({ path: '/product/success', query: { id: payload.productId, name: payload.productName } })
  } catch (e) {
    toast(e.message, 'error')
  } finally {
    submitting.value = false
  }
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
    <p class="eyebrow muted">管理介面 / Admin</p>
    <h1>新增<span class="serif"><em>商品</em></span></h1>
 
  </section>

  <div class="container">
    <!-- ────────── Create form ────────── -->
    <div class="card">
      <h2>新增商品</h2>
      <p class="muted small">填寫以下欄位以新增一筆商品。</p>
      <div class="divider"></div>
      <form @submit.prevent="submit">
        <div class="field">
          <label for="pid">商品編號 *</label>
          <input id="pid" v-model="form.productId" type="text" placeholder="例如:P0001" maxlength="32" />
        </div>
        <div class="field">
          <label for="pname">商品名稱 *</label>
          <input id="pname" v-model="form.productName" type="text" placeholder="例如:陶瓷馬克杯" maxlength="128" />
        </div>
        <div class="row">
          <div class="field">
            <label for="price">售價 *</label>
            <input id="price" v-model.number="form.price" type="number" step="0.01" min="0" placeholder="0.00" />
          </div>
          <div class="field">
            <label for="stock">庫存 *</label>
            <input id="stock" v-model.number="form.stock" type="number" step="1" min="0" placeholder="0" />
          </div>
        </div>

        <div class="actions">
          <button type="submit" :disabled="submitting">
            {{ submitting ? '處理中…' : '新增商品' }}
          </button>
          <button type="button" class="ghost" @click="resetForm" :disabled="submitting">清空</button>
        </div>
      </form>
    </div>

  
  </div>
</template>

<style scoped>
.hero      { margin-bottom: 2.5rem; max-width: 720px; }
.eyebrow   { font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.18em; margin-bottom: 0.4rem; }
.hero h1   { margin-bottom: 0.6rem; }
.hero h1 em { font-style: italic; color: var(--accent); }
.lede      { font-size: 1.05rem; max-width: 600px; }

.container {
  height: 100vh;

  display: flex;
  justify-content: center; /* 水平置中 */
  align-items: center;     /* 垂直置中 */

  background-color: #f5f5f5;
}
@media (max-width: 880px) { .grid { grid-template-columns: 1fr; } }

.field { margin-bottom: 1rem; }
.row   { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.actions { display: flex; gap: 0.75rem; margin-top: 0.5rem; }

.card-head { display: flex; justify-content: space-between; align-items: center; }
.small { font-size: 0.8rem; padding: 0.4rem 0.8rem; }
</style>
