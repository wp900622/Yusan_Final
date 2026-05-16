## 技術棧

| 層 | 技術 |
|---|---|
| 前端 | Vue 3.5 · Vite · Pinia · Vue Router · axios |
| 後端 | Spring Boot 4.0.6 · Spring Security · Spring Data JPA · JWT (jjwt 0.12.5) · OWASP HTML Sanitizer |
| 資料庫 | PostgreSQL 13+（含 Stored Procedure） |
| 建置 | Gradle 8 / Java 17+ · Node 20+ |

## 結構

```
Yusan_Final/
├── backend/    # Spring Boot 後端 (port 8081)
│   └── src/main/java/com/example/bank/
│       ├── Controller/   # AuthController, OrderController, ProductController
│       ├── Service/      # OrderService, ProductService, UserService
│       ├── Repository/   # JPA + Native Query + SP 呼叫
│       ├── Entity/       # Order, Product, UserEntity, Role
│       ├── DTO/          # OrderRequest, SignUpRequest, AuthResponse...
│       ├── Security/     # SecurityConfig (JWT + @PreAuthorize)
│       └── util/         # JwtUtil, JwtAuthenticationFilter, XssSanitizer
├── frontend/   # Vue 3 前端 (port 5173+)
│   └── src/
│       ├── api/          # axios client
│       ├── components/   # Pages & forms
│       ├── router/       # Vue Router + admin 守衛
│       └── stores/       # Pinia auth store
└── db/
    ├── DDL.sql           # 表結構
    └── DML.sql           # Stored Procedures
```

## 啟動

### 1) 資料庫

需要本機 PostgreSQL 跑在 `localhost:5432`，建立 `shoppingdb`：

```sql
-- 在 psql 內執行
CREATE DATABASE shoppingdb;
\c shoppingdb
\i db/DDL.sql
\i db/DML.sql
```

連線設定在 [backend/src/main/resources/application.properties](backend/src/main/resources/application.properties)，預設 `postgres` / `wangpin0622`，請依本機環境調整。

### 2) 後端

```bash
cd backend
./gradlew bootRun
```

Spring Boot 跑在 http://localhost:8081

> Java 17 以上。Gradle wrapper 會在第一次自動下載 Adoptium JDK 17 toolchain。

### 3) 前端

```bash
cd frontend
npm install
npm run dev
```

Vite 預設 http://localhost:5173（若被佔用會自動換 port）。已設好對 `/auth`、`/order`、`/orders`、`/api` 的 proxy 指向後端 8081。

## 帳號 / 權限

- 註冊 `username` 為 **`admin`** 的帳號會自動取得 `ADMIN` 角色（先搶先贏制）；其他帳號為 `USER`。
- 後端 `/auth/**` 免驗證；其餘 API 全部需要 JWT。
- 需要管理員權限的 API 使用 `@PreAuthorize("hasRole('ADMIN')")`，前端用 router `beforeEach` 守衛阻擋 `/admin`、`/product/success`。

### 主要 API

| Method | Path | 權限 | 說明 |
|---|---|---|---|
| POST | `/auth/signup` | 公開 | 註冊（會驗證 email 格式 + XSS 清淨 username/realName） |
| POST | `/auth/login` | 公開 | 登入，回傳 JWT |
| GET  | `/api/product/available` | 已登入 | 取得有庫存商品 |
| POST | `/api/product` | ADMIN | 新增商品（呼叫 `sp_insert_product`） |
| POST | `/order` | 已登入 | 建立訂單（呼叫 `sp_insert_order_main` + `sp_process_order_item`） |
| GET  | `/orders/{id}` | 已登入 | 查詢訂單 |

## 資料庫設計

- `users` — 帳號、BCrypt 密碼、角色（`USER` / `ADMIN`）
- `products` — 商品主檔
- `orders` — 訂單主檔，`member_id` FK → `users.user_id`
- `order_details` — 訂單明細，購買當下價格快照

下單流程（Service 一支 Transaction 內完成）：

1. `sp_insert_order_main` — 寫入訂單主檔
2. 每個品項呼叫 `sp_process_order_item` — 行鎖檢查庫存、扣庫存、寫明細
3. 後端 native query 重算 `total_price`

## 安全機制

- **JWT** — 24 小時 token，subject 放 username，後端從 SecurityContext 取得登入者。
- **下單者身份** — `member_id` 一律從 JWT 解析、不接受前端傳入，避免冒用。
- **XSS** — 註冊欄位 username/realName 經 [`XssSanitizer.clean()`](backend/src/main/java/com/example/bank/util/XssSanitizer.java) 剝除 HTML；email 改用 `@Email` 格式驗證以保留 `@` 符號。
- **密碼** — `BCryptPasswordEncoder` 雜湊。
