import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL =  'http://localhost:8089/user-service/login/portal';
const TOTAL_SEEDED_USERS = 250;
const SEEDED_PASSWORD = '12345';

export const options = {
    scenarios: {
      login_LoadTest: {
        executor: 'constant-vus',
        vus: 100,
        duration: '30s',
      }
    }
};

export default function loginLoadTest () {
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

  console.log(`Status: ${res.status}, Body: ${res.body}`);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

  sleep(1);
}