<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/auth'

const router = useRouter()
const form = reactive({ username: '', password: '', confirmPassword: '' })
const error = ref('')
const submitting = ref(false)

async function submit() {
  if (!form.username || !form.password || !form.confirmPassword) {
    error.value = '請填寫所有欄位'
    return
  }
  if (form.password !== form.confirmPassword) {
    error.value = '兩次密碼輸入不一致'
    return
  }

  submitting.value = true
  error.value = ''
  try {
    await register({ username: form.username.trim(), password: form.password })
    router.push('/shop')
  } catch (e) {
    error.value = e.message || '註冊失敗'
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="hero">
    <p class="eyebrow muted">會員註冊</p>
    <h1>建立您的帳號</h1>
    <p class="lede muted">快速註冊後即可新增商品、建立訂單並查看結帳資訊。</p>
  </section>

  <div class="card auth-card">
    <form @submit.prevent="submit">
      <div class="field">
        <label for="username">帳號</label>
        <input id="username" v-model="form.username" type="text" placeholder="輸入帳號" autocomplete="username" />
      </div>
      <div class="field">
        <label for="password">密碼</label>
        <input id="password" v-model="form.password" type="password" placeholder="輸入密碼" autocomplete="new-password" />
      </div>
      <div class="field">
        <label for="confirmPassword">確認密碼</label>
        <input id="confirmPassword" v-model="form.confirmPassword" type="password" placeholder="再次輸入密碼" autocomplete="new-password" />
      </div>

      <p v-if="error" class="error">{{ error }}</p>

      <div class="actions">
        <button type="submit" :disabled="submitting">{{ submitting ? '註冊中…' : '註冊' }}</button>
      </div>
    </form>
    <p class="muted small">已經有帳號了？<router-link to="/login">前往登入</router-link></p>
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
