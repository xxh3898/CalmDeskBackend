# 🌿 CalmDesk (칼름데스크) - Backend

직장인의 스트레스 관리와 건강한 업무 환경을 위한 **Wellbeing Management System**입니다. 생체 데이터(심박수 등)를 기반으로 한 스트레스 분석, 효율적인 근태 관리, 그리고 보상 기반의 웰빙 포인트 시스템을 제공합니다.

---

## 🚀 주요 기능 (Key Features)

### 1. 웰빙 대시보드 (Admin/Employee Wellbeing)
- **스트레스 지수 분석**: 생체 데이터를 수집하여 0~100% 범위의 직관적인 스트레스 점수로 변환 및 시각화합니다.
- **고위험군 관리**: 스트레스 지수가 높은 직원을 자동으로 감지하여 관리 대시보드에서 실시간 모니터링합니다.
- **부서별 통계**: 부서별 평균 스트레스 수준, 휴식 횟수(Cooldown) 등을 분석하여 조직 건강 상태를 진단합니다.

### 2. 근태 관제 (Attendance Control)
- **실시간 출퇴근 관리**: QR 또는 모바일 앱을 통한 정밀한 출퇴근 기록 및 근태 상태 관리를 지원합니다.
- **웰빙 연동 출근**: 스트레스 상태에 따른 유연한 휴식 권고 시스템과 연동됩니다.

### 3. 포인트 및 보상 시스템 (Rewards)
- **웰빙 포인트**: 스트레스 관리 활동(명상, 휴식 등) 보상으로 포인트를 지급합니다.
- **기프티콘 스토어**: 적립된 포인트로 기업 내 제휴 스토어에서 기프티콘(커피, 간식 등)을 구매할 수 있습니다.
- **쿠폰 보관함**: 구매한 기프티콘의 유효기간 및 사용 여부를 마이페이지에서 통합 관리합니다.

### 4. 상담 및 소통 (Consultation & Feedback)
- **전문상담 신청**: 고위험군 직원을 위한 심리 상담 예약 및 매칭 시스템을 제공합니다.
- **웰빙 설문**: 정기적인 자기 진단 설문을 통해 데이터를 보정하고 개인별 최적화된 휴식 모델을 제안합니다.

---

## 🛠 Tech Stack

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 4.0.1
- **Database**: 
  - **MySQL**: 메인 관계형 데이터베이스
  - **Redis**: 실시간 데이터 처리 및 캐싱 (출퇴근 상태, 세션 등)
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security & JWT (JSON Web Token)
- **Build Tool**: Gradle

### Infrastructure
- **Docker**: 서비스 컨테이너화 및 개발 환경 일치
- **GitHub Actions**: CI/CD 파이프라인 (필요 시 연동 가능)

---

## 🏗 프로젝트 구조 (Architecture)

**Domain-Driven Layered Architecture**를 지향하며, 각 도메인은 독립적으로 구성되어 있습니다.

```
com.code808.calmdesk
├── domain
│   ├── attendance    # 출퇴근/근태 도메인
│   ├── auth          # 인증/인가 (JWT) 도메인
│   ├── company       # 조직/부서 관리
│   ├── consultation  # 상담 관리
│   ├── dashboard     # 관리자/직원 통계 대시보드
│   ├── gifticon      # 포인트/기프티콘/주문
│   ├── member        # 회원 정보 및 프로필
│   └── survey        # 스트레스 자가 진단 설문
├── global
│   ├── config        # 보안/Redis/JPA 설정
│   ├── dto           # 공통 응답 포맷 (ApiResponse)
│   ├── exception     # 전역 예외 처리
│   └── util          # 유틸 클래스
```

---

## ⚙️ Development Setup

### Requirementss
- JDK 17
- MySQL 8.0+
- Redis

### 환경 설정
1. `src/main/resources/application-secret.yaml` 파일을 생성하고 다음 정보를 입력합니다:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/calmdesk_db
       username: [YOUR_USERNAME]
       password: [YOUR_PASSWORD]
     data:
       redis:
         host: localhost
         port: 6379
   jwt:
     secret: [YOUR_JWT_SECRET_KEY]
   ```

2. 프로젝트 루트에서 명령어를 실행하여 서비스를 기동합니다:
   ```bash
   ./gradlew bootRun
   ```

### 데이터베이스 초기화
- 첫 기동 시 `src/main/resources/data.sql`에 정의된 기본 데이터(회사, 부서, 직급 등)가 자동으로 삽입됩니다.

---

## 🔗 API Documentation
- 서비스 포트: `8080`
- 기본 API 엔드포인트: `/api`
- (선택 사항) Swagger UI: `http://localhost:8080/swagger-ui.html` (설정 시)

---

## 👥 Contributors
- **Code808 Team**