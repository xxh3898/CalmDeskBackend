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
    // Repository를 통해 DB에서 회원 조회 (회사·부서·직급 함께 FETCH)
    Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // Entity를 DTO로 변환
    return ProfileResponse.from(member);
}
```

**설명:**
- `findByIdWithCompanyAndDepartmentAndRank(memberId)`: 회원 + Company, Department, Rank를 한 번에 조회 (N+1 방지)
- `orElseThrow()`: 회원이 없으면 예외 발생
- `ProfileResponse.from(member)`: Entity → DTO 변환

---

### 3단계: 데이터베이스 접근 (Repository)

**파일:** `MemberRepository.java` (인터페이스)

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT m FROM Member m " +
           "LEFT JOIN FETCH m.company " +
           "LEFT JOIN FETCH m.department " +
           "LEFT JOIN FETCH m.rank " +
           "WHERE m.memberId = :memberId")
    Optional<Member> findByIdWithCompanyAndDepartmentAndRank(@Param("memberId") Long memberId);
}
```

**설명:**
- `JpaRepository<Member, Long>`: Spring Data JPA 기본 메서드 (`findById`, `save`, `deleteById` 등)
- `@Query`: JPQL로 **회사·부서·직급을 JOIN FETCH**해 한 번에 로드 (Lazy 로딩 추가 쿼리 방지)
- `findByIdWithCompanyAndDepartmentAndRank`: 프로필 조회·수정 시 사용

---

### 4단계: Entity → DTO 변환

**파일:** `ProfileResponse.java`

```java
public static ProfileResponse from(Member member) {
    long totalEarned = member.getTotalEarned() != null ? member.getTotalEarned() : 0L;
    long totalSpent = member.getTotalSpent() != null ? member.getTotalSpent() : 0L;
    int currentPoint = (int) (totalEarned - totalSpent);

    String companyName = member.getCompany() != null && member.getCompany().getCompanyName() != null
            ? member.getCompany().getCompanyName() : "";
    String department = member.getDepartment() != null && member.getDepartment().getDepartmentName() != null
            ? member.getDepartment().getDepartmentName() : "";
    String position = member.getRank() != null && member.getRank().getRankName() != null
            ? member.getRank().getRankName() : "";

    return ProfileResponse.builder()
            .memberId(member.getId())
            .name(member.getName())
            .email(member.getEmail())
            .phone(member.getPhone())
            .companyName(companyName)
            .department(department)
            .position(position)
            .joinDate(member.getHireDate() != null
                    ? member.getHireDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) : "")
            .currentPoint(currentPoint)
            .build();
}
```

**설명:**
- **포인트**: `currentPoint = totalEarned - totalSpent`
- **관계 데이터**: `getCompany()`, `getDepartment()`, `getRank()` → null-safe로 문자열 추출
- **날짜**: `hireDate`를 `yyyy.MM.dd`로 포맷

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
Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(1);
// → 회원 + 회사·부서·직급 한 번에 조회
```

#### 4. Repository에서 DB 조회
```java
// @Query JPQL → Hibernate가 SQL로 변환
SELECT m.*, c.*, d.*, r.*
FROM MEMBER m
LEFT JOIN COMPANY c ON m.company_id = c.company_id
LEFT JOIN DEPARTMENT d ON m.department_id = d.department_id
LEFT JOIN MEMBER_RANK r ON m.rank_id = r.rank_id
WHERE m.member_id = 1
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
    phone: "010-1234-5678",
    company: Company { companyName: "칼름데스크" },
    department: Department { departmentName: "개발팀" },
    rank: Rank { rankName: "사원" },
    hireDate: LocalDate,
    totalEarned: 100000, totalSpent: 20000
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
    "companyName": "칼름데스크",
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

### 예시: Member → Company, Department, Rank

```java
// Member Entity
@ManyToOne @JoinColumn(name = "COMPANY_ID")    private Company company;
@ManyToOne @JoinColumn(name = "DEPARTMENT_ID") private Department department;
@ManyToOne @JoinColumn(name = "RANK_ID")       private Rank rank;

// Service에서 접근
String deptName = member.getDepartment().getDepartmentName();
// → Lazy일 때: 접근 시점에 추가 SQL (N+1 가능)
```

**프로필 조회에서의 처리:**
- `findByIdWithCompanyAndDepartmentAndRank`는 **JPQL `LEFT JOIN FETCH`**로 `company`, `department`, `rank`를 한 번에 조회.
- 따라서 `ProfileResponse.from(member)`에서 `getCompany()`, `getDepartment()`, `getRank()` 호출 시 **추가 쿼리 없음** (N+1 방지).

**일반 `findById`만 쓸 경우 (Lazy):**
```sql
SELECT * FROM MEMBER WHERE member_id = 1;           -- 1차
SELECT * FROM DEPARTMENT WHERE department_id = 1;   -- getDepartment() 시
SELECT * FROM COMPANY WHERE company_id = 1;         -- getCompany() 시
```

