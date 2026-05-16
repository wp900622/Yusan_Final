# Yusan Final

練習 GitHub Flow 標準分支工作流的示範專案。

## 結構

```
Yusan_Final/
├── backend/    # SpringBoot 後端
├── frontend/   # Vue.js 前端
└── db/         # SQL schema
```

## GitHub Flow

- `main` 永遠保持可部署狀態
- 寫功能開短期分支 (`feature/xxx`)
- 修 bug 開短期分支 (`fix/xxx`)
- PR + Code Review → Merge 回 main → 刪分支
