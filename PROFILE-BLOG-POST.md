# [개인 프로젝트] Spring Boot + React로 마이페이지 프로필 조회·수정 기능 구현하기

## 서론

Spring Boot 백엔드와 React 프론트엔드를 연동하여 마이페이지의 **프로필 조회**와 **프로필 수정** 기능을 구현했다. 
프론트엔드에서 API를 호출하고, 백엔드에서 데이터를 조회·수정하는 전체 흐름을 경험하면서 
**RESTful API 설계**, **JPA/Hibernate**, **React Hooks**, **상태 관리** 등에 대해 학습할 수 있었다.

---

## 주제

**칼름데스크(CalmDesk)** 프로젝트의 마이페이지 기능 중 **프로필 조회**와 **프로필 수정** 기능을 구현했다.

- **프로필 조회**: 회원의 이메일, 전화번호, 회사명, 부서, 직급, 입사일, 현재 포인트 등을 조회
- **프로필 수정**: 이메일과 전화번호를 수정 (중복 검사 포함)
- **비밀번호 변경**: 선택적으로 비밀번호 변경 가능

---

## 목표

1. **프론트엔드와 백엔드 연동**: React에서 Axios를 사용하여 Spring Boot API를 호출하는 방법 학습
2. **JPA/Hibernate 활용**: `@Query`와 `LEFT JOIN FETCH`를 사용하여 N+1 문제 해결
3. **React Hooks 이해**: `useState`, `useEffect`를 활용한 상태 관리와 API 호출
4. **DTO 패턴**: Entity와 DTO를 분리하여 API 응답 구조화
5. **검증 로직**: 프론트엔드와 백엔드 양쪽에서 데이터 검증 구현

---

## 구조

### 프로젝트 구조

**프론트엔드 (React):**
```
finalprojectfront/src/
├── pages/employee/MyPage/
│   ├── MyPage.jsx              # 라우팅
│   ├── MyPageMain.jsx          # 마이페이지 메인 (프로필 조회)
│   └── ProfileEditView.jsx     # 프로필 수정 화면
├── api/
│   ├── axios.js                # Axios 설정 (baseURL, 인터셉터)
│   └── mypageApi.js            # 마이페이지 API 함수들
└── store/
    └── useStore.js             # Zustand 전역 상태 관리
```

**백엔드 (Spring Boot):**
```
finalprojectback/src/main/java/com/code808/calmdesk/
├── domain/mypage/
│   ├── controller/employee/
│   │   └── MyPageController.java      # REST API 엔드포인트
│   ├── service/employee/
│   │   ├── MyPageService.java         # 인터페이스
│   │   └── MyPageServiceImpl.java     # 비즈니스 로직 구현
│   └── dto/
│       ├── ProfileResponse.java       # 프로필 조회 응답 DTO
│       └── ProfileUpdateRequest.java  # 프로필 수정 요청 DTO
├── domain/member/
│   ├── entity/
│   │   └── Member.java                # 회원 엔티티
│   └── repository/
│       └── MemberRepository.java      # 데이터 접근 계층
└── global/
    └── dto/
        └── ApiResponse.java           # 공통 응답 래퍼
```

---

## 핵심 기능

### 1. 프로필 조회 기능

프로필 조회는 화면이 로드될 때 자동으로 실행된다. React의 `useEffect` Hook을 사용하여 컴포넌트 마운트 시 API를 호출한다.

#### 프론트엔드 (React)

**파일:** `ProfileEditView.jsx`

```javascript
import { useState, useEffect } from 'react';
import { mypageApi } from '../../../api/mypageApi';
import useStore from '../../../store/useStore';

const ProfileEditView = () => {
  const { user } = useStore();
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [loading, setLoading] = useState(true);

  // memberId 추출 (user.id가 없으면 기본값 1)
  const getMemberId = () => {
    if (!user || !user.id) return 1;
    const id = typeof user.id === 'string' ? parseInt(user.id, 10) : Number(user.id);
    return isNaN(id) ? 1 : id;
  };
  const memberId = getMemberId();

  // 컴포넌트 마운트 시 프로필 조회
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true);
        const res = await mypageApi.getProfile(memberId);
        // → GET /api/mypage/profile?memberId=1
        
        if (res.success && res.data) {
          setEmail(res.data.email || '');
          setPhone(res.data.phone || '');
        }
      } catch (err) {
        console.error('프로필 로드 실패:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [memberId]);

  // ... 나머지 코드
};
```

**설명:**
- `useEffect`의 두 번째 인자 `[memberId]`는 `memberId`가 변경될 때만 `useEffect`가 재실행되도록 한다.
- `async/await`를 사용하여 비동기 API 호출을 처리한다.
- `mypageApi.getProfile(memberId)`는 내부적으로 Axios를 사용하여 HTTP GET 요청을 보낸다.

