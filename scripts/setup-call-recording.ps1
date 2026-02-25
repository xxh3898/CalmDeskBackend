# =============================================================================
# CalmDesk 음성 통화 녹음(STT) 기능 설정 스크립트
# =============================================================================
# 팀원들이 음성 녹음 기능을 사용하려면 Google Cloud Speech-to-Text API 설정이 필요합니다.
# 이 스크립트는 설정 파일 템플릿 생성 및 환경 변수 설정을 도와줍니다.
# =============================================================================

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$ResourcesPath = Join-Path $ProjectRoot "src\main\resources"
$SecretPath = Join-Path $ResourcesPath "application-secret.yaml"
$RecordingsPath = Join-Path $ProjectRoot "recordings"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  CalmDesk 음성 녹음 기능 설정" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 녹음 저장 폴더 생성
Write-Host "[1/4] 녹음 저장 폴더 확인..." -ForegroundColor Yellow
if (-not (Test-Path $RecordingsPath)) {
    New-Item -ItemType Directory -Path $RecordingsPath -Force | Out-Null
    Write-Host "  -> recordings 폴더 생성됨: $RecordingsPath" -ForegroundColor Green
} else {
    Write-Host "  -> recordings 폴더가 이미 존재합니다." -ForegroundColor Green
}
Write-Host ""

# 2. application-secret.yaml 확인/템플릿 생성
Write-Host "[2/4] application-secret.yaml 확인..." -ForegroundColor Yellow
if (-not (Test-Path $SecretPath)) {
    Write-Host "  -> application-secret.yaml이 없습니다. 템플릿을 생성합니다." -ForegroundColor Yellow
    
    $template = @"
# application-secret.yaml (Git에 커밋하지 마세요!)
# 이 파일은 각자 로컬에서만 사용합니다.

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
    username: your_username
    password: your_password

jwt:
  secret: your-jwt-secret
  expiration: 1800000
  refresh-expiration: 604800000

# ===== 음성 녹음(STT) Google Cloud 설정 =====
app:
  call-record:
    google-cloud:
      # GCP 콘솔에서 프로젝트 ID 확인: https://console.cloud.google.com
      project-id: YOUR_GCP_PROJECT_ID
      # 서비스 계정 JSON 키 파일의 절대 경로 (아래 경로를 본인 환경에 맞게 수정하세요)
      credentials-path: C:\path\to\your\service-account-key.json
"@
    
    Set-Content -Path $SecretPath -Value $template -Encoding UTF8
    Write-Host "  -> 템플릿 생성됨: $SecretPath" -ForegroundColor Green
    Write-Host "  -> 아래 3단계에서 JSON 키 파일을 받고, credentials-path를 수정하세요!" -ForegroundColor Cyan
} else {
    $content = Get-Content $SecretPath -Raw
    if ($content -notmatch "call-record|google-cloud|credentials-path") {
        Write-Host "  -> application-secret.yaml에 call-record 설정이 없을 수 있습니다." -ForegroundColor Yellow
        Write-Host "  -> 아래 가이드를 참고해 app.call-record.google-cloud 섹션을 추가하세요." -ForegroundColor Cyan
    } else {
        Write-Host "  -> application-secret.yaml에 STT 설정이 이미 있습니다." -ForegroundColor Green
    }
}
Write-Host ""

# 3. GCP 설정 안내
Write-Host "[3/4] Google Cloud Speech-to-Text 설정 방법" -ForegroundColor Yellow
Write-Host @"

  [필수 단계]
  1. GCP 콘솔 접속: https://console.cloud.google.com
  2. 프로젝트 생성 또는 선택
  3. 'API 및 서비스' -> '라이브러리'에서 'Cloud Speech-to-Text API' 검색 후 사용 설정
  4. 'IAM 및 관리자' -> '서비스 계정' -> 서비스 계정 생성
     - 역할: 'Cloud Speech-to-Text API 사용자' 또는 '소유자'
  5. 생성한 서비스 계정 -> '키' -> '키 추가' -> JSON 선택 -> 다운로드
  6. 다운로드한 JSON 파일을 안전한 폴더에 보관 (예: C:\gcp-keys\your-project-key.json)
  7. application-secret.yaml에서 credentials-path를 해당 JSON 파일 경로로 수정

  [대안] 환경 변수 사용
  - credentials-path 대신 GOOGLE_APPLICATION_CREDENTIALS 환경 변수 설정 가능
  - 아래 [4/4]에서 환경 변수 설정 옵션 실행

"@ -ForegroundColor Gray
Write-Host ""

# 4. 환경 변수 설정 (선택)
Write-Host "[4/4] GOOGLE_APPLICATION_CREDENTIALS 환경 변수 설정 (선택)" -ForegroundColor Yellow
Write-Host "  application-secret.yaml 대신 환경 변수로 인증할 수 있습니다." -ForegroundColor Gray
$setEnv = Read-Host "  JSON 키 파일 경로를 입력하면 사용자 환경 변수로 설정합니다 (건너뛰려면 Enter)"
Write-Host ""

if ($setEnv -and (Test-Path $setEnv)) {
    [Environment]::SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", $setEnv, "User")
    Write-Host "  -> GOOGLE_APPLICATION_CREDENTIALS 설정 완료 (사용자 환경 변수)" -ForegroundColor Green
    Write-Host "  -> 새 터미널을 열어야 적용됩니다." -ForegroundColor Cyan
} elseif ($setEnv) {
    Write-Host "  -> 파일을 찾을 수 없습니다: $setEnv" -ForegroundColor Red
    Write-Host "  -> application-secret.yaml의 credentials-path를 직접 설정하세요." -ForegroundColor Yellow
} else {
    Write-Host "  -> 건너뜀. application-secret.yaml에서 credentials-path를 설정하세요." -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  설정 완료 후 애플리케이션을 재시작하세요." -ForegroundColor Cyan
Write-Host "  자세한 내용: scripts/SETUP-CALL-RECORDING.md" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
