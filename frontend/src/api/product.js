import http from './http'

export const productApi = {
  listAll:        ()         => http.get('/api/product'),
  listAvailable:  ()         => http.get('/api/product/available'),
  create:         payload    => http.post('/api/product', payload)
}
