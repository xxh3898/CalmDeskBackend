-- 현재 엔티티 기준 더미 데이터
-- 사용 순서: 1) db-reset.sql 실행  2) 백엔드 1회 기동 (ddl create로 테이블 생성)  3) 이 파일 실행
-- 로그인 테스트: 이메일 hong@calmdesk.com / 비밀번호 password (위 순서대로 하면 member_id=1)

USE calmdesk;

-- 1. COMPANY
INSERT INTO company (company_name, company_code, category, min_value, max_value) VALUES
('calmdesk', 'CALM001', 'IT', 100, 50000),
('테스트회사', 'TEST002', '서비스', 50, 30000);

-- 2. DEPARTMENT (company_id 참조)
INSERT INTO department (department_name, company_id) VALUES
('개발팀', 1),
('인사팀', 1),
('기획팀', 1),
('영업팀', 2);

-- 3. MEMBER_RANK
INSERT INTO member_rank (rank_name) VALUES
('사원'), ('대리'), ('과장'), ('부장');

-- 4. MEMBER (company_id, department_id, rank_id 참조, password=BCrypt "password")
INSERT INTO member (name, email, password, phone, role, status, register_date, company_id, department_id, rank_id, created_date, modify_date) VALUES
('홍길동', 'hong@calmdesk.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '010-1111-2222', 'ADMIN', 'Y', '2025-01-15', 1, 1, 1, NOW(), NOW()),
('김철수', 'kim@calmdesk.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '010-2222-3333', 'EMPLOYEE', 'Y', '2025-03-01', 1, 2, 2, NOW(), NOW()),
('이영희', 'lee@calmdesk.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '010-3333-4444', 'EMPLOYEE', 'Y', '2025-06-10', 1, 1, 1, NOW(), NOW()),
('박민수', 'park@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '010-4444-5555', 'EMPLOYEE', 'Y', '2025-09-01', 2, 4, 3, NOW(), NOW());

-- 5. GIFTICON (이미지 컬럼: URL 또는 이모지)
INSERT INTO gifticon (gifticon_name, image, price, stock_quantity, period, status) VALUES
('스타벅스 아메리카노', '☕', 3000, 100, 30, 'Y'),
('배달의민족 5천원권', '🍜', 5000, 50, 90, 'Y'),
('쿠팡이츠 1만원권', '🛵', 10000, 30, 90, 'Y'),
('문화상품권 5천원', '🎫', 5000, 80, 365, 'Y'),
('기프티콘 테스트', '🎁', 1000, 200, 30, 'Y');

-- 6. MISSION_LIST
INSERT INTO mission_list (reward_name, point_account, reward_description, status) VALUES
('출석 체크', 100, '매일 첫 출석 시 100P', 'Y'),
('주간 미션', 500, '주 1회 목표 달성 시 500P', 'Y'),
('이벤트 미션', 1000, '이벤트 기간 한정 1000P', 'Y');

-- 7. GIFT_ORDER (member_id, gifticon_id 참조, 마이페이지 기프티콘 목록용)
INSERT INTO gift_order (period, order_date, approval_amount, member_id, gifticon_id) VALUES
(30, '2025-12-01', 3000, 1, 1),
(90, '2025-12-05', 5000, 1, 2),
(30, '2025-12-10', 3000, 1, 1),
(90, '2025-12-15', 10000, 2, 3),
(30, '2025-12-20', 1000, 3, 5);

-- 8. POINT_HISTORY (member_id 참조, 마이페이지 포인트/현재잔액용 – balance_after 누적)
INSERT INTO point_history (point_type, amount, balance_after, source_type, member_id, gifticon_id, mission_id, create_date) VALUES
('EARN', 500, 500, '미션 적립', 1, NULL, 1, NOW() - INTERVAL 10 DAY),
('EARN', 1000, 1500, '미션 적립', 1, NULL, 2, NOW() - INTERVAL 5 DAY),
('SPEND', 3000, 0, '기프티콘 사용', 1, 1, NULL, NOW() - INTERVAL 2 DAY),
('EARN', 500, 500, '미션 적립', 2, NULL, 1, NOW() - INTERVAL 3 DAY),
('EARN', 100, 100, '출석', 3, NULL, NULL, NOW());

-- 9. consultation (member_id 참조)
INSERT INTO consultation (title, description, status, member_id, created_date, modify_date) VALUES
('연차 문의', '2025년 연차 일수 확인 요청', 'COMPLETED', 1, NOW() - INTERVAL 5 DAY, NOW()),
('복지 포인트 문의', '포인트 사용 가능 기한 문의', 'IN_PROGRESS', 2, NOW() - INTERVAL 2 DAY, NOW()),
('시스템 오류 신고', '로그인 지연 현상', 'WAITING', 3, NOW(), NOW());
