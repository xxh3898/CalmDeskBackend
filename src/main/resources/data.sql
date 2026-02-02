-- TRUNCATE 구문 제거됨 (ddl-auto: create 모드에서는 불필요하며, 테이블 없을 시 에러 유발)

-- 1. 회사
INSERT INTO company (company_name, company_code, category, min_value, max_value) VALUES
                                                                                     ('칼름데스크', 'CALMDESK', 'IT', 0, 100000),
                                                                                     ('테크솔루션', 'TECHSOL', 'IT', 0, 100000),
                                                                                     ('퓨처인사이트', 'FUTURE', 'CONSULTING', 0, 50000),
                                                                                     ('그린에너지', 'GREEN', 'ENERGY', 0, 80000),
                                                                                     ('스마트물류', 'LOGIS', 'LOGISTICS', 0, 70000);

-- 2. 부서
INSERT INTO department (department_name, company_id) VALUES
                                                         ('개발팀', 1),
                                                         ('디자인팀', 1),
                                                         ('기획팀', 1),
                                                         ('인사팀', 1),
                                                         ('영업팀', 1),
                                                         ('마케팅팀', 1);

-- 3. 직급
INSERT INTO member_rank (rank_name) VALUES
                                        ('사원'),
                                        ('대리'),
                                        ('과장'),
                                        ('차장'),
                                        ('부장');

-- 4. 회원
-- 비밀번호: pass1234!
INSERT INTO member (name, email, password, phone, role, status, join_date, company_id, department_id, rank_id, created_date, modify_date) VALUES
                                                                                                                                              ('김철수', 'user1@test.com', '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa', '010-1111-1111', 'EMPLOYEE', 'Y', DATE_SUB(CURRENT_DATE, INTERVAL 2 YEAR), 1, 1, 3, NOW(), NOW()),
                                                                                                                                              ('이영희', 'user2@test.com', '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa', '010-2222-2222', 'EMPLOYEE', 'Y', DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR), 1, 2, 2, NOW(), NOW()),
                                                                                                                                              ('박민수', 'user3@test.com', '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa', '010-3333-3333', 'EMPLOYEE', 'Y', DATE_SUB(CURRENT_DATE, INTERVAL 6 MONTH), 1, 3, 1, NOW(), NOW()),
                                                                                                                                              ('최지현', 'user4@test.com', '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa', '010-4444-4444', 'ADMIN', 'Y', DATE_SUB(CURRENT_DATE, INTERVAL 3 YEAR), 1, 1, 4, NOW(), NOW()),
                                                                                                                                              ('정우성', 'user5@test.com', '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa', '010-5555-5555', 'EMPLOYEE', 'Y', DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH), 1, 2, 1, NOW(), NOW());

-- 5. 계좌
INSERT INTO account (member_id, remaining_point, total_earned, total_spent) VALUES
                                                                                (1, 47000, 97000, 50000),
                                                                                (2, 15000, 20000, 5000),
                                                                                (3, 5000, 5000, 0),
                                                                                (4, 100000, 200000, 100000),
                                                                                (5, 0, 0, 0);

-- 6. 휴가
INSERT INTO vacation_rest (rest_id, total_count, spent_count, member_id) VALUES
                                                                             (1, 15, 5, 1),
                                                                             (2, 15, 2, 2),
                                                                             (3, 12, 0, 3),
                                                                             (4, 20, 10, 4),
                                                                             (5, 12, 1, 5);

-- 7. 기프티콘
INSERT INTO gifticon (gifticon_name, image, price, stock_quantity, period, status) VALUES
                                                                                       ('스타벅스 아메리카노', 'https://via.placeholder.com/150', 4500, 100, 30, 'Y'),
                                                                                       ('배스킨라빈스 파인트', 'https://via.placeholder.com/150', 8900, 50, 60, 'Y'),
                                                                                       ('문화상품권 1만원권', 'https://via.placeholder.com/150', 10000, 30, 365, 'Y'),
                                                                                       ('치킨 세트', 'https://via.placeholder.com/150', 20000, 10, 90, 'Y'),
                                                                                       ('영화 관람권', 'https://via.placeholder.com/150', 12000, 40, 180, 'Y');

-- 8. 상담
INSERT INTO consultation (title, description, status, member_id, created_date, modify_date) VALUES
                                                                                                ('업무 스트레스 상담', '최근 업무량이 늘어 힘듭니다.', 'WAITING', 1, NOW(), NOW()),
                                                                                                ('대인관계 고민', '동료와의 소통이 어렵습니다.', 'IN_PROGRESS', 2, NOW(), NOW()),
                                                                                                ('진로 상담', '커리어 패스에 대해 고민입니다.', 'WAITING', 3, NOW(), NOW()),
                                                                                                ('건강 문제', '건강상 이유로 휴직을 고려 중입니다.', 'COMPLETED', 1, DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 1 MONTH)),
                                                                                                ('적응 문제', '신입 사원으로서 적응이 어렵습니다.', 'WAITING', 5, NOW(), NOW());

