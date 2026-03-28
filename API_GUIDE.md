# API 사용 가이드 (프론트엔드 개발자용)

## 🚀 빠른 시작

### 1. 백엔드 서버 실행

```bash
# Docker로 실행 (권장)
docker-compose up -d

# 서버 상태 확인
curl http://localhost:8080/actuator/health
```

### 2. API 문서 확인

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 📡 API 기본 정보

### Base URL
```
로컬: http://localhost:8080
배포: https://api.yourdomain.com
```

### 공통 응답 형식

#### 성공 응답
```json
{
  "status": "success",
  "data": {
    // 실제 데이터
  }
}
```

#### 에러 응답
```json
{
  "status": "error",
  "code": "C001",
  "message": "Error message"
}
```

### 에러 코드 목록

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| C001 | 500 | Internal server error |
| C002 | 400 | Invalid input value |
| C003 | 405 | Method not allowed |
| C004 | 404 | Entity not found |

## 📝 API 엔드포인트

ERD 설계 및 도메인 구현 후 API 엔드포인트가 추가될 예정입니다.

현재는 기본 인프라만 구성되어 있습니다:
- Health Check: `/actuator/health`
- Swagger UI: `/swagger-ui.html`
- OpenAPI Spec: `/api-docs`

## 🔧 Next.js에서 사용 예시

### API Client 설정 (axios)

```typescript
// lib/api/client.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // CORS credentials 포함
});

// 응답 인터셉터
apiClient.interceptors.response.use(
  (response) => response.data, // ApiResponse<T>의 data 추출
  (error) => {
    if (error.response?.data) {
      // 백엔드 에러 형식 처리
      const { code, message } = error.response.data;
      console.error(`API Error [${code}]: ${message}`);
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### React Query 설정 예시

```typescript
// app/providers.tsx
'use client';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactNode, useState } from 'react';

export function Providers({ children }: { children: ReactNode }) {
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 60 * 1000, // 1분
            refetchOnWindowFocus: false,
          },
        },
      })
  );

  return (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
}
```

## 🐛 디버깅

### CORS 에러가 발생하면

1. 백엔드 서버 실행 확인: `curl http://localhost:8080/actuator/health`
2. CORS 설정 확인: `CorsConfig.java`에 프론트엔드 URL 추가

### 연결이 안 되면

```bash
# 백엔드 서버 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f app

# 포트 확인
lsof -i:8080
```

## 📞 백엔드 개발자에게 문의할 때

다음 정보를 함께 공유해주세요:

1. **요청 URL**: 어떤 API를 호출했나요?
2. **요청 Body**: 어떤 데이터를 보냈나요?
3. **응답 상태 코드**: 200, 400, 500 등
4. **에러 메시지**: 콘솔에 표시된 에러
5. **브라우저 Network 탭** 스크린샷

## 🔄 API 변경 사항

API가 변경되면 이 문서와 Swagger가 자동으로 업데이트됩니다.
항상 최신 버전은 http://localhost:8080/swagger-ui.html 에서 확인하세요.