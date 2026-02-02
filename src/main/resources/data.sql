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
INSERT INTO account (member_id, remaining_point, total_earned, total_spent, account_leave, total_earned_point, total_spent_point) VALUES
                                                                                                                                      (1, 47000, 97000, 50000, 0, 97000, 50000),
                                                                                                                                      (2, 15000, 20000, 5000, 0, 20000, 5000),
                                                                                                                                      (3, 5000, 5000, 0, 0, 5000, 0),
                                                                                                                                      (4, 100000, 200000, 100000, 0, 200000, 100000),
                                                                                                                                      (5, 0, 0, 0, 0, 0, 0);

-- 6. 휴가
INSERT INTO vacation_rest (rest_id, total_count, spent_count, member_id) VALUES
                                                                             (1, 15, 5, 1),
                                                                             (2, 15, 2, 2),
                                                                             (3, 12, 0, 3),
                                                                             (4, 20, 10, 4),
                                                                             (5, 12, 1, 5);

-- 7. 기프티콘
INSERT INTO gifticon (gifticon_name, image, price, stock_quantity, period, status, company_id) VALUES
                                                                                                   ('스타벅스 아메리카노', 'https://via.placeholder.com/150', 4500, 100, 30, 'Y', 1),
                                                                                                   ('배스킨라빈스 파인트', 'https://via.placeholder.com/150', 8900, 50, 60, 'Y', 1),
                                                                                                   ('문화상품권 1만원권', 'https://via.placeholder.com/150', 10000, 30, 365, 'Y', 1),
                                                                                                   ('치킨 세트', 'https://via.placeholder.com/150', 20000, 10, 90, 'Y', 1),
                                                                                                   ('영화 관람권', 'https://via.placeholder.com/150', 12000, 40, 180, 'Y', 1);

-- [추가] 7-5. 미션 (Point History에서 참조하므로 필요)
INSERT INTO mission_list (mission_code, reward_name, reward_description, goal_count, point_account, status) VALUES
                                                                                                                ('MISSION_ATTEND_7', '7일 연속 출석', '7일 연속 출석 시 포인트 지급', 7, 1000, 'Y'),
                                                                                                                ('MISSION_EARLY_BIRD', '얼리버드', '08:30 이전 출근', 1, 100, 'Y'),
                                                                                                                ('MISSION_REVIEW', '리뷰 작성', '상담 리뷰 작성', 1, 500, 'Y'),
                                                                                                                ('MISSION_MENTOR', '멘토링', '신입사원 멘토링 완료', 1, 2000, 'Y'),
                                                                                                                ('MISSION_PROJECT', '프로젝트 완료', '프로젝트 성공적 완료', 1, 10000, 'Y');

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

