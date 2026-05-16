// Minimal Node.js backend stub
const http = require('http');

const PORT = 3000;

const server = http.createServer((req, res) => {
  res.writeHead(200, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({ message: 'Yusan API up' }));
});

server.listen(PORT, () => {
  console.log(`Backend listening on http://localhost:${PORT}`);
});
