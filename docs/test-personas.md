# 테스트 페르소나 - 중학생 기준

중학생 사용자 3명의 페르소나와 각 API 호출 예시

---

## 페르소나 1 — 김민준 (중2, 우등생형)

**배경**
- 중학교 2학년, 서울 ○○중학교
- 내신 관리가 목표, 중간/기말 시험에 민감
- 저녁 9시~11시 집중 공부, 주말은 오전 공부
- 문제집/강의 혼합형 선호

### 회원가입

```json
POST /api/auth/signup
{
  "email": "minjun.kim.test@example.com",
  "password": "Test1234!",
  "nickname": "민준"
}
```

### 프로필 업데이트

```json
PATCH /api/users/me
{
  "nickname": "민준",
  "ageGroup": "중등",
  "schoolName": "서울○○중학교"
}
```

### 나의 학습 설정

```json
POST /api/my-learning
{
  "preferredStudyTime": "저녁 (21:00~23:00)",
  "offDays": "일요일",
  "preferredMaterialFormat": "문제집 + 강의 혼합",
  "preferredClassStyle": "개념 먼저 설명 후 문제 풀기",
  "preferredStudyMethod": "반복 풀이",
  "preferredTeacherStyle": "친절하고 상세한 설명",
  "preferredFriendStyle": "함께 문제 풀기",
  "extraNotes": "시험 2주 전부터는 집중 모드로 공부하고 싶어요"
}
```

### 학습 목표 생성 — 중간고사 수학

```json
POST /api/goals
{
  "attachmentIds": [],
  "title": "중2 1학기 중간고사 수학",
  "purpose": "수학 90점 이상 달성해서 내신 1등급 유지",
  "targetScore": 90,
  "maxScore": 100,
  "startDate": "2026-07-07",
  "endDate": "2026-07-18",
  "methods": ["개념 정리", "기출 반복 풀이", "오답 노트"],
  "byDayStudyTimes": [
    { "dayOfWeek": 1, "availableMinutes": 90 },
    { "dayOfWeek": 2, "availableMinutes": 90 },
    { "dayOfWeek": 3, "availableMinutes": 90 },
    { "dayOfWeek": 4, "availableMinutes": 90 },
    { "dayOfWeek": 5, "availableMinutes": 60 },
    { "dayOfWeek": 6, "availableMinutes": 120 }
  ],
  "excludeDates": ["2026-07-13"]
}
```

---

## 페르소나 2 — 이소연 (중1, 게임+공부 균형형)

**배경**
- 중학교 1학년, 경기도 ○○중학교
- 공부보다 게임/유튜브를 좋아하지만 최소한의 성적은 유지하고 싶음
- 집중 시간이 짧아 30~40분 단위로 공부
- 시험 1주일 전부터 본격 준비

### 회원가입

```json
POST /api/auth/signup
{
  "email": "soyeon.lee.test@example.com",
  "password": "Test1234!",
  "nickname": "소연"
}
```

### 프로필 업데이트

```json
PATCH /api/users/me
{
  "nickname": "소연이",
  "ageGroup": "중등",
  "schoolName": "경기○○중학교"
}
```

### 나의 학습 설정

```json
POST /api/my-learning
{
  "preferredStudyTime": "오후 (16:00~18:00)",
  "offDays": "토요일, 일요일",
  "preferredMaterialFormat": "유튜브 강의",
  "preferredClassStyle": "짧고 핵심만 설명",
  "preferredStudyMethod": "영상 시청 후 요약",
  "preferredTeacherStyle": "재미있고 유머 있는 선생님",
  "preferredFriendStyle": "각자 공부하는 분위기",
  "extraNotes": "30분 이상 집중이 안 돼서 짧게 끊어서 하고 싶어요"
}
```

### 학습 목표 생성 — 기말고사 영어 (단기 집중)