-- 9. 근태 (Attendance)
INSERT INTO attendance (work_date, check_in, check_out, attendance_status, member_id) VALUES
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), ' 18:05:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 29 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 29 DAY), ' 08:55:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 29 DAY), ' 18:10:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), ' 09:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 27 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 27 DAY), ' 08:45:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 27 DAY), ' 18:30:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 26 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 26 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 26 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), ' 09:10:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), ' 18:00:00'), 'LATE', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 24 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 24 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 24 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 23 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 23 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 23 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), ' 08:55:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), ' 19:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 21 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 21 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 21 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), ' 08:40:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 19 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 19 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 19 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), ' 09:05:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), ' 18:00:00'), 'LATE', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 17 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 17 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 17 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), ' 08:30:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), ' 17:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 13 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 13 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 13 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 9 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 9 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 9 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), ' 09:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 18:00:00'), 'ATTEND', 1),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 18:00:00'), 'ATTEND', 1);

-- 오늘 근태
INSERT INTO attendance (work_date, check_in, check_out, attendance_status, member_id) VALUES
    (CURRENT_DATE, CONCAT(CURRENT_DATE, ' 08:55:00'), NULL, 'ATTEND', 1);

-- user2 최근 3일
INSERT INTO attendance (work_date, check_in, check_out, attendance_status, member_id) VALUES
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 18:00:00'), 'ATTEND', 2),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 09:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 18:00:00'), 'ATTEND', 2),
                                                                                          (CURRENT_DATE, CONCAT(CURRENT_DATE, ' 08:50:00'), NULL, 'ATTEND', 2);

-- 10. 감정 체크인
INSERT INTO emotion_checkin (attendance_id, stress_level, memo, created_date, modify_date) VALUES
                                                                                               (1, 30, '상쾌한 월요일', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
                                                                                               (2, 40, '업무 파악 중', DATE_SUB(NOW(), INTERVAL 29 DAY), DATE_SUB(NOW(), INTERVAL 29 DAY)),
                                                                                               (3, 60, '회의가 많음', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
                                                                                               (4, 50, '보통 하루', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
                                                                                               (5, 80, '불금인데 야근', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
                                                                                               (6, 20, '주말 푹 쉬고 옴', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
                                                                                               (7, 30, '업무 집중 잘됨', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
                                                                                               (8, 45, '무난함', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
                                                                                               (9, 70, '급한 에러 발생', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
                                                                                               (10, 60, '피곤함', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
                                                                                               (11, 30, '컨디션 좋음', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
                                                                                               (12, 40, '점심 맛있었음', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
                                                                                               (13, 50, '오후에 졸림', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
                                                                                               (14, 85, '보고서 마감 압박', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
                                                                                               (15, 90, '스트레스 최고조', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
                                                                                               (16, 40, '주말 지나고 회복', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
                                                                                               (17, 35, '여유로운 편', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
                                                                                               (18, 55, '동료와 약간의 마찰', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
                                                                                               (19, 45, '무난하게 해결', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
                                                                                               (20, 30, '금요일 퇴근 기다림', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
                                                                                               (21, 20, '월요병 없음', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
                                                                                               (22, 40, '새 프로젝트 시작', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
                                                                                               (23, 60, '일정이 타이트함', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
                                                                                               (24, 70, '야근 확정', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
                                                                                               (25, 50, '그럭저럭', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
                                                                                               (26, 40, '날씨가 좋음', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
                                                                                               (27, 30, '집중력 최고', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
                                                                                               (28, 55, '약간 지침', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
                                                                                               (29, 65, '어제 이슈 처리', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
                                                                                               (30, 40, '오늘 아침 상쾌', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 11. 포인트 내역
INSERT INTO point_history (point_type, amount, balance_after, source_type, member_id, gifticon_id, mission_id, create_date) VALUES
                                                                                                                                ('EARN', 1000, 48000, 'ATTENDANCE', 1, NULL, 1, NOW()),
                                                                                                                                ('SPEND', 4500, 43500, 'GIFTICON', 1, 1, NULL, NOW()),
                                                                                                                                ('EARN', 5000, 20000, 'EVENT', 2, NULL, NULL, NOW()),
                                                                                                                                ('EARN', 100, 5100, 'ATTENDANCE', 3, NULL, 5, NOW()),
                                                                                                                                ('EARN', 10000, 110000, 'PROJECT', 4, NULL, NULL, NOW());

-- 12. 주문 내역
INSERT INTO gift_order (period, order_date, approval_amount, member_id, gifticon_id) VALUES
                                                                                         (30, CURRENT_DATE, 4500, 1, 1),
                                                                                         (60, DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 8900, 2, 2),
                                                                                         (365, DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), 10000, 4, 3),
                                                                                         (90, DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), 20000, 1, 4),
                                                                                         (30, DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 4500, 3, 1);