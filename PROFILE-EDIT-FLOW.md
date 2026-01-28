# 마이페이지 전체 기능 흐름

## 📍 라우팅 경로

```
/app/mypage
  ↓
MyPage.jsx (라우터)
  ├─ / (MyPageMain) - 마이페이지 메인
  ├─ /profile (ProfileEditView) - 프로필 수정
  ├─ /coupons (CouponWalletView) - 기프티콘 보관함
  └─ /points (PointHistoryView) - 포인트 내역
```

---

## 📋 마이페이지 API 목록

| 기능 | HTTP 메서드 | 엔드포인트 | 설명 |
|------|------------|-----------|------|
| 프로필 조회 | GET | `/api/mypage/profile?memberId={id}` | 회원 프로필 정보 조회 |
| 프로필 수정 | PUT | `/api/mypage/profile?memberId={id}` | 이메일, 전화번호 수정 |
| 비밀번호 변경 | PUT | `/api/mypage/password?memberId={id}` | 비밀번호 변경 |
| 포인트 내역 조회 | GET | `/api/mypage/points?memberId={id}` | 포인트 적립/사용 내역 조회 |
| 기프티콘 목록 조회 | GET | `/api/mypage/coupons?memberId={id}` | 보유한 기프티콘 목록 조회 |

---

## 🔄 프로필 조회 흐름

### 1단계: 화면 진입 (프론트엔드)

**파일:** `finalprojectfront/src/pages/employee/MyPage/MyPageMain.jsx` 또는 `ProfileEditView.jsx`

**코드 위치:**
- `MyPageMain.jsx`: **36-59행** `useEffect` - 컴포넌트 마운트 시 실행
- `ProfileEditView.jsx`: **36-59행** `useEffect` - 프로필 수정 화면 진입 시 실행
- `getMemberId()` - `useStore().user.id` → `memberId` 추출 (없으면 1)

**동작:**
```javascript
// API 호출
const res = await mypageApi.getProfile(memberId);
// → GET /api/mypage/profile?memberId=1

// 응답 처리
if (res.success && res.data) {
  setEmail(res.data.email || '');
  setPhone(res.data.phone || '');
  // 또는 프로필 정보 표시
}
```

**API 호출:**
- **파일:** `finalprojectfront/src/api/mypageApi.js`
- **5-9행:** `getProfile(memberId)` → `apiClient.get('/mypage/profile', { params: { memberId } })`

**HTTP 요청:**
- **파일:** `finalprojectfront/src/api/axios.js`
- **baseURL:** `http://localhost:8008/api`
- **요청 인터셉터:** `localStorage.getItem('token')` → `Authorization: Bearer ${token}` 추가
- **최종 URL:** `GET http://localhost:8008/api/mypage/profile?memberId=1`

---

### 2단계: 백엔드 Controller 수신

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/controller/employee/MyPageController.java`

**코드 위치:**
- **22-31행:** `getProfile(@RequestParam Long memberId)`
- **25행:** `myPageService.getProfile(memberId)` 호출
- **26행:** 성공 시 `ApiResponse.success("프로필 조회 성공", response)` 반환

**응답 형식:**
```json
{
  "success": true,
  "message": "프로필 조회 성공",
  "data": {
    "memberId": 1,
    "name": "홍길동",
    "email": "user1@test.com",
    "phone": "010-1111-2222",
    "companyName": "칼름데스크",
    "department": "개발팀",
    "position": "사원",
    "joinDate": "2026.01.20",
    "currentPoint": 35000
  }
}
```

---

### 3단계: Service 비즈니스 로직

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/service/employee/MyPageServiceImpl.java`

**코드 위치:**
- **27-31행:** `getProfile(Long memberId)`
- **28행:** `memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)` - 회원 + 회사·부서·직급 한 번에 조회
- **30행:** `ProfileResponse.from(member)` - Entity → DTO 변환

---

