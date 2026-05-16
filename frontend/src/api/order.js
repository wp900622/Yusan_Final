import http from './http'
const BaseURL = 'http://localhost:8081/'
export const orderApi = {
  create: payload      => http.post(`http://localhost:8081/order`, payload),
  getOne: id           => http.get(`${BaseURL}orders/${id}`)
}
