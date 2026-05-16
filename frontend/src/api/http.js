import axios from 'axios'

const TOKEN_KEY = 'shopping_web_token'

const http = axios.create({
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

http.interceptors.request.use(config => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Normalise responses so callers always get the unwrapped payload
// and reject with a useful message on backend-defined errors.
http.interceptors.response.use(
  res => {
    const body = res.data
    if (body && body.success === false) {
      return Promise.reject(new Error(body.message || 'Request failed'))
    }
    return body && typeof body === 'object' && 'data' in body ? body.data : body
  },
  err => {
    const serverData = err?.response?.data
    const msg =
      serverData?.message ||
      (typeof serverData === 'string' ? serverData : null) ||
      err?.message ||
      'Network error'
    return Promise.reject(new Error(msg))
  }
)

export default http