### 4단계: Repository DB 조회

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/member/repository/MemberRepository.java`

**코드 위치:**
- **18-23행:** `findByIdWithCompanyAndDepartmentAndRank(@Param("memberId") Long memberId)`
- **JPQL:** `SELECT m FROM Member m LEFT JOIN FETCH m.company LEFT JOIN FETCH m.department LEFT JOIN FETCH m.rank WHERE m.memberId = :memberId`

**실제 SQL (Hibernate 변환):**
```sql
SELECT m.*, c.*, d.*, r.*
FROM MEMBER m
LEFT JOIN COMPANY c ON m.company_id = c.company_id
LEFT JOIN DEPARTMENT d ON m.department_id = d.department_id
LEFT JOIN MEMBER_RANK r ON m.rank_id = r.rank_id
WHERE m.member_id = 1
```

---

### 5단계: Entity → DTO 변환

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/dto/ProfileResponse.java`

**코드 위치:**
- `ProfileResponse.from(Member member)` - 정적 메서드
- `totalEarned - totalSpent` → `currentPoint` 계산
- `hireDate` → `yyyy.MM.dd` 포맷
- `company`, `department`, `rank` → 문자열 추출

---

## 🔄 프로필 수정 흐름 (저장하기 클릭)

### 1단계: 프론트엔드 - 저장 버튼 클릭

**파일:** `finalprojectfront/src/pages/employee/MyPage/ProfileEditView.jsx`

**코드 위치:**
- **저장 버튼:** `<S.SaveButton onClick={handleSave}>`
- **70-120행:** `handleSave()` 함수

**검증 로직:**
```javascript
// 연락처 필수 체크
if (!trimmedPhone) {
  setError('연락처를 입력하세요.');
  return;
}

// 비밀번호 변경 시 검증
const wantChangePassword = currentPassword || newPassword || newPasswordConfirm;
if (wantChangePassword) {
  // 현재/새/확인 모두 필수, 새=확인 일치 체크
}
```

---

### 2단계: 프로필 수정 API 호출

**코드 위치:**
- **94-97행:** `mypageApi.updateProfile(memberId, { email, phone })`

**API 함수:**
- **파일:** `finalprojectfront/src/api/mypageApi.js`
- **13-18행:** `updateProfile(memberId, data)` → `apiClient.put('/mypage/profile', data, { params: { memberId } })`

**HTTP 요청:**
- **최종 URL:** `PUT http://localhost:8008/api/mypage/profile?memberId=1`
- **Body:**
```json
{
  "email": "newemail@test.com",
  "phone": "010-9999-8888"
}
```

---

### 3단계: 백엔드 Controller 수신

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/controller/employee/MyPageController.java`

**코드 위치:**
- **34-45행:** `updateProfile(@RequestParam Long memberId, @Valid @RequestBody ProfileUpdateRequest request)`
- **37행:** `@Valid` - DTO 검증 (`@Email`, `@NotBlank`)
- **39행:** `myPageService.updateProfile(memberId, request)` 호출

---

### 4단계: Service 비즈니스 로직

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/service/employee/MyPageServiceImpl.java`

**코드 위치:**
- **33-55행:** `updateProfile(Long memberId, ProfileUpdateRequest request)`
- **@Transactional** - 트랜잭션 시작

**단계별 동작:**

1. **36행:** 회원 조회
   ```java
   Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)
           .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
   ```

2. **39-44행:** 이메일 변경 검증
   ```java
   if (request.getEmail() != null && !request.getEmail().equals(member.getEmail())) {
       if (memberRepository.existsByEmail(request.getEmail())) {
           throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
       }
       member.setEmail(request.getEmail());
   }
   ```

3. **46-51행:** 연락처 변경 검증
   ```java
   if (request.getPhone() != null && !request.getPhone().equals(member.getPhone())) {
       if (memberRepository.existsByPhone(request.getPhone())) {
           throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
       }
       member.setPhone(request.getPhone());
   }
   ```

4. **53행:** 저장
   ```java
   memberRepository.save(member);
   ```

5. **54행:** 응답 DTO 생성
   ```java
   return ProfileResponse.from(member);
   ```

---

