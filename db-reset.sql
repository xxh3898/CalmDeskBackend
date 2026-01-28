-- calmdesk DB 전체 초기화 (notification 등 엔티티에 없는 테이블 제거)
-- MySQL에서 이 스크립트 실행 후, 백엔드를 실행하면 ddl-auto: create 로 깨끗하게 테이블이 생성됩니다.

DROP DATABASE IF EXISTS calmdesk;

CREATE DATABASE calmdesk
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 사용 후 application-secret.yaml 에서 ddl-auto 를 update 로 바꿔두세요.
