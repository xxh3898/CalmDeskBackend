# 더미 데이터 확인 및 테스트 가이드

## 1단계: 데이터베이스에서 직접 확인

### MySQL Workbench에서 확인

```sql
-- 모든 테이블의 데이터 확인
SELECT * FROM COMPANY;
SELECT * FROM DEPARTMENT;
SELECT * FROM MEMBER_RANK;
SELECT * FROM MEMBER;
```

### 확인할 내용:
- ✅ COMPANY 테이블에 '테스트 회사'가 있는지
- ✅ DEPARTMENT 테이블에 '개발팀'이 있는지
- ✅ MEMBER_RANK 테이블에 '사원'이 있는지
- ✅ MEMBER 테이블에 '홍길동' 회원이 있는지
- ✅ member_id가 1인지 확인 (API 테스트에 필요)

---

## 2단계: Spring Boot 애플리케이션 실행

### IntelliJ에서 실행:
1. `DemoApplication.java` 파일 열기
2. 상단의 초록색 재생 버튼 클릭
3. 콘솔에서 "Started DemoApplication" 메시지 확인

### 확인할 내용:
- ✅ 애플리케이션이 정상적으로 시작되는가?
- ✅ 데이터베이스 연결 에러가 없는가?
- ✅ 포트 8008에서 실행 중인가?

---

## 3단계: API 엔드포인트 테스트

### 방법 1: 브라우저에서 테스트

```
http://localhost:8008/api/mypage/profile?memberId=1
```

**예상 응답 (성공):**
```json
{
  "success": true,
  "message": "프로필 조회 성공",
  "data": {
    "memberId": 1,
    "name": "홍길동",
    "email": "test@example.com",
    "phone": "010-1234-5678",
    "department": "개발팀",
    "position": "사원",
    "joinDate": "2026.01.XX",
    "currentPoint": 800
  }
}
```

**예상 응답 (실패 - 회원 없음):**
```json
{
  "success": false,
  "message": "회원을 찾을 수 없습니다.",
  "data": null
}
```

### 방법 2: IntelliJ HTTP Client 사용

`finalprojectback/src/test/http/mypage-api.http` 파일 생성 (아래 참고)

### 방법 3: Postman 사용
- GET 요청: `http://localhost:8008/api/mypage/profile?memberId=1`

---

## 4단계: 모든 MyPage API 테스트

### 테스트할 API 목록:

1. **프로필 조회**
   ```
   GET http://localhost:8008/api/mypage/profile?memberId=1
   ```

2. **프로필 수정**
   ```
   PUT http://localhost:8008/api/mypage/profile?memberId=1
   Content-Type: application/json
   
   {
     "email": "newemail@example.com",
     "phone": "010-9999-8888"
   }
   ```

3. **비밀번호 변경**
   ```
   PUT http://localhost:8008/api/mypage/password?memberId=1
   Content-Type: application/json
   
   {
     "currentPassword": "password123",
     "newPassword": "newpassword123"
   }
   ```

4. **포인트 내역 조회**
   ```
   GET http://localhost:8008/api/mypage/points?memberId=1
   ```

5. **기프티콘 목록 조회**
   ```
   GET http://localhost:8008/api/mypage/coupons?memberId=1
   ```

6. **알림 목록 조회**
   ```
   GET http://localhost:8008/api/mypage/notifications?memberId=1
   ```

---

## 5단계: 에러 확인 및 디버깅

### 일반적인 에러:

1. **"회원을 찾을 수 없습니다"**
   - 해결: member_id가 1인지 확인
   - SQL: `SELECT member_id FROM MEMBER WHERE name = '홍길동';`

2. **"데이터베이스 연결 실패"**
   - 해결: MySQL 서버가 실행 중인지 확인
   - 해결: application.yaml의 설정 확인

3. **"NullPointerException"**
   - 해결: Department나 Rank가 제대로 연결되었는지 확인
   - SQL: `SELECT * FROM MEMBER WHERE member_id = 1;`

---

## 6단계: 프론트엔드 연동 테스트 (선택)

프론트엔드에서 실제 API를 호출하도록 연결:
- `mypageApi.getProfile(1)` 호출
- 응답 데이터를 화면에 표시