**API 함수 (mypageApi.js):**

```javascript
export const mypageApi = {
  getProfile: async (memberId) => {
    const response = await apiClient.get('/mypage/profile', {
      params: { memberId },
    });
    return response.data;
  },
};
```

**Axios 설정 (axios.js):**

```javascript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8008/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 토큰 추가
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;
```

**설명:**
- `baseURL`을 설정하여 모든 API 요청의 기본 URL을 지정한다.
- 요청 인터셉터를 사용하여 `localStorage`에서 토큰을 가져와 `Authorization` 헤더에 추가한다.
- 최종 요청 URL: `GET http://localhost:8008/api/mypage/profile?memberId=1`

---

#### 백엔드 (Spring Boot)

**Controller (MyPageController.java):**

```java
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

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
}
```

**설명:**
- `@GetMapping("/profile")`: HTTP GET 요청을 처리한다.
- `@RequestParam Long memberId`: 쿼리 파라미터로 `memberId`를 받는다.
- `ApiResponse.success()`: 성공 응답을 래핑하여 일관된 응답 형식을 유지한다.

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

**Service (MyPageServiceImpl.java):**

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;

    @Override
    public ProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return ProfileResponse.from(member);
    }
}
```

**설명:**
- `@Transactional(readOnly = true)`: 읽기 전용 트랜잭션으로 성능을 최적화한다.
- `findByIdWithCompanyAndDepartmentAndRank()`: 회원 정보와 함께 회사, 부서, 직급 정보를 한 번에 조회한다 (N+1 문제 해결).

**Repository (MemberRepository.java):**

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m " +
           "LEFT JOIN FETCH m.company " +
           "LEFT JOIN FETCH m.department " +
           "LEFT JOIN FETCH m.rank " +
           "WHERE m.memberId = :memberId")
    Optional<Member> findByIdWithCompanyAndDepartmentAndRank(@Param("memberId") Long memberId);
}
```

**설명:**
- `LEFT JOIN FETCH`: JPQL에서 `FETCH`를 사용하면 연관된 엔티티를 즉시 로딩한다.
- 이렇게 하면 회원을 조회할 때 회사, 부서, 직급 정보도 함께 가져와서 **N+1 문제를 방지**한다.

**실제 SQL (Hibernate가 변환):**

```sql
SELECT m.*, c.*, d.*, r.*
FROM MEMBER m
LEFT JOIN COMPANY c ON m.company_id = c.company_id
LEFT JOIN DEPARTMENT d ON m.department_id = d.department_id
LEFT JOIN MEMBER_RANK r ON m.rank_id = r.rank_id
WHERE m.member_id = 1
```

**DTO 변환 (ProfileResponse.java):**

```java
@Getter
@Builder
public class ProfileResponse {
    private Long memberId;
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String department;
    private String position;
    private String joinDate;
    private Integer currentPoint;

    public static ProfileResponse from(Member member) {
        // 포인트 계산
        long totalEarned = member.getTotalEarned() != null ? member.getTotalEarned() : 0L;
        long totalSpent = member.getTotalSpent() != null ? member.getTotalSpent() : 0L;
        int currentPoint = (int) (totalEarned - totalSpent);

        // 날짜 포맷
        String joinDate = member.getHireDate() != null
                ? member.getHireDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                : "";

        return ProfileResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .companyName(member.getCompany() != null ? member.getCompany().getCompanyName() : "")
                .department(member.getDepartment() != null ? member.getDepartment().getDepartmentName() : "")
                .position(member.getRank() != null ? member.getRank().getRankName() : "")
                .joinDate(joinDate)
                .currentPoint(currentPoint)
                .build();
    }
}
```

**설명:**
- `ProfileResponse.from()`: 정적 메서드로 Entity를 DTO로 변환한다.
- `totalEarned - totalSpent`로 현재 포인트를 계산한다.
- 날짜는 `yyyy.MM.dd` 형식으로 포맷팅한다.

---

### 2. 프로필 수정 기능

프로필 수정은 사용자가 "저장하기" 버튼을 클릭할 때 실행된다. 이메일과 전화번호를 수정할 수 있으며, 중복 검사를 수행한다.

#### 프론트엔드 (React)

**파일:** `ProfileEditView.jsx`

