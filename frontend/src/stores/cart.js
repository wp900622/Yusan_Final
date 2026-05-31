import { defineStore } from 'pinia'

const STORAGE_KEY = 'shopping_web_cart'

function loadState () {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    const parsed = raw ? JSON.parse(raw) : null
    return {
      items: Array.isArray(parsed?.items) ? parsed.items : [],
      customerName: typeof parsed?.customerName === 'string' ? parsed.customerName : '',
    }
  } catch {
    return { items: [], customerName: '' }
  }
}

export const useCartStore = defineStore('cart', {
  state: () => loadState(),

  getters: {
    // 購物車內商品總件數 (用於導覽列徽章)
    count: state => state.items.reduce((sum, i) => sum + (Number(i.quantity) || 0), 0),
    // 訂單總金額
    totalAmount: state =>
      state.items.reduce((sum, i) => sum + Number(i.price) * (Number(i.quantity) || 0), 0),
    isEmpty: state => state.items.length === 0,
  },

  actions: {
    persist () {
      try {
        localStorage.setItem(
          STORAGE_KEY,
          JSON.stringify({ items: this.items, customerName: this.customerName })
        )
      } catch { /* 忽略寫入失敗 (例如無痕模式) */ }
    },

    // 加入商品；若已存在則累加數量，並夾在庫存上限內
    addItem (product, qty = 1) {
      const quantity = Math.max(1, Number(qty) || 1)
      const stock = Number(product.stock) || 0
      const existing = this.items.find(i => i.productId === product.productId)

      if (existing) {
        existing.quantity = Math.min(existing.quantity + quantity, stock || existing.quantity + quantity)
        existing.stock = stock
        existing.price = Number(product.price)
      } else {
        this.items.push({
          productId: product.productId,
          productName: product.productName,
          price: Number(product.price),
          stock,
          quantity: stock ? Math.min(quantity, stock) : quantity,
        })
      }
      this.persist()
    },

    setQuantity (productId, qty) {
      const item = this.items.find(i => i.productId === productId)
      if (!item) return
      let q = Math.floor(Number(qty) || 1)
      if (q < 1) q = 1
      if (item.stock && q > item.stock) q = item.stock
      item.quantity = q
      this.persist()
    },

    removeItem (productId) {
      this.items = this.items.filter(i => i.productId !== productId)
      this.persist()
    },

    setCustomerName (name) {
      this.customerName = name
      this.persist()
    },

    clear () {
      this.items = []
      this.customerName = ''
      this.persist()
    },
  },
})
