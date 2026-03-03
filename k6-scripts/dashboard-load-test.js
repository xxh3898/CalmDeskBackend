import http from 'k6/http';
import { check, sleep } from 'k6';

// 테스트 옵션 (부하 단계 설정)
export const options = {
    stages: [
        { duration: '30s', target: 50 }, // 30초 동안 50명의 가상 유저(VUs)로 증가
        { duration: '1m', target: 50 },  // 1분 동안 50명 유지
        { duration: '30s', target: 0 },  // 30초 동안 0명으로 감소
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이내에 완료되어야 함
        http_req_failed: ['rate<0.01'],   // 에러율이 1% 미만이어야 함
    },
};

// URL 설정 (환경 변수로 덮어쓰기 가능)
const BASE_URL = __ENV.K6_BASE_URL || 'http://localhost:8080';

// 테스트용 더미 계정 정보 (data.sql 기반)
const TEST_EMAIL = __ENV.K6_TEST_EMAIL || 'user4@test.com'; 
const TEST_PASSWORD = __ENV.K6_TEST_PASSWORD || 'pass1234!';

export function setup() {
    // 1. 단 1번 실행되는 setup(): 로그인하여 토큰 획득
    const loginPayload = JSON.stringify({
        email: TEST_EMAIL,
        password: TEST_PASSWORD,
    });

    const loginHeaders = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload, loginHeaders);

    check(loginRes, {
        'login successful': (r) => r.status === 200,
    });

    // 응답에서 accessToken 속성 추출 
    // (만약 응답 형식이 다르면 r.json('token') 등으로 변경해야 함)
    let token = loginRes.json('accessToken') || loginRes.json('token');

    // Spring Security JWT 필터용 토큰 prefix 추가
    if (token && !token.startsWith('Bearer ')) {
        token = `Bearer ${token}`;
    }

    return { token: token };
}

export default function (data) {
    if (!data.token) {
        console.error('로그인 실패로 토큰이 없습니다. 테스트를 종료합니다.');
        return;
    }

    const params = {
        headers: {
            'Authorization': data.token,
            'Content-Type': 'application/json',
        },
    };

    // 2. 가상 유저(VUs)들이 반복적으로 호출할 메인 타겟 API
    // N+1 문제 해결 (Fetch Join 등) 성능 개선을 명확히 측정할 수 있는 멤버 목록 조회 API
    const res = http.get(`${BASE_URL}/api/admin/team/members?page=0&size=50`, params);

    check(res, {
        'members list status is 200': (r) => r.status === 200,
    });

    // 다음 요청 전 1초 대기하여 적정 부하 유지
    sleep(1);
}