### 5단계: Repository 중복 검사

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/member/repository/MemberRepository.java`

**코드 위치:**
- **14행:** `existsByEmail(String email)` - 이메일 중복 체크
- **16행:** `existsByPhone(String phone)` - 전화번호 중복 체크

**JPA 메서드명 규칙:**
- `existsByEmail` → `SELECT COUNT(*) > 0 FROM MEMBER WHERE email = ?`
- `existsByPhone` → `SELECT COUNT(*) > 0 FROM MEMBER WHERE phone = ?`

---

### 6단계: DB 저장

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/member/entity/Member.java`

**엔티티:**
- **33행:** `@Column(nullable = false, length = 50)` `private String email;`
- **36행:** `@Column(nullable = false, unique = true, length = 30)` `private String phone;`

**저장:**
- `memberRepository.save(member)` → JPA가 `UPDATE MEMBER SET email=?, phone=? WHERE member_id=?` 실행

---

### 7단계: 응답 반환

**Controller → Service → DTO 변환 → JSON 응답**

**응답 형식:**
```json
{
  "success": true,
  "message": "프로필 수정 성공",
  "data": {
    "memberId": 1,
    "name": "홍길동",
    "email": "newemail@test.com",
    "phone": "010-9999-8888",
    ...
  }
}
```

---

### 8단계: 프론트엔드 응답 처리

**파일:** `finalprojectfront/src/pages/employee/MyPage/ProfileEditView.jsx`

**코드 위치:**
- **98-102행:** 프로필 수정 응답 처리
  ```javascript
  if (!res.success) {
    setError(res.message || '프로필 수정에 실패했습니다.');
    setSaving(false);
    return;
  }
  ```

- **103-113행:** 비밀번호 변경 (선택)
  ```javascript
  if (wantChangePassword) {
    const pwRes = await mypageApi.changePassword(memberId, {
      currentPassword: currentPassword.trim(),
      newPassword: newPassword.trim()
    });
    // ...
  }
  ```

- **114행:** 성공 시 마이페이지로 이동
  ```javascript
  navigate('/app/mypage');
  ```

---

## 🔐 비밀번호 변경 흐름 (선택)

### 1단계: 프론트엔드 API 호출

**파일:** `finalprojectfront/src/pages/employee/MyPage/ProfileEditView.jsx`
- **104-107행:** `mypageApi.changePassword(memberId, { currentPassword, newPassword })`

**API 함수:**
- **파일:** `finalprojectfront/src/api/mypageApi.js`
- **21-26행:** `changePassword(memberId, data)` → `PUT /api/mypage/password?memberId=1`

**Body:**
```json
{
  "currentPassword": "password123",
  "newPassword": "newpassword123"
}
```

---

### 2단계: 백엔드 Controller

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/controller/employee/MyPageController.java`
- **48-59행:** `changePassword(@RequestParam Long memberId, @Valid @RequestBody PasswordChangeRequest request)`

---

### 3단계: Service 비즈니스 로직

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/service/employee/MyPageServiceImpl.java`
- **57-69행:** `changePassword(Long memberId, PasswordChangeRequest request)`

**동작:**
1. **60행:** 회원 조회 (`findById`)
2. **63행:** 현재 비밀번호 비교 (`member.getPassword().equals(request.getCurrentPassword())`)
3. **67행:** 새 비밀번호 설정 (`member.setPassword(request.getNewPassword())`)
4. **68행:** 저장 (`memberRepository.save(member)`)

---

## 💰 포인트 내역 조회 흐름

### 1단계: 프론트엔드 API 호출

**파일:** `finalprojectfront/src/pages/employee/MyPage/PointHistoryView.jsx`

**API 함수:**
- **파일:** `finalprojectfront/src/api/mypageApi.js`
- **29-34행:** `getPointHistory(memberId)` → `GET /api/mypage/points?memberId=1`

---

### 2단계: 백엔드 Controller

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/controller/employee/MyPageController.java`
- **61-71행:** `getPointHistory(@RequestParam Long memberId)`

---

### 3단계: Service 비즈니스 로직

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/service/employee/MyPageServiceImpl.java`
- **71-80행:** `getPointHistory(Long memberId)`

