# 백엔드 데이터 흐름 가이드

## 📚 전체 흐름 개요

```
HTTP 요청 (프론트엔드)
    ↓
Controller (API 엔드포인트)
    ↓
Service (비즈니스 로직)
    ↓
Repository (데이터베이스 접근)
    ↓
Entity (데이터베이스 테이블)
    ↓
MySQL 데이터베이스
    ↓
Entity → DTO 변환
    ↓
JSON 응답 (프론트엔드)
```

---

## 🔍 실제 예시: 프로필 조회 API

### 1단계: HTTP 요청 받기 (Controller)

**파일:** `MyPageController.java`

```java
@GetMapping("/profile")
public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@RequestParam Long memberId) {
    try {
        ProfileResponse response = myPageService.getProfile(memberId);
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", response));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }
}
```

**설명:**
- `@GetMapping("/profile")`: `GET /api/mypage/profile?memberId=1` 요청을 받음
- `@RequestParam Long memberId`: URL 파라미터에서 `memberId` 추출 (예: `?memberId=1`)
- `myPageService.getProfile(memberId)`: Service 계층 호출
- `ResponseEntity`: HTTP 응답 반환 (200 OK 또는 400 Bad Request)

---

### 2단계: 비즈니스 로직 처리 (Service)

**파일:** `MyPageServiceImpl.java`

```java
@Override
public ProfileResponse getProfile(Long memberId) {
    // Repository를 통해 DB에서 회원 조회
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    
    // Entity를 DTO로 변환
    return ProfileResponse.from(member);
}
```

**설명:**
- `memberRepository.findById(memberId)`: Repository 호출
- `orElseThrow()`: 회원이 없으면 예외 발생
- `ProfileResponse.from(member)`: Entity → DTO 변환

---

### 3단계: 데이터베이스 접근 (Repository)

**파일:** `MemberRepository.java` (인터페이스)

```java
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}
```

**설명:**
- `JpaRepository<Member, Long>`: Spring Data JPA가 자동으로 제공하는 메서드
  - `findById(id)`: ID로 조회
  - `save(entity)`: 저장/수정
  - `deleteById(id)`: 삭제

**실제 구현:** `MemberRepositoryImpl.java`

```java
// JPA가 자동으로 SQL 생성
// SELECT * FROM MEMBER WHERE member_id = 1
Member member = memberRepository.findById(1);
```

---

### 4단계: Entity → DTO 변환

**파일:** `ProfileResponse.java`

```java
public static ProfileResponse from(Member member) {
    // 포인트 계산
    int currentPoint = member.getTotalEarnedPoint() - member.getTotalSpentPoint();
    
    return ProfileResponse.builder()
            .memberId(member.getMemberId())
            .name(member.getName())
            .email(member.getEmail())
            .phone(member.getPhone())
            .department(member.getDepartment().getDepartmentName())  // 관계 데이터 접근
            .position(member.getRank().getRankName())                 // 관계 데이터 접근
            .joinDate(member.getCreatedDate().format(...))            // 날짜 포맷팅
            .currentPoint(currentPoint)
            .build();
}
```

**설명:**
- Entity (Member)의 모든 데이터를 DTO (ProfileResponse)로 변환
- 관계 데이터 접근: `member.getDepartment().getDepartmentName()`
- 계산 로직: 포인트 계산
- 포맷팅: 날짜 형식 변환

---

## 🔄 전체 데이터 흐름 상세

### 예시: `GET /api/mypage/profile?memberId=1`

#### 1. HTTP 요청 도착
```
GET http://localhost:8008/api/mypage/profile?memberId=1
```

#### 2. Controller에서 처리
```java
// MyPageController.getProfile(1) 호출
ProfileResponse response = myPageService.getProfile(1);
```

#### 3. Service에서 비즈니스 로직
```java
// MyPageServiceImpl.getProfile(1)
Member member = memberRepository.findById(1);
// → Repository 호출
```

#### 4. Repository에서 DB 조회
```java
// Spring이 자동으로 SQL 생성
SELECT * FROM MEMBER WHERE member_id = 1
```

#### 5. 데이터베이스에서 결과 반환
```
member_id: 1
name: "홍길동"
email: "test@example.com"
...
```

#### 6. Entity 객체 생성
```java
Member member = {
    memberId: 1,
    name: "홍길동",
    email: "test@example.com",
    department: Department { departmentName: "개발팀" },
    rank: Rank { rankName: "사원" }
}
```

