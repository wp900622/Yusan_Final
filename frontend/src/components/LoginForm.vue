<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'

const router = useRouter()
const form = reactive({ username: '', password: '' })
const error = ref('')
const submitting = ref(false)

async function submit() {
  if (!form.username || !form.password) {
    error.value = '請輸入帳號與密碼'
    return
  }

  submitting.value = true
  error.value = ''
  try {
    await login({ username: form.username.trim(), password: form.password })
    router.push('/shop')
  } catch (e) {
    error.value = e.message || '登入失敗'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="hero">
    <p class="eyebrow muted">會員登入</p>
    <h1>歡迎回來</h1>
    <p class="lede muted">請使用帳號密碼登入，享受快速購物與管理。</p>
  </section>

  <div class="card auth-card">
    <form @submit.prevent="submit">
      <div class="field">
        <label for="username">帳號</label>
        <input id="username" v-model="form.username" type="text" placeholder="輸入帳號" autocomplete="username" />
      </div>

      <div class="field">
        <label for="password">密碼</label>
        <input id="password" v-model="form.password" type="password" placeholder="輸入密碼" autocomplete="current-password" />
      </div>

      <p v-if="error" class="error">{{ error }}</p>

      <div class="actions">
        <button type="submit" :disabled="submitting">{{ submitting ? '登入中…' : '登入' }}</button>
      </div>
    </form>
    <p class="muted small">還沒有帳號？<router-link to="/register">註冊一個</router-link></p>
  </div>
</template>

<style scoped>
.auth-card {
  max-width: 420px;
  margin: 0 auto;
}
.field { margin-bottom: 1rem; }
.error { color: var(--danger); margin: 0.5rem 0; }
.actions { display: flex; justify-content: flex-end; }
</style>
