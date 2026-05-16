import http from './http'

const BaseURL = 'http://localhost:8081/api/'
export const productApi = {
  listAll:        ()         => http.get(`${BaseURL}product`),
  listAvailable:  ()         => http.get(`${BaseURL}product/available`),
  create:         payload    => http.post(`${BaseURL}product`, payload)
}
