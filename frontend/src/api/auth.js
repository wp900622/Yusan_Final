const STORAGE_USERS = 'shopping_web_users'
const STORAGE_SESSION = 'shopping_web_session'

function getUsers() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_USERS) || '[]')
  } catch {
    return []
  }
}

function saveUsers(users) {
  localStorage.setItem(STORAGE_USERS, JSON.stringify(users))
}

function setSession(user) {
  localStorage.setItem(STORAGE_SESSION, JSON.stringify(user))
}

function clearSession() {
  localStorage.removeItem(STORAGE_SESSION)
}

export function register({ username, password }) {
  const users = getUsers()
  if (users.some(u => u.username === username)) {
    return Promise.reject(new Error('此帳號已存在'))
  }
  const newUser = { username, password }
  users.push(newUser)
  saveUsers(users)
  setSession({ username })
  return Promise.resolve({ username })
}

export function login({ username, password }) {
  const users = getUsers()
  const user = users.find(u => u.username === username && u.password === password)
  if (!user) {
    return Promise.reject(new Error('帳號或密碼不正確'))
  }
  setSession({ username })
  return Promise.resolve({ username })
}

export function logout() {
  clearSession()
}

export function getCurrentUser() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_SESSION) || 'null')
  } catch {
    return null
  }
}

export function isLoggedIn() {
  return Boolean(getCurrentUser())
}