```javascript
const handleSave = async () => {
  const trimmedPhone = phone.trim();
  
  // 연락처 필수 체크
  if (!trimmedPhone) {
    setError('연락처를 입력하세요.');
    return;
  }

  // 비밀번호 변경 시 검증
  const wantChangePassword = currentPassword || newPassword || newPasswordConfirm;
  if (wantChangePassword) {
    if (!currentPassword.trim()) {
      setError('현재 비밀번호를 입력하세요.');
      return;
    }
    if (!newPassword.trim()) {
      setError('새 비밀번호를 입력하세요.');
      return;
    }
    if (newPassword !== newPasswordConfirm) {
      setError('새 비밀번호와 확인이 일치하지 않습니다.');
      return;
    }
  }

  setError(null);
  setSaving(true);
  
  try {
    // 1. 프로필 수정 (이메일, 전화번호)
    const res = await mypageApi.updateProfile(memberId, {
      email: email.trim() || null,
      phone: trimmedPhone
    });
    
    if (!res.success) {
      setError(res.message || '프로필 수정에 실패했습니다.');
      setSaving(false);
      return;
    }

    // 2. 비밀번호 변경 (선택적)
    if (wantChangePassword) {
      const pwRes = await mypageApi.changePassword(memberId, {
        currentPassword: currentPassword.trim(),
        newPassword: newPassword.trim()
      });
      
      if (!pwRes.success) {
        setError(pwRes.message || '비밀번호 변경에 실패했습니다.');
        setSaving(false);
        return;
      }
    }

    // 성공 시 마이페이지로 이동
    navigate('/app/mypage');
  } catch (err) {
    setError(err.response?.data?.message || err.message || '수정에 실패했습니다.');
  } finally {
    setSaving(false);
  }
};
```

**설명:**
- 프론트엔드에서 **연락처 필수 체크**와 **비밀번호 일치 검증**을 수행한다.
- 프로필 수정이 성공한 후에만 비밀번호 변경을 시도한다 (순차적 처리).
- 모든 작업이 완료되면 `navigate('/app/mypage')`로 마이페이지 메인으로 이동한다.

**API 함수:**

```javascript
// 프로필 수정
updateProfile: async (memberId, data) => {
  const response = await apiClient.put('/mypage/profile', data, {
    params: { memberId },
  });
  return response.data;
},

// 비밀번호 변경
changePassword: async (memberId, data) => {
  const response = await apiClient.put('/mypage/password', data, {
    params: { memberId },
  });
  return response.data;
},
```

**HTTP 요청:**

```
PUT http://localhost:8008/api/mypage/profile?memberId=1
Content-Type: application/json

{
  "email": "newemail@test.com",
  "phone": "010-9999-8888"
}
```

---

#### 백엔드 (Spring Boot)

**Controller (MyPageController.java):**

```java
@PutMapping("/profile")
public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
        @RequestParam Long memberId,
        @Valid @RequestBody ProfileUpdateRequest request) {
    try {
        ProfileResponse response = myPageService.updateProfile(memberId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필 수정 성공", response));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }
}
```

**설명:**
- `@PutMapping("/profile")`: HTTP PUT 요청을 처리한다.
- `@Valid`: DTO의 검증 어노테이션(`@Email`, `@NotBlank` 등)을 활성화한다.
- `@RequestBody`: JSON 요청 본문을 DTO로 변환한다.

**DTO (ProfileUpdateRequest.java):**

```java
@Getter
@NoArgsConstructor
public class ProfileUpdateRequest {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;
}
```

**설명:**
- `@Email`: 이메일 형식 검증
- `@NotBlank`: 전화번호 필수 검증
- `@Valid`가 붙은 DTO는 자동으로 검증되어, 검증 실패 시 400 Bad Request를 반환한다.

**Service (MyPageServiceImpl.java):**

```java
@Override
@Transactional
public ProfileResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
    // 1. 회원 조회
    Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // 2. 이메일 변경 검증 및 수정
    if (request.getEmail() != null && !request.getEmail().equals(member.getEmail())) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        member.setEmail(request.getEmail());
    }

    // 3. 전화번호 변경 검증 및 수정
    if (request.getPhone() != null && !request.getPhone().equals(member.getPhone())) {
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }
        member.setPhone(request.getPhone());
    }

    // 4. 저장
    memberRepository.save(member);
    
    // 5. 응답 DTO 생성
    return ProfileResponse.from(member);
}
```

**설명:**
- `@Transactional`: 이메일과 전화번호 변경이 하나의 트랜잭션으로 처리된다.
- `existsByEmail()`, `existsByPhone()`: JPA 메서드명 규칙으로 자동 생성된 쿼리 메서드
  - `existsByEmail` → `SELECT COUNT(*) > 0 FROM MEMBER WHERE email = ?`
  - `existsByPhone` → `SELECT COUNT(*) > 0 FROM MEMBER WHERE phone = ?`
- 변경된 값이 기존 값과 다를 때만 중복 검사를 수행한다.