**동작:**
1. **73행:** 회원 조회 (`findById`)
2. **76행:** 포인트 내역 조회 (`pointHistoryRepository.findByMemberIdOrderByCreateDateDesc(member.getId())`)
3. **77-79행:** `PointHistoryResponse.from()` 변환 후 리스트 반환

---

## 🎫 기프티콘 목록 조회 흐름

### 1단계: 프론트엔드 API 호출

**파일:** `finalprojectfront/src/pages/employee/MyPage/MyPageMain.jsx` 또는 `CouponWalletView.jsx`

**API 함수:**
- **파일:** `finalprojectfront/src/api/mypageApi.js`
- **37-42행:** `getCoupons(memberId)` → `GET /api/mypage/coupons?memberId=1`

---

### 2단계: 백엔드 Controller

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/controller/employee/MyPageController.java`
- **73-83행:** `getCoupons(@RequestParam Long memberId)`

---

### 3단계: Service 비즈니스 로직

**파일:** `finalprojectback/src/main/java/com/code808/calmdesk/domain/mypage/service/employee/MyPageServiceImpl.java`
- **82-91행:** `getCoupons(Long memberId)`

**동작:**
1. **84행:** 회원 조회 (`findById`)
2. **87행:** 주문 목록 조회 (`orderRepository.findByMemberOrderByOrderDateDesc(member)`)
3. **88-90행:** `CouponResponse.from(order, order.getGifticon())` 변환 후 리스트 반환

**CouponResponse 포함 필드:**
- `orderId`, `gifticonId`, `gifticonName`, `shop`, `image` (URL 또는 이모지), `price`, `expiryDate`, `status` (AVAILABLE/USED)

---

## 📁 관련 파일 목록

### 프론트엔드

| 파일 | 경로 | 역할 |
|------|------|------|
| **MyPage.jsx** | `finalprojectfront/src/pages/employee/MyPage/MyPage.jsx` | 라우팅: `/`, `/profile`, `/coupons`, `/points` |
| **MyPageMain.jsx** | `finalprojectfront/src/pages/employee/MyPage/MyPageMain.jsx` | 마이페이지 메인 화면 (프로필, 기프티콘 미리보기) |
| **ProfileEditView.jsx** | `finalprojectfront/src/pages/employee/MyPage/ProfileEditView.jsx` | 프로필 수정 화면, `handleSave`, `getProfile` 호출 |
| **CouponWalletView.jsx** | `finalprojectfront/src/pages/employee/MyPage/CouponWalletView.jsx` | 기프티콘 보관함 전체보기 |
| **PointHistoryView.jsx** | `finalprojectfront/src/pages/employee/MyPage/PointHistoryView.jsx` | 포인트 내역 조회 화면 |
| **mypageApi.js** | `finalprojectfront/src/api/mypageApi.js` | `getProfile`, `updateProfile`, `changePassword`, `getPointHistory`, `getCoupons` API 함수 |
| **axios.js** | `finalprojectfront/src/api/axios.js` | `baseURL`, 요청/응답 인터셉터 (토큰, 401 처리) |
| **useStore.js** | `finalprojectfront/src/store/useStore.js` | Zustand 스토어, `user.id` 추출 |

### 백엔드

| 파일 | 경로 | 역할 |
|------|------|------|
| **MyPageController** | `domain/mypage/controller/employee/MyPageController.java` | `GET /api/mypage/profile`, `PUT /api/mypage/profile`, `PUT /api/mypage/password`, `GET /api/mypage/points`, `GET /api/mypage/coupons` |
| **MyPageService** | `domain/mypage/service/employee/MyPageService.java` | 인터페이스 |
| **MyPageServiceImpl** | `domain/mypage/service/employee/MyPageServiceImpl.java` | `getProfile`, `updateProfile`, `changePassword`, `getPointHistory`, `getCoupons` 구현 |
| **ProfileResponse** | `domain/mypage/dto/ProfileResponse.java` | 프로필 조회/수정 응답 DTO |
| **ProfileUpdateRequest** | `domain/mypage/dto/ProfileUpdateRequest.java` | 프로필 수정 요청 DTO (`@Email`, `@NotBlank`) |
| **PasswordChangeRequest** | `domain/mypage/dto/PasswordChangeRequest.java` | 비밀번호 변경 요청 DTO |
| **PointHistoryResponse** | `domain/mypage/dto/PointHistoryResponse.java` | 포인트 내역 응답 DTO |
| **CouponResponse** | `domain/mypage/dto/CouponResponse.java` | 기프티콘 목록 응답 DTO (`image` 필드 포함) |
| **MemberRepository** | `domain/member/repository/MemberRepository.java` | `findByIdWithCompanyAndDepartmentAndRank`, `existsByEmail`, `existsByPhone` |
| **PointHistoryRepository** | `domain/gifticon/repository/PointHistoryRepository.java` | `findByMemberIdOrderByCreateDateDesc` |
| **OrderRepository** | `domain/gifticon/repository/OrderRepository.java` | `findByMemberOrderByOrderDateDesc` |
| **Member** | `domain/member/entity/Member.java` | 회원 엔티티 (`email`, `phone`, `password`) |
| **ApiResponse** | `global/dto/ApiResponse.java` | 공통 응답 래퍼 (`{ success, message, data }`) |

---

## 🔗 데이터 흐름 요약

```
[화면] ProfileEditView / MyPageMain
   ↓ getMemberId() → useStore().user.id (없으면 1)
   ↓ mypageApi.getProfile(memberId)
