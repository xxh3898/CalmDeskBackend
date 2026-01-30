/*
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE point_history;
TRUNCATE TABLE gift_order;
TRUNCATE TABLE gifticon;
TRUNCATE TABLE account;
TRUNCATE TABLE stress_summary;
TRUNCATE TABLE emotion_checkin;
TRUNCATE TABLE vacation_rest;
TRUNCATE TABLE consultation;
TRUNCATE TABLE attendance;
TRUNCATE TABLE member;
TRUNCATE TABLE member_rank;
TRUNCATE TABLE department;
TRUNCATE TABLE company;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. 회사
INSERT INTO company (
    company_name,
    company_code,
    category,
    min_value,
    max_value
) VALUES
      ('칼름데스크', 'CALMDESK', 'IT', 0, 100000),
      ('테크솔루션', 'TECHSOL', 'IT', 0, 100000),
      ('퓨처인사이트', 'FUTURE', 'CONSULTING', 0, 50000),
      ('그린에너지', 'GREEN', 'ENERGY', 0, 80000),
      ('스마트물류', 'LOGIS', 'LOGISTICS', 0, 70000);

-- 2. 부서
INSERT INTO department (
    department_name,
    company_id
) VALUES
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
INSERT INTO member (
    name,
    email,
    password,
    phone,
    role,
    status,
    join_date,
    company_id,
    department_id,
    rank_id,
    created_date,
    modify_date
) VALUES
      (
          '김철수',
          'user1@test.com',
          '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa',
          '010-1111-1111',
          'EMPLOYEE',
          'Y',
          '2023-01-01',
          1, 1, 3,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          '이영희',
          'user2@test.com',
          '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa',
          '010-2222-2222',
          'EMPLOYEE',
          'Y',
          '2023-02-15',
          1, 2, 2,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          '박민수',
          'user3@test.com',
          '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa',
          '010-3333-3333',
          'EMPLOYEE',
          'Y',
          '2023-03-20',
          1, 3, 1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          '최지현',
          'user4@test.com',
          '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa',
          '010-4444-4444',
          'ADMIN',
          'Y',
          '2022-12-01',
          1, 1, 4,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          '정우성',
          'user5@test.com',
          '$2a$10$f/eYobDQgSJInVvSDhCghOTh2jqSdwIW72jAHa2TQRJUgrZ5RYsRa',
          '010-5555-5555',
          'EMPLOYEE',
          'Y',
          '2024-01-10',
          1, 2, 1,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      );

-- 5. 계좌
INSERT INTO account (
    member_id,
    remaining_point,
    total_earned,
    total_spent
) VALUES
      (1, 47000, 97000, 50000),
      (2, 15000, 20000, 5000),
      (3, 5000, 5000, 0),
      (4, 100000, 200000, 100000),
      (5, 0, 0, 0);

-- 6. 휴가
INSERT INTO vacation_rest (
    rest_id,
    total_count,
    spent_count,
    member_id
) VALUES
      (1, 15, 5, 1),
      (2, 15, 2, 2),
      (3, 12, 0, 3),
      (4, 20, 10, 4),
      (5, 12, 1, 5);

-- 7. 스트레스
INSERT INTO stress_summary (
    member_id,
    score,
    avg_stress,
    period,
    start_time,
    end_time
) VALUES
      (1, 45, 40, 'WEEKLY', CURRENT_TIMESTAMP - INTERVAL 7 DAY, CURRENT_TIMESTAMP),
      (2, 60, 55, 'WEEKLY', CURRENT_TIMESTAMP - INTERVAL 7 DAY, CURRENT_TIMESTAMP),
      (3, 30, 25, 'WEEKLY', CURRENT_TIMESTAMP - INTERVAL 7 DAY, CURRENT_TIMESTAMP),
      (4, 80, 75, 'WEEKLY', CURRENT_TIMESTAMP - INTERVAL 7 DAY, CURRENT_TIMESTAMP),
      (5, 50, 50, 'WEEKLY', CURRENT_TIMESTAMP - INTERVAL 7 DAY, CURRENT_TIMESTAMP);

-- 8. 근태
INSERT INTO attendance (
    work_date,
    check_in,
    check_out,
    attendance_status,
    member_id
) VALUES
      (CURRENT_DATE, '2024-01-29 09:00:00', NULL, 'ATTEND', 1),
      (DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), '2024-01-28 08:50:00', '2024-01-28 18:00:00', 'ATTEND', 1),
      (DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), '2024-01-27 08:55:00', '2024-01-27 18:00:00', 'ATTEND', 1),
      (CURRENT_DATE, '2024-01-29 08:45:00', NULL, 'ATTEND', 2),
      (CURRENT_DATE, '2024-01-29 09:10:00', NULL, 'LATE', 3);

-- 9. 기프티콘
INSERT INTO gifticon (
    gifticon_name,
    image,
    price,
    stock_quantity,
    period,
    status
) VALUES
      ('스타벅스 아메리카노', 'https://via.placeholder.com/150', 4500, 100, 30, 'Y'),
      ('배스킨라빈스 파인트', 'https://via.placeholder.com/150', 8900, 50, 60, 'Y'),
      ('문화상품권 1만원권', 'https://via.placeholder.com/150', 10000, 30, 365, 'Y'),
      ('치킨 세트', 'https://via.placeholder.com/150', 20000, 10, 90, 'Y'),
      ('영화 관람권', 'https://via.placeholder.com/150', 12000, 40, 180, 'Y');

-- 10. 상담
INSERT INTO consultation (
    title,
    description,
    status,
    member_id,
    created_date,
    modify_date
) VALUES
      ('업무 스트레스 상담', '최근 업무량이 늘어 힘듭니다.', 'WAITING', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('대인관계 고민', '동료와의 소통이 어렵습니다.', 'IN_PROGRESS', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('진로 상담', '커리어 패스에 대해 고민입니다.', 'WAITING', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      ('건강 문제', '건강상 이유로 휴직을 고려 중입니다.', 'COMPLETED', 1,
       DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 MONTH),
       DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 MONTH)),
      ('적응 문제', '신입 사원으로서 적응이 어렵습니다.', 'WAITING', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 11. 포인트
INSERT INTO point_history (
    point_type,
    amount,
    balance_after,
    source_type,
    member_id,
    gifticon_id,
    mission_id,
    create_date
) VALUES
      ('EARN', 1000, 48000, 'ATTENDANCE', 1, NULL, 1, CURRENT_TIMESTAMP),
      ('SPEND', 4500, 43500, 'GIFTICON', 1, 1, NULL, CURRENT_TIMESTAMP),
      ('EARN', 5000, 20000, 'EVENT', 2, NULL, NULL, CURRENT_TIMESTAMP),
      ('EARN', 100, 5100, 'ATTENDANCE', 3, NULL, 5, CURRENT_TIMESTAMP),
      ('EARN', 10000, 110000, 'PROJECT', 4, NULL, NULL, CURRENT_TIMESTAMP);

-- 12. 주문
INSERT INTO gift_order (
    period,
    order_date,
    approval_amount,
    member_id,
    gifticon_id
) VALUES
      (30, CURRENT_DATE, 4500, 1, 1),
      (60, DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), 8900, 2, 2),
      (365, DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), 10000, 4, 3),
      (90, DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), 20000, 1, 4),
      (30, DATE_SUB(CURRENT_DATE, INTERVAL 2 DAY), 4500, 3, 1);*/