**Repository (MemberRepository.java):**

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
```

**설명:**
- JPA 메서드명 규칙을 사용하면 구현 없이 자동으로 쿼리가 생성된다.
- `existsByEmail`은 이메일이 존재하는지 `boolean`으로 반환한다.

---

### 3. 비밀번호 변경 기능

비밀번호 변경은 프로필 수정과 별도로 처리되며, 선택적으로 실행된다.

**Service (MyPageServiceImpl.java):**

```java
@Override
@Transactional
public void changePassword(Long memberId, PasswordChangeRequest request) {
    Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

    // 현재 비밀번호 확인
    if (!member.getPassword().equals(request.getCurrentPassword())) {
        throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
    }

    // 새 비밀번호 설정
    member.setPassword(request.getNewPassword());
    memberRepository.save(member);
}
```

**설명:**
- 현재 비밀번호가 일치하는지 확인한 후, 새 비밀번호로 변경한다.
- 실제 프로젝트에서는 비밀번호를 **해싱**하여 저장해야 한다 (현재는 평문 저장).

---

## 데이터 흐름 요약

```
[프론트엔드] ProfileEditView
   ↓ getMemberId() → useStore().user.id (없으면 1)
   ↓ mypageApi.getProfile(memberId)
[HTTP] GET /api/mypage/profile?memberId=1
   ↓
[백엔드 Controller] MyPageController.getProfile(1)
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
[프론트엔드] setEmail(res.data.email), setPhone(res.data.phone)
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
[프론트엔드] res.success → (비밀번호 변경 있으면) changePassword → navigate('/app/mypage')
```

---

## Spring Boot와 React를 쓰면서 느낀점

### 1. **레이어드 아키텍처의 장점**

Controller → Service → Repository로 계층을 나누니 **책임 분리**가 명확해졌다.
- Controller: HTTP 요청/응답 처리
- Service: 비즈니스 로직
- Repository: 데이터 접근

이렇게 분리하니 코드 수정 시 영향 범위가 명확하고, 테스트도 쉬워졌다.

### 2. **JPA의 편리함과 주의점**

JPA의 메서드명 규칙(`existsByEmail`, `findByMemberOrderByOrderDateDesc`)을 사용하면 
구현 없이 자동으로 쿼리가 생성되어 편리했다. 하지만 **N+1 문제**를 주의해야 했다.

처음에는 `member.getCompany()`, `member.getDepartment()`를 각각 호출했더니 
각각 별도의 SELECT 쿼리가 실행되었다. `LEFT JOIN FETCH`를 사용하여 한 번의 쿼리로 
연관된 엔티티를 모두 가져오니 성능이 크게 개선되었다.

### 3. **React Hooks의 강력함**

`useState`와 `useEffect`를 사용하여 상태 관리와 API 호출을 깔끔하게 처리할 수 있었다.
특히 `useEffect`의 두 번째 인자(의존성 배열)를 사용하여 불필요한 재렌더링을 방지할 수 있어 유용했다.

### 4. **DTO 패턴의 필요성**

Entity를 그대로 반환하지 않고 DTO로 변환하니:
- **보안**: 비밀번호 같은 민감한 정보를 노출하지 않음
- **유연성**: 클라이언트에 필요한 형태로 데이터 변환 가능
- **유지보수**: Entity 변경 시 DTO만 수정하면 됨

이런 장점을 직접 경험할 수 있었다.

### 5. **프론트엔드와 백엔드 검증의 차이**

프론트엔드에서는 **사용자 경험**을 위해 즉시 피드백을 제공하고,
백엔드에서는 **보안**을 위해 `@Valid`와 중복 검사를 수행한다.

양쪽 모두 검증이 필요하다는 것을 배웠다. 프론트엔드 검증만으로는 부족하고, 
백엔드 검증이 없으면 악의적인 요청을 막을 수 없기 때문이다.

### 6. **에러 처리의 중요성**

`try-catch`와 `ApiResponse`를 사용하여 일관된 에러 응답을 제공하니 
프론트엔드에서 에러 처리가 훨씬 쉬워졌다. 
특히 `IllegalArgumentException`을 `ApiResponse.error()`로 변환하여 
클라이언트에게 명확한 에러 메시지를 전달할 수 있었다.

---

## 마무리

프로필 조회와 수정 기능을 구현하면서 **전체적인 웹 애플리케이션의 데이터 흐름**을 이해할 수 있었다.
프론트엔드에서 사용자 입력을 받아 API를 호출하고, 백엔드에서 비즈니스 로직을 처리한 후 
다시 프론트엔드로 응답을 보내는 과정이 명확해졌다.

다음에는 **인증/인가**, **파일 업로드**, **실시간 알림** 등 더 복잡한 기능을 구현해보고 싶다.
