# 음성 통화 녹음 기능 설정 가이드

다른 팀원들이 CalmDesk의 **음성 통화 녹음(STT)** 기능을 사용하려면 아래 설정이 필요합니다.

---

## 빠른 설정 (PowerShell 스크립트)

```powershell
cd "c:\final backend"
.\scripts\setup-call-recording.ps1
```

스크립트가 다음을 수행합니다:
- `recordings` 폴더 생성
- `application-secret.yaml` 없으면 템플릿 생성
- GCP 설정 방법 안내
- `GOOGLE_APPLICATION_CREDENTIALS` 환경 변수 설정 (선택)

---

## 수동 설정

### 1. 녹음 저장 폴더

기본 경로: `./recordings` (프로젝트 루트 기준)

애플리케이션이 자동으로 생성하므로 별도 작업 불필요. 다른 경로를 쓰려면 `application.yaml`에서 수정:

```yaml
app:
  call-record:
    recording-dir: ./recordings  # 원하는 경로로 변경
```

---

### 2. Google Cloud Speech-to-Text API 설정

#### 2-1. GCP 프로젝트 준비

1. [Google Cloud Console](https://console.cloud.google.com) 접속
2. 프로젝트 생성 또는 기존 프로젝트 선택
3. **프로젝트 ID** 확인 (예: `my-project-111-487708`)

#### 2-2. Speech-to-Text API 활성화

1. **API 및 서비스** → **라이브러리**
2. `Cloud Speech-to-Text API` 검색
3. **사용** 클릭

#### 2-3. 서비스 계정 및 키 생성

1. **IAM 및 관리자** → **서비스 계정**
2. **서비스 계정 만들기**
   - 이름: `calmdesk-stt` 등
   - 역할: `Cloud Speech-to-Text API 사용자` (또는 `소유자`)
3. 생성된 서비스 계정 클릭 → **키** 탭
4. **키 추가** → **새 키 만들기** → **JSON** 선택 → 생성
5. 다운로드된 JSON 파일을 안전한 위치에 저장  
   - 예: `C:\gcp-keys\calmdesk-stt-key.json`

---

### 3. 인증 설정 (둘 중 하나 선택)

#### 방법 A: application-secret.yaml 사용 (권장)

`src/main/resources/application-secret.yaml`에 아래 내용 추가 또는 수정:

```yaml
app:
  call-record:
    google-cloud:
      project-id: YOUR_GCP_PROJECT_ID          # 본인 GCP 프로젝트 ID
      credentials-path: C:\gcp-keys\your-service-account-key.json  # JSON 키 파일 절대 경로
```

> `application-secret.yaml`은 `.gitignore`에 포함되어 있으므로 Git에 커밋되지 않습니다.

#### 방법 B: 환경 변수 사용

Windows (PowerShell):

```powershell
[Environment]::SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", "C:\gcp-keys\your-key.json", "User")
```

Windows (명령 프롬프트, 사용자 단위):

```cmd
setx GOOGLE_APPLICATION_CREDENTIALS "C:\gcp-keys\your-key.json"
```

**중요:** 환경 변수 변경 후 터미널/IDE를 다시 실행해야 합니다.

---

### 4. 설정 확인

1. 애플리케이션 실행
2. 로그에서 다음 메시지 확인:
   - `Google Cloud 인증: application-secret.yaml의 credentials-path 사용` 또는
   - `Google Cloud 인증: 환경 변수 GOOGLE_APPLICATION_CREDENTIALS 사용`
3. 통화 녹음 업로드 API `POST /api/call-records` 호출 테스트

---

## 지원 오디오 형식

- WebM (권장, 브라우저 기본 녹음 형식)
- WAV
- OGG/Opus

---

## 문제 해결

| 증상 | 확인 사항 |
|------|----------|
| `Google Cloud 인증 JSON 파일을 찾을 수 없습니다` | `credentials-path` 또는 `GOOGLE_APPLICATION_CREDENTIALS` 경로가 올바른지 확인 |
| `Permission denied` | 서비스 계정에 Speech-to-Text API 사용 권한이 있는지 확인 |
| STT 결과가 비어 있음 | 오디오에 실제 음성이 있는지, 형식이 지원되는지 확인 |
| `recordings` 폴더 없음 | 앱 첫 실행 시 자동 생성됨. 수동 생성 시 `mkdir recordings` |

---

## 요약

1. GCP에서 Speech-to-Text API 활성화
2. 서비스 계정 JSON 키 다운로드
3. `application-secret.yaml`의 `credentials-path` 설정 또는 `GOOGLE_APPLICATION_CREDENTIALS` 환경 변수 설정
4. 애플리케이션 재시작
