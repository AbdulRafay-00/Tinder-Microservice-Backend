import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = "http://localhost:8089/user-service/login/portal";
const TOTAL_SEEDED_USERS = 250;
const SEEDED_PASSWORD = "12345";

export const options = {
  scenarios: {
    stress_test: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '1m', target: 100 },  // baseline — confirmed load-test peak
        { duration: '2m', target: 100 },  // hold at baseline
        { duration: '1m', target: 300 },  // push to 3x — stress zone
        { duration: '3m', target: 300 },  // hold at stress level
        { duration: '1m', target: 500 },  // push to 5x — find the breaking point
        { duration: '3m', target: 500 },  // hold — stabilize or keep degrading?
        { duration: '1m', target: 0 },    // ramp down — recovery check
      ],
    },
  },
  thresholds: {
    'http_req_duration': [
      'p(90)<3500',
      'p(95)<4000',
    ],
    'http_req_failed': ['rate<0.05'],
  }
}

export function loginStressTest() {
  const userIndex = Math.floor(Math.random() * TOTAL_SEEDED_USERS) + 1;
  const email = `loadtest${userIndex}@test.com`;

  const payload = JSON.stringify({
    email: email,
    password: SEEDED_PASSWORD,
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  const res = http.post(`${BASE_URL}`, payload, params);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}

export default loginStressTest;