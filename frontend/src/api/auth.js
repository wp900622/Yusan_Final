import axios from 'axios'

const STORAGE_SESSION = 'shopping_web_session'
const STORAGE_TOKEN = 'shopping_web_token'

const http = axios.create()

// ----- localStorage helpers -----

function setSession(user) {
  localStorage.setItem(STORAGE_SESSION, JSON.stringify(user))
}

function clearSession() {
  localStorage.removeItem(STORAGE_SESSION)
  localStorage.removeItem(STORAGE_TOKEN)
}

export function getCurrentUser() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_SESSION) || 'null')
  } catch {
    return null
  }
}

export function getToken() {
  return localStorage.getItem(STORAGE_TOKEN)
}

export function isLoggedIn() {
  return Boolean(getCurrentUser())
}

// ----- error handling -----

function extractErrorMessage(error, fallback) {
  const payload = error.response?.data
  return (
    (payload && (payload.message || payload.error)) ||
    (typeof payload === 'string' ? payload : null) ||
    error.message ||
    fallback
  )
}

// ----- auth API -----

export async function register({ email, name, username, password }) {
  try {
    const { data } = await http.post('/auth/signup', { email, name, username, password })
    return data
  } catch (e) {
    throw new Error(extractErrorMessage(e, '註冊失敗'))
  }
}

export async function login({ username, password }) {
  try {
    const { data } = await http.post('/auth/login', { username, password })
    const resolvedUsername = data?.username || username
    const role = (data?.role || 'user').toLowerCase()
    if (data?.token) {
      localStorage.setItem(STORAGE_TOKEN, data.token)
    }
    setSession({ username: resolvedUsername, role })
    return { username: resolvedUsername, token: data?.token, role }
  } catch (e) {
    throw new Error(extractErrorMessage(e, '登入失敗'))
  }
}

export function logout() {
  clearSession()
}
