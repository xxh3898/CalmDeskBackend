-- 명함 연락처 테이블 (직원/외부인/협력사)
-- MySQL: 테이블명 소문자 (Hibernate 기본 네이밍)
CREATE TABLE IF NOT EXISTS business_card_contact (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    department_id BIGINT NULL,
    contact_type VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    company_name VARCHAR(200) NULL,
    title VARCHAR(100) NULL,
    phone VARCHAR(50) NULL,
    mobile VARCHAR(50) NULL,
    email VARCHAR(100) NULL,
    address VARCHAR(500) NULL,
    fax VARCHAR(50) NULL,
    website VARCHAR(200) NULL,
    created_date DATETIME(6) NULL,
    modify_date DATETIME(6) NULL,
    INDEX IDX_BC_COMPANY (company_id),
    INDEX IDX_BC_PHONE (company_id, phone),
    INDEX IDX_BC_EMAIL (company_id, email)
);
