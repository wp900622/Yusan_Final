<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

const products = ref([]);
const selectedProducts = ref([]);


// 取得庫存 > 0 的商品
const loadProducts = async () => {
  const res = await axios.get('http://localhost:8081/api/product/available');
  products.value = res.data.map(p => ({ ...p, buyQty: 1 }));
};

// 數量校驗：不能大於庫存
const validateQty = (p) => {
  if (p.buyQty > p.stock) p.buyQty = p.stock;
  if (p.buyQty < 1) p.buyQty = 1;
};

// 計算總金額
const totalAmount = computed(() => {
  return selectedProducts.value.reduce((sum, p) => sum + (p.price * p.buyQty), 0);
});

// 送出訂單
const submitOrder = async () => {
  const request = {
    memberId: "USER_001",
    Items: selectedProducts.value.map(p => ({ productId: p.productId, quantity: p.buyQty }))
  };
  console.log(request);
  try {
    const res = await axios.post('http://localhost:8081/order', request);
    alert('訂單建立成功！編號：' + res.data);
    loadProducts(); // 重新載入最新庫存
    selectedProducts.value = [];
  } catch (err) {
    alert('失敗：' + err.response.data);
  }
};

onMounted(loadProducts);
</script>
<template>
  <div class="order-container">
    <h2>1. 選擇商品 (庫存 > 0)</h2>
    <table border="1">
    <thead>
      <tr>
        <th>勾選</th>
        <th>商品</th>
        <th>單價</th>
        <th>庫存</th>
        <th>購買數量</th>
        <th>小計</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="p in products" :key="p.productId">
        <td><input type="checkbox" :value="p" v-model="selectedProducts"></td>
        <td>{{ p.productName }}</td>
        <td>${{ p.price }}</td>
        <td>{{ p.stock }}</td>
        <td>
          <input type="number" v-model.number="p.buyQty" min="1" :max="p.stock" 
                 @input="validateQty(p)">
        </td>
        <td>${{ (p.price * (p.buyQty || 0)) }}</td>
      </tr>
    </tbody>
    </table>

    <div v-if="selectedProducts.length > 0" class="summary">
      <h2>2. 訂單確認</h2>
      <ul>
        <li v-for="item in selectedProducts" :key="item.productId">
          {{ item.productName }} x {{ item.buyQty }} = ${{ item.price * item.buyQty }}
        </li>
      </ul>
      <h3>訂單總金額：${{ totalAmount }}</h3>
      <button @click="submitOrder">建立訂單</button>
    </div>
  </div>
</template>