```json
POST /api/goals
{
  "attachmentIds": [],
  "title": "중1 1학기 기말 영어 벼락치기",
  "purpose": "영어 70점 이상 맞아서 엄마한테 혼나지 않기",
  "targetScore": 70,
  "maxScore": 100,
  "startDate": "2026-07-14",
  "endDate": "2026-07-18",
  "methods": ["단어 암기", "핵심 문법 정리"],
  "byDayStudyTimes": [
    { "dayOfWeek": 1, "availableMinutes": 40 },
    { "dayOfWeek": 2, "availableMinutes": 40 },
    { "dayOfWeek": 3, "availableMinutes": 40 },
    { "dayOfWeek": 4, "availableMinutes": 40 },
    { "dayOfWeek": 5, "availableMinutes": 60 }
  ],
  "excludeDates": []
}
```

---

## 페르소나 3 — 박재원 (중3, 고입 준비형)

**배경**
- 중학교 3학년, 부산 ○○중학교
- 특수목적고/자율고 진학 목표
- 수학·과학 집중 공부, 학원 3개 병행
- 학원 없는 수요일·주말만 자습 가능

### 회원가입

```json
POST /api/auth/signup
{
  "email": "jaewon.park.test@example.com",
  "password": "Test1234!",
  "nickname": "재원"
}
```

### 프로필 업데이트

```json
PATCH /api/users/me
{
  "nickname": "재원",
  "ageGroup": "중등",
  "schoolName": "부산○○중학교"
}
```

### 나의 학습 설정

```json
POST /api/my-learning
{
  "preferredStudyTime": "수요일 저녁 + 주말 오전",
  "offDays": "없음",
  "preferredMaterialFormat": "교재 + 인강 병행",
  "preferredClassStyle": "심화 개념 + 고난도 문제",
  "preferredStudyMethod": "단계별 심화 문제 풀이",
  "preferredTeacherStyle": "엄격하지만 실력 있는 선생님",
  "preferredFriendStyle": "경쟁하면서 함께 성장",
  "extraNotes": "수학 올림피아드 준비도 병행하고 싶어요. 평일 학원 후 자습 시간이 거의 없어요."
}
```

### 학습 목표 생성 — 3학년 2학기 수학 (장기)

```json
POST /api/goals
{
  "attachmentIds": [],
  "title": "중3 2학기 수학 심화 - 고입 대비",
  "purpose": "수학 전 범위 완성해서 자사고 입시 대비",
  "targetScore": 95,
  "maxScore": 100,
  "startDate": "2026-07-07",
  "endDate": "2026-11-30",
  "methods": ["개념 완성", "심화 문제 풀이", "오답 분석", "모의고사 풀기"],
  "byDayStudyTimes": [
    { "dayOfWeek": 3, "availableMinutes": 120 },
    { "dayOfWeek": 6, "availableMinutes": 180 },
    { "dayOfWeek": 7, "availableMinutes": 180 }
  ],
  "excludeDates": [
    "2026-08-15",
    "2026-09-16",
    "2026-09-17",
    "2026-09-18",
    "2026-10-03",
    "2026-10-09"
  ]
}
```

---

## 페르소나별 비교 요약

| 항목 | 김민준 (우등생) | 이소연 (균형형) | 박재원 (고입준비) |
|------|---------------|----------------|-----------------|
| 학년 | 중2 | 중1 | 중3 |
| 목표 점수 | 90점 | 70점 | 95점 |
| 일 공부 가능 시간 | 60~120분 | 40~60분 | 120~180분 (주 3일) |
| 공부 기간 | 단기 (2주) | 단기 (5일) | 장기 (5개월) |
| 선호 방법 | 반복 풀이 | 영상 + 요약 | 심화 문제 |
| 쉬는 날 | 일요일 | 주말 전체 | 없음 |
| AI 플랜 기대 | 단계별 계획 | 짧게 쪼개진 계획 | 장기 로드맵 |

---

## 첨부 자료 예시 (Attachment)

각 페르소나가 자료를 연결할 때 사용 가능한 예시

### 텍스트 자료 등록

```json
POST /api/attachments/text
{
  "content": "이차방정식 근의 공식: x = (-b ± √(b²-4ac)) / 2a\n판별식 D = b²-4ac\nD>0 서로 다른 두 실근, D=0 중근, D<0 허근"
}
```

### 웹 URL 자료 등록

```json
POST /api/attachments/url
{
  "url": "https://www.youtube.com/watch?v=example",
  "type": "VIDEO"
}
```