#### 7. DTO로 변환
```java
ProfileResponse.from(member)
// → ProfileResponse 객체 생성
```

#### 8. JSON 응답 반환
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
    "joinDate": "2026.01.20",
    "currentPoint": 800
  }
}
```

---

## 🗂️ 각 계층의 역할

### Controller 계층
- **역할**: HTTP 요청/응답 처리
- **책임**:
  - URL 매핑 (`@GetMapping`, `@PostMapping` 등)
  - 파라미터 추출 (`@RequestParam`, `@RequestBody`)
  - 응답 생성 (`ResponseEntity`)
  - 예외 처리

### Service 계층
- **역할**: 비즈니스 로직 처리
- **책임**:
  - 데이터 검증
  - 비즈니스 규칙 적용
  - Repository 호출
  - Entity → DTO 변환
  - 트랜잭션 관리 (`@Transactional`)

### Repository 계층
- **역할**: 데이터베이스 접근
- **책임**:
  - SQL 쿼리 실행
  - Entity 조회/저장/삭제
  - 데이터베이스와의 통신

### Entity 계층
- **역할**: 데이터베이스 테이블 매핑
- **책임**:
  - 테이블 구조 정의
  - 관계 매핑 (`@ManyToOne`, `@OneToMany` 등)
  - 데이터 저장

### DTO 계층
- **역할**: API 요청/응답 데이터 구조
- **책임**:
  - 클라이언트와의 데이터 교환 형식 정의
  - Entity와 분리하여 보안 및 유연성 확보

---

## 🔗 관계 데이터 접근

### 예시: Member → Department → Company

```java
// Member Entity
@ManyToOne
@JoinColumn(name = "DEPARTMENT_ID")
private Department department;

// Service에서 접근
String deptName = member.getDepartment().getDepartmentName();
// → Lazy Loading: 실제로 접근할 때 SQL 실행
```

**실제 SQL 실행:**
```sql
-- 1. Member 조회
SELECT * FROM MEMBER WHERE member_id = 1;

-- 2. Department 조회 (Lazy Loading)
SELECT * FROM DEPARTMENT WHERE department_id = 1;

-- 3. Company 조회 (필요시)
SELECT * FROM COMPANY WHERE company_id = 1;
```

---

## 📊 데이터 변환 과정

### Entity (DB 테이블 구조)
```java
Member {
    memberId: Long
    name: String
    email: String
    department: Department (객체)
    rank: Rank (객체)
    totalEarnedPoint: Integer
    totalSpentPoint: Integer
}
```

### DTO (API 응답 구조)
```java
ProfileResponse {
    memberId: Long
    name: String
    email: String
    department: String        // 객체 → 문자열
    position: String          // 객체 → 문자열
    currentPoint: Integer     // 계산된 값
    joinDate: String          // 날짜 포맷팅
}
```

**변환 이유:**
- Entity는 내부 구조 (객체 관계)
- DTO는 외부 API 형식 (단순 데이터)
- 보안: 불필요한 정보 숨김
- 유연성: API 형식 변경 용이

---

## 🎯 핵심 개념 정리

### 1. 의존성 주입 (Dependency Injection)
```java
@Service
@RequiredArgsConstructor  // 생성자 자동 생성
public class MyPageServiceImpl {
    private final MemberRepository memberRepository;  // 자동 주입
}
```

### 2. 트랜잭션 관리
```java
@Transactional  // 데이터 변경 시 필요
public ProfileResponse updateProfile(...) {
    // 여러 DB 작업이 하나의 트랜잭션으로 실행
}
```

### 3. 예외 처리
```java
try {
    // 정상 처리
} catch (IllegalArgumentException e) {
    // 에러 응답
}
```

---

## 📝 실제 테스트 방법

### 1. 브라우저에서 테스트
```
http://localhost:8008/api/mypage/profile?memberId=1
```

### 2. 로그 확인
- 콘솔에 SQL 쿼리 출력 (`show-sql: true`)
- 각 단계별 로그 확인

### 3. 디버깅
- Controller에 브레이크포인트
- Service에 브레이크포인트
- Repository 호출 확인

---

## 🚀 다음 단계

1. **각 계층의 역할 이해** ✅
2. **데이터 흐름 추적** ✅
3. **실제 API 테스트** (브라우저/Postman)
4. **프론트엔드 연동** (나중에)

이제 백엔드 구조를 이해하셨으니, 실제로 API를 테스트해보시고, 궁금한 점이 있으면 언제든 물어보세요!