[HTTP] GET /api/mypage/profile?memberId=1
   ↓
[Controller] MyPageController.getProfile(1)
   ↓
[Service] MyPageServiceImpl.getProfile(1)
   ↓
[Repository] MemberRepository.findByIdWithCompanyAndDepartmentAndRank(1)
   ↓
[DB] SELECT ... FROM MEMBER ... WHERE member_id = 1
   ↓
[Entity] Member + Company, Department, Rank
   ↓
[DTO] ProfileResponse.from(member)
   ↓
[응답] ApiResponse { success: true, data: ProfileResponse }
   ↓
[프론트] setEmail(res.data.email), setPhone(res.data.phone)
   ↓ 폼에 표시
   ↓
[저장 클릭] handleSave()
   ↓ mypageApi.updateProfile(memberId, { email, phone })
[HTTP] PUT /api/mypage/profile?memberId=1, body: { email, phone }
   ↓
[Controller] MyPageController.updateProfile(1, ProfileUpdateRequest)
   ↓ @Valid 검증
[Service] MyPageServiceImpl.updateProfile(1, request)
   ↓ findByIdWithCompanyAndDepartmentAndRank(1)
   ↓ existsByEmail / existsByPhone 중복 체크
   ↓ member.setEmail / setPhone
   ↓ memberRepository.save(member)
   ↓ ProfileResponse.from(member)
[응답] ApiResponse { success: true, data: ProfileResponse }
   ↓
[프론트] res.success → (비밀번호 변경 있으면) changePassword → navigate('/app/mypage')
```

---

## 🎯 핵심 포인트

1. **memberId 추출:** `useStore().user.id` → 없으면 기본값 1
2. **프로필 조회:** 화면 로드 시 `useEffect`에서 자동 호출
3. **검증:** 프론트(연락처 필수, 비밀번호 일치) + 백엔드(`@Valid`, 중복 체크)
4. **트랜잭션:** `@Transactional`로 이메일/연락처 변경이 하나의 트랜잭션
5. **비밀번호 변경:** 선택적 - 입력이 있으면 `updateProfile` 성공 후 `changePassword` 호출
6. **에러 처리:** `IllegalArgumentException` → Controller에서 `ApiResponse.error()` → 프론트에서 `setError()` 표시
7. **기프티콘 이미지:** `CouponResponse.image` 필드 - URL이면 `<img>`, 이모지면 텍스트로 표시
8. **포인트 내역:** `PointHistoryRepository.findByMemberIdOrderByCreateDateDesc()` - 최신순 정렬