-- user2 최근 3일
INSERT INTO attendance (work_date, check_in, check_out, attendance_status, member_id) VALUES
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 18:00:00'), 'ATTEND', 2),
                                                                                          (DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 09:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 18:00:00'), 'ATTEND', 2),
                                                                                          (CURRENT_DATE, CONCAT(CURRENT_DATE, ' 08:50:00'), NULL, 'ATTEND', 2);

-- 10. 감정 체크인
INSERT INTO emotion_checkin (attendance_id, stress_level, memo, created_date, modify_date) VALUES
                                                                                               (1, 2, '출근: 상쾌한 월요일', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), ' 08:50:00')),
                                                                                               (1, 3, '퇴근: 업무 파악 완료', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), ' 18:05:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), ' 18:05:00')),
                                                                                               (2, 2, '출근: 비가 옴', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 29 DAY), ' 08:55:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 29 DAY), ' 08:55:00')),
                                                                                               (2, 3, '퇴근: 내일 할 일 정리', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 29 DAY), ' 18:10:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 29 DAY), ' 18:10:00')),
                                                                                               (3, 3, '출근: 회의 준비', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), ' 09:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), ' 09:00:00')),
                                                                                               (3, 4, '퇴근: 회의가 길었음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 28 DAY), ' 18:00:00')),
                                                                                               (4, 2, '출근: 날씨 좋음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 27 DAY), ' 08:45:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 27 DAY), ' 08:45:00')),
                                                                                               (4, 3, '퇴근: 보고서 작성 중', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 27 DAY), ' 18:30:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 27 DAY), ' 18:30:00')),
                                                                                               (5, 4, '출근: 피곤함', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 26 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 26 DAY), ' 08:50:00')),
                                                                                               (5, 5, '퇴근: 금요일 야근', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 26 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 26 DAY), ' 18:00:00')),
                                                                                               (6, 1, '출근: 늦잠', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), ' 09:10:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), ' 09:10:00')),
                                                                                               (6, 2, '퇴근: 주말 잘 쉬자', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), ' 18:00:00')),
                                                                                               (7, 2, '출근: 상쾌함', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 24 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 24 DAY), ' 08:50:00')),
                                                                                               (7, 2, '퇴근: 집중 잘됨', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 24 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 24 DAY), ' 18:00:00')),
                                                                                               (8, 2, '출근: 커피 한잔', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 23 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 23 DAY), ' 08:50:00')),
                                                                                               (8, 3, '퇴근: 무난한 하루', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 23 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 23 DAY), ' 18:00:00')),
                                                                                               (9, 3, '출근: 긴급 이슈 확인', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), ' 08:55:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), ' 08:55:00')),
                                                                                               (9, 5, '퇴근: 에러 해결하느라 늦음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), ' 19:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 22 DAY), ' 19:00:00')),
                                                                                               (10, 4, '출근: 어제 여파로 피곤', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 21 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 21 DAY), ' 08:50:00')),
                                                                                               (10, 3, '퇴근: 일찍 자야지', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 21 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 21 DAY), ' 18:00:00')),
                                                                                               (11, 2, '출근: 일찍 도착', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), ' 08:40:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), ' 08:40:00')),
                                                                                               (11, 2, '퇴근: 컨디션 회복', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), ' 18:00:00')),
                                                                                               (12, 2, '출근: 점심 약속 기대', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 19 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 19 DAY), ' 08:50:00')),
                                                                                               (12, 3, '퇴근: 배부르고 졸림', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 19 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 19 DAY), ' 18:00:00')),
                                                                                               (13, 2, '출근: 버스 놓침', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), ' 09:05:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), ' 09:05:00')),
                                                                                               (13, 4, '퇴근: 오후 업무 밀림', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), ' 18:00:00')),
                                                                                               (14, 4, '출근: 마감일 임박', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 17 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 17 DAY), ' 08:50:00')),
                                                                                               (14, 5, '퇴근: 압박감 심함', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 17 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 17 DAY), ' 18:00:00')),
                                                                                               (15, 5, '출근: 스트레스 최고조', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), ' 08:50:00')),
                                                                                               (15, 4, '퇴근: 겨우 마감', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), ' 18:00:00')),
                                                                                               (16, 2, '출근: 주말 지나고 회복', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), ' 08:30:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), ' 08:30:00')),
                                                                                               (16, 2, '퇴근: 칼퇴', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), ' 17:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 15 DAY), ' 17:00:00')),
                                                                                               (17, 2, '출근: 여유로움', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), ' 08:50:00')),
                                                                                               (17, 2, '퇴근: 평온', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), ' 18:00:00')),
                                                                                               (18, 3, '출근: 회의 준비', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 13 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 13 DAY), ' 08:50:00')),
                                                                                               (18, 4, '퇴근: 의견 충돌 있었음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 13 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 13 DAY), ' 18:00:00')),
                                                                                               (19, 3, '출근: 어제 일 수습', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), ' 08:50:00')),
                                                                                               (19, 3, '퇴근: 잘 해결됨', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), ' 18:00:00')),
                                                                                               (20, 2, '출근: 금요일이다', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), ' 08:50:00')),
                                                                                               (20, 1, '퇴근: 주말 시작', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), ' 18:00:00')),
                                                                                               (21, 1, '출근: 월요병 없음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), ' 08:50:00')),
                                                                                               (21, 2, '퇴근: 시작이 좋음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), ' 18:00:00')),
                                                                                               (22, 2, '출근: 새 프로젝트', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 9 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 9 DAY), ' 08:50:00')),
                                                                                               (22, 3, '퇴근: 할 일 많음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 9 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 9 DAY), ' 18:00:00')),
                                                                                               (23, 3, '출근: 일정 타이트함', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), ' 09:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), ' 09:00:00')),
                                                                                               (23, 4, '퇴근: 야근 각', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 8 DAY), ' 18:00:00')),
                                                                                               (24, 4, '출근: 피로 누적', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 08:50:00')),
                                                                                               (24, 5, '퇴근: 너무 힘들다', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), ' 18:00:00')),
                                                                                               (25, 3, '출근: 그냥저냥', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), ' 08:50:00')),
                                                                                               (25, 3, '퇴근: 내일은 주말', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), ' 18:00:00')),
                                                                                               (26, 2, '출근: 날씨 좋음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), ' 08:50:00')),
                                                                                               (26, 2, '퇴근: 기분 좋음', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), ' 18:00:00')),
                                                                                               (27, 2, '출근: 집중력 최고', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), ' 08:50:00')),
                                                                                               (27, 1, '퇴근: 뿌듯함', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 4 DAY), ' 18:00:00')),
                                                                                               (28, 2, '출근: 약간 지침', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), ' 08:50:00')),
                                                                                               (28, 3, '퇴근: 충전 필요', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), ' 18:00:00')),
                                                                                               (29, 3, '출근: 이슈 발생', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 08:50:00')),
                                                                                               (29, 4, '퇴근: 처리하느라 진땀', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), ' 18:00:00')),
                                                                                               (30, 2, '출근: 상쾌한 아침', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 08:50:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 08:50:00')),
                                                                                               (30, 2, '퇴근: 내일도 화이팅', CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 18:00:00'), CONCAT(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), ' 18:00:00'));

-- 11. 포인트 내역 (mission_id 1, 5가 있으므로 mission_list insert가 선행되어야 함)
INSERT INTO point_history (point_type, amount, balance_after, source_type, member_id, gifticon_id, mission_id, create_date) VALUES
                                                                                                                                ('EARN', 1000, 48000, 'ATTENDANCE', 1, NULL, 1, NOW()),
                                                                                                                                ('SPEND', 4500, 43500, 'GIFTICON', 1, 1, NULL, NOW()),
                                                                                                                                ('EARN', 5000, 20000, 'EVENT', 2, NULL, NULL, NOW()),
                                                                                                                                ('EARN', 100, 5100, 'ATTENDANCE', 3, NULL, 5, NOW()),
                                                                                                                                ('EARN', 10000, 110000, 'PROJECT', 4, NULL, NULL, NOW());

-- 12. 주문 내역 (수정됨: earn_point, spend_point, type 컬럼 추가)
INSERT INTO gift_order (period, order_date, approval_amount, member_id, gifticon_id, earn_point, spend_point, type) VALUES
                                                                                                                        (30, CURRENT_DATE, 4500, 1, 1, 0, 4500, 'SPEND'),
                                                                                                                        (60, DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 8900, 2, 2, 0, 8900, 'SPEND'),
                                                                                                                        (365, DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), 10000, 4, 3, 0, 10000, 'SPEND'),
                                                                                                                        (90, DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), 20000, 1, 4, 0, 20000, 'SPEND'),
                                                                                                                        (30, DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 4500, 3, 1, 0, 4500, 'SPEND');