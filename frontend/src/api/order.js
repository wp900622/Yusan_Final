import http from './http'

export const orderApi = {
  create: payload      => http.post('/order', payload),
  getOne: id           => http.get(`/orders/${id}`)
}
