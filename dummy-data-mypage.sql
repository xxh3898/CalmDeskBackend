-- 마이페이지 더미 데이터 (엔티티 기준)
-- 실행 순서: FK 관계를 고려하여 순서대로 실행

USE calmdesk;

DROP DATABASE IF EXISTS calmdesk;

CREATE DATABASE calmdesk
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 1. 회사 (COMPANY)
INSERT INTO COMPANY (company_name, company_code, category, min_value, max_value) VALUES
('칼름데스크', 'CALMDESK', 'IT', 0, 100000);

-- 2. 부서 (DEPARTMENT) - company_id = 1
INSERT INTO DEPARTMENT (department_name, company_id) VALUES
('개발팀', 1),
('디자인팀', 1),
('기획팀', 1);

-- 3. 직급 (MEMBER_RANK)
INSERT INTO MEMBER_RANK (rank_name) VALUES
('사원'),
('대리'),
('과장'),
('차장'),
('부장');

-- 4. 회원 (MEMBER) - company_id=1, department_id=1~3, rank_id=1~2
-- BaseTimeEntity: created_date, modify_date 포함
-- 비밀번호: BCrypt "password123" 해시 사용 시 로그인 가능 (아래 해시로 교체 가능)
INSERT INTO MEMBER (name, email, password, phone, role, status, join_date, company_id, department_id, rank_id, created_date, modify_date) VALUES
('홍길동', 'user1@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '010-1111-2222', 'EMPLOYEE', 'Y', CURDATE(), 1, 1, 1, NOW(), NOW()),
('김철수', 'user2@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '010-2222-3333', 'ADMIN', 'Y', DATE_SUB(CURDATE(), INTERVAL 1 YEAR), 1, 2, 2, NOW(), NOW()),
('이영희', 'user3@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '010-3333-4444', 'EMPLOYEE', 'Y', DATE_SUB(CURDATE(), INTERVAL 6 MONTH), 1, 3, 1, NOW(), NOW());

-- 4-1. 계좌 (ACCOUNT) - MEMBER 1:1, 등록된 회원 수만큼만 삽입
-- ※ 기존 문제: member_id=4 계좌를 넣었지만 회원은 3명(1,2,3)뿐이라 FK 오류 → 3건만 삽입
INSERT INTO ACCOUNT (member_id, remaining_point, total_earned, total_spent) VALUES
(1, 3000,  50000, 47000),   -- 홍길동: POINT_HISTORY 마지막 balance_after=3000과 맞춤
(2, 500,   500,   0),
(3, 100,   100,   0);

-- 5. 기프티콘 (GIFTICON)
INSERT INTO GIFTICON (gifticon_name, image, price, stock_quantity, period, status) VALUES
('스타벅스 아메리카노', '☕', 5000, 100, 30, 'Y'),
('배달의민족 1만원권', '🛵', 10000, 50, 60, 'Y'),
('BHC 후라이드 치킨', '🍗', 20000, 30, 30, 'Y'),
('CGV 영화관람권', '🎬', 12000, 40, 90, 'Y'),
('교보문고 도서상품권', '📚', 15000, 60, 60, 'Y'),
('올리브영 2만원권', '💄', 20000, 25, 30, 'Y'),
('GS25 편의점 상품권', '🏪', 5000, 80, 30, 'Y'),
('맥도날드 햄버거 세트', '🍔', 8000, 50, 30, 'Y'),
('이디야 커피 아메리카노', '☕', 4000, 100, 30, 'Y'),
('쿠팡 5천원권', '📦', 5000, 70, 60, 'Y');

-- 6. 주문 (GIFT_ORDER) - member_id=1 (홍길동)
INSERT INTO GIFT_ORDER (period, order_date, approval_amount, member_id, gifticon_id) VALUES
(30, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 5000, 1, 1),
(60, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 10000, 1, 2),
(30, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 20000, 1, 3),
(90, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 12000, 1, 4),
(60, DATE_SUB(CURDATE(), INTERVAL 25 DAY), 15000, 1, 5),
(30, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 20000, 1, 6),
(30, DATE_SUB(CURDATE(), INTERVAL 35 DAY), 5000, 1, 7),
(30, DATE_SUB(CURDATE(), INTERVAL 40 DAY), 8000, 1, 8),
(30, DATE_SUB(CURDATE(), INTERVAL 45 DAY), 4000, 1, 9),
(60, DATE_SUB(CURDATE(), INTERVAL 50 DAY), 5000, 1, 10);

-- 7. 포인트 내역 (POINT_HISTORY) - member_id=1
-- SPEND는 amount를 양수로 넣고, balance_after만 차감된 값으로 넣는 방식 권장 (백엔드에서 type으로 +/- 구분)
INSERT INTO POINT_HISTORY (point_type, amount, balance_after, source_type, member_id, gifticon_id, mission_id, create_date) VALUES
('EARN', 10000, 10000, 'MISSION', 1, NULL, 1, DATE_SUB(NOW(), INTERVAL 30 DAY)),
('EARN', 15000, 25000, 'MISSION', 1, NULL, 2, DATE_SUB(NOW(), INTERVAL 25 DAY)),
('EARN', 20000, 45000, 'MISSION', 1, NULL, 3, DATE_SUB(NOW(), INTERVAL 20 DAY)),
('EARN', 5000, 50000, 'MISSION', 1, NULL, 4, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('SPEND', 5000, 45000, 'PURCHASE', 1, 1, NULL, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('SPEND', 10000, 35000, 'PURCHASE', 1, 2, NULL, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('SPEND', 20000, 15000, 'PURCHASE', 1, 3, NULL, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('SPEND', 12000, 3000, 'PURCHASE', 1, 4, NULL, DATE_SUB(NOW(), INTERVAL 20 DAY));

-- 8. 알림 (NOTIFICATION) - 현재 프로젝트에 Notification 엔티티/테이블 없음 → 주석 처리
-- INSERT INTO NOTIFICATION (title, content, status, member_id, created_date) VALUES ...

-- 확인용
-- SELECT * FROM MEMBER WHERE member_id = 1;
-- SELECT * FROM ACCOUNT WHERE member_id = 1;
-- SELECT * FROM POINT_HISTORY WHERE member_id = 1 ORDER BY create_date DESC;

SELECT * FROM COMPANY;
SELECT * FROM MEMBER;
SELECT * FROM ACCOUNT;
SELECT * FROM POINT_HISTORY WHERE member_id = 1 ORDER BY create_date DESC;