---

## 📊 데이터 변환 과정

### Entity (DB 테이블 구조)
```java
Member {
    memberId: Long
    name: String
    email: String
    password: String
    phone: String
    company: Company         // @ManyToOne
    department: Department   // @ManyToOne
    rank: Rank               // @ManyToOne
    hireDate: LocalDate
    totalEarned: Long
    totalSpent: Long
}
```

### DTO (API 응답 구조)
```java
ProfileResponse {
    memberId: Long
    name: String
    email: String
    phone: String
    companyName: String      // company.getCompanyName()
    department: String       // department.getDepartmentName()
    position: String         // rank.getRankName()
    joinDate: String         // hireDate 포맷 (yyyy.MM.dd)
    currentPoint: Integer    // totalEarned - totalSpent
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

### 1. HTTP 파일로 테스트 (IntelliJ / VS Code REST Client)
`src/test/http/mypage-api.http` 에 프로필 조회·수정·비밀번호 변경 예시가 있음.
```
GET http://localhost:8008/api/mypage/profile?memberId=1
PUT http://localhost:8008/api/mypage/profile?memberId=1
PUT http://localhost:8008/api/mypage/password?memberId=1
```

### 2. 브라우저에서 조회만
```
http://localhost:8008/api/mypage/profile?memberId=1
```

### 3. 로그 확인
- `application.yaml`의 `show-sql: true` 시 콘솔에 SQL 출력
- `findByIdWithCompanyAndDepartmentAndRank` 호출 시 JOIN FETCH 쿼리 확인

### 4. 디버깅
- `MyPageController`, `MyPageServiceImpl`에 브레이크포인트
- `memberId`, Request DTO, `ProfileResponse` 값 확인

---

## 📝 정보수정: 프로필 수정·비밀번호 변경 흐름

### 1. 프로필 수정 (이메일·연락처) `PUT /api/mypage/profile`

| 단계 | 파일 | 동작 |
|------|------|------|
| 1 | `MyPageController.updateProfile` | `memberId`(쿼리), `ProfileUpdateRequest`(body) 수신 |
| 2 | `MyPageServiceImpl.updateProfile` | `findByIdWithCompanyAndDepartmentAndRank`로 회원 조회 |
| 3 | | 이메일 변경 시 `existsByEmail` 검사 → 중복이면 예외 |
| 4 | | 연락처 변경 시 `existsByPhone` 검사 → 중복이면 예외 |
| 5 | | `member.setEmail`, `member.setPhone` 후 `memberRepository.save(member)` |
| 6 | | `ProfileResponse.from(member)`로 응답 DTO 생성 |
| 7 | `MyPageController` | `ApiResponse.success("프로필 수정 성공", data)` 반환 |

**Request DTO (`ProfileUpdateRequest`):**
- `email` (optional, `@Email`)
- `phone` (필수, `@NotBlank`)

---

### 2. 비밀번호 변경 `PUT /api/mypage/password`

| 단계 | 파일 | 동작 |
|------|------|------|
| 1 | `MyPageController.changePassword` | `memberId`(쿼리), `PasswordChangeRequest`(body) 수신 |
| 2 | `MyPageServiceImpl.changePassword` | `memberRepository.findById(memberId)`로 회원 조회 |
| 3 | | `member.getPassword().equals(request.getCurrentPassword())` → 일치하지 않으면 예외 |
| 4 | | `member.setPassword(request.getNewPassword())` 후 `memberRepository.save(member)` |
| 5 | `MyPageController` | `ApiResponse.success("비밀번호 변경 성공", null)` 반환 |

**Request DTO (`PasswordChangeRequest`):**
- `currentPassword` (필수, `@NotBlank`)
- `newPassword` (필수, `@NotBlank`)

> ⚠️ **참고:** 비밀번호가 DB에 BCrypt 등으로 해시되어 있다면, `PasswordEncoder.matches()`로 검증·`encode()`로 저장하도록 변경해야 함.

---

## 📋 정보수정 관련 API 요약

| 기능 | 메서드 | URL | 쿼리 | Body | 응답 `data` |
|------|--------|-----|------|------|--------------|
| 프로필 조회 | GET | `/api/mypage/profile` | `memberId` | - | `ProfileResponse` |
| 프로필 수정 | PUT | `/api/mypage/profile` | `memberId` | `{ email, phone }` | `ProfileResponse` |
| 비밀번호 변경 | PUT | `/api/mypage/password` | `memberId` | `{ currentPassword, newPassword }` | `null` |

---

## 🗂️ 정보수정 관련 파일 역할

| 파일 | 경로 | 역할 |
|------|------|------|
| **MyPageController** | `domain/mypage/controller/employee/` | `getProfile`, `updateProfile`, `changePassword` 엔드포인트. `memberId` 추출, 서비스 호출, `ApiResponse`로 래핑, `IllegalArgumentException` → 400. |
| **MyPageService** | `domain/mypage/service/employee/` | 인터페이스: `getProfile`, `updateProfile`, `changePassword` 시그니처. |
| **MyPageServiceImpl** | `domain/mypage/service/employee/` | 프로필 조회: `findByIdWithCompanyAndDepartmentAndRank` → `ProfileResponse.from`. 프로필 수정: 이메일/연락처 중복 검사, `setEmail`/`setPhone`, `save`. 비밀번호 변경: 현재 비밀번호 비교, `setPassword`, `save`. |
| **ProfileResponse** | `domain/mypage/dto/` | 프로필 조회·수정 응답. `from(Member)`: `totalEarned`-`totalSpent` → `currentPoint`, `hireDate` 포맷, `company`/`department`/`rank` → 문자열. |
| **ProfileUpdateRequest** | `domain/mypage/dto/` | 프로필 수정 요청: `email`(@Email), `phone`(@NotBlank). |
| **PasswordChangeRequest** | `domain/mypage/dto/` | 비밀번호 변경 요청: `currentPassword`, `newPassword`(@NotBlank). |
| **ApiResponse** | `global/dto/` | `{ success, message, data }`. `success(message, data)`, `error(message)`. |
| **MemberRepository** | `domain/member/repository/` | `findById`, `findByIdWithCompanyAndDepartmentAndRank`, `existsByEmail`, `existsByPhone`. |
| **Member** | `domain/member/entity/` | 회원 엔티티. `email`, `phone`, `password`, `company`, `department`, `rank`, `hireDate`, `totalEarned`, `totalSpent` 등. |
| **Company, Department, Rank** | `domain/member/entity/` | `Member`와 `@ManyToOne`. `getCompanyName()`, `getDepartmentName()`, `getRankName()`으로 `ProfileResponse`에 사용. |
| **SecurityConfig** | `global/security/` | `/api/**` `permitAll`, CORS, CSRF 비활성. formLogin/httpBasic 비활성. |

---

## 🔐 ApiResponse·Request DTO

### ApiResponse (공통 응답 래퍼)

```java
// 성공
ApiResponse.success("프로필 조회 성공", profileResponse);
// → { "success": true, "message": "프로필 조회 성공", "data": { ... } }

// 실패 (Controller catch에서 사용)
ApiResponse.error("회원을 찾을 수 없습니다.");
// → { "success": false, "message": "회원을 찾을 수 없습니다.", "data": null }
```

### 요청 DTO·검증

- **ProfileUpdateRequest**: `@Valid`로 `@Email`, `@NotBlank(phone)` 검사. Controller에서 `@Valid @RequestBody` 사용.
- **PasswordChangeRequest**: `@Valid`로 `@NotBlank(currentPassword, newPassword)` 검사.

---

---

## 🌱 더미 데이터 (회사·부서·직급)

### MySQL 직접 vs 백엔드 data.sql

| 구분 | MySQL 직접 입력 | 백엔드 `data.sql` |
|------|-----------------|--------------------|
| **장점** | DB만 있어도 즉시 입력 가능 | 앱 기동만으로 자동 삽입, 팀원/재설치 시 동일, 버전 관리·배포에 포함 가능 |
| **단점** | 팀원마다 수동 반복, 스키마 변경 시 SQL 다시 작성 | `spring.sql.init` 설정 필요, DB 컬럼명이 다르면 data.sql 수정 필요 |
| **권장** | DB만 따로 쓸 때, 1회성 수동 보정 | **개발/로컬 더미** → `data.sql` 사용 권장 |

### 적용된 방식: `data.sql`

- **위치:** `src/main/resources/data.sql`
- **내용:** `COMPANY`, `DEPARTMENT`, `MEMBER_RANK`에 `INSERT IGNORE` (이미 있으면 스킵 → 재기동 시 중복 에러 방지)
- **실행 조건:** `application.yaml`의 `spring.sql.init.mode: always` (MySQL은 기본 `never`라 `always`로 설정)
- **실행 시점:** Hibernate `ddl-auto: update`로 테이블 생성/수정 후, `data.sql` 실행

### 컬럼/테이블명 참고

- `ddl-auto: update`로 Hibernate가 만든 컬럼명은 보통 **소문자 snake_case** (예: `company_id`, `company_name`, `min_value`).
- `Unknown column` 이 나오면 실제 DB 컬럼명을 확인한 뒤 `data.sql`을 맞추면 됨.

---

## 🚀 다음 단계

1. **각 계층의 역할 이해** ✅
2. **데이터 흐름 추적** ✅
3. **정보수정(프로필·비밀번호) 흐름** ✅
4. **더미 데이터** ✅ (`data.sql`로 회사·부서·직급)
5. **실제 API 테스트** (브라우저/Postman, `mypage-api.http`)
6. **프론트엔드 연동** (ProfileEditView → mypageApi)

이제 백엔드 구조를 이해하셨으니, 실제로 API를 테스트해보시고, 궁금한 점이 있으면 언제든 물어보세요!
