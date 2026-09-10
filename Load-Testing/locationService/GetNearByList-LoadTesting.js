import http from 'k6/http';
import { check, sleep } from 'k6';

const GATEWAY = 'http://localhost:8089';
const LOGIN_URL = `${GATEWAY}/user-service/login/portal`;
const DISCOVERY_URL = `${GATEWAY}/orchestration/discovery`;

const TOTAL_SEEDED_USERS = 250;
const SEEDED_PASSWORD = '123456789';

const CENTER_LAT = 24.8607;
const CENTER_LNG = 67.0011;
const SPREAD_RADIUS_KM = 20;

export const options = {
  scenarios: {
    login_FetchUserTest: {
      "executor": "constant-vus",
      "vus": 1,
      "duration": "2s",
      // "exec": "loginSmokeTest"
    },
    // load_test: {
    //   executor: 'ramping-vus',
    //   startVUs: 0,
    //   stages: [
    //     { duration: '10s', target: 50 },
    //     { duration: '10s', target: 50 },
    //     { duration: '20s', target: 0 },
    //   ],
    // },
  },
  thresholds: {
    'http_req_duration': ['p(90)<2550', 'p(95)<2600'],
    'http_req_failed': ['rate<0.01'],
  },
};

// per-VU state — set once, reused across all iterations of this VU
let token = null;
let email = null;
let coords = null;

function randomPointNear(centerLat, centerLng, radiusKm) {
  const r = radiusKm * Math.sqrt(Math.random());
  const theta = Math.random() * 2 * Math.PI;
  const dLat = (r / 111.32) * Math.sin(theta);
  const dLng = (r / (111.32 * Math.cos(centerLat * Math.PI / 180))) * Math.cos(theta);
  return {
    latitude: +(centerLat + dLat).toFixed(6),
    longitude: +(centerLng + dLng).toFixed(6),
  };
}

function login() {
  const userIndex = Math.floor(Math.random() * TOTAL_SEEDED_USERS) + 1;
  email = `loadtest${userIndex}@test.com`;

  const payload = JSON.stringify({ email, password: SEEDED_PASSWORD });
  const params = { headers: { 'Content-Type': 'application/json' } };

  const res = http.post(LOGIN_URL, payload, params);

  check(res, { 'login status is 200': (r) => r.status === 200 });

  token = res.body;
}

export default function locationLoadTest() {
  if (!token) {
    login();
  }

  if (!coords) {
    coords = randomPointNear(CENTER_LAT, CENTER_LNG, SPREAD_RADIUS_KM);
  }

  const discoveryPayload = JSON.stringify({
    latitude: coords.latitude,
    longitude: coords.longitude,
  });

  const discoveryParams = {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(DISCOVERY_URL, discoveryPayload, discoveryParams);

  check(res, { 'discovery status is 200': (r) => r.status === 200 });

  const list = res.json('nearby_user_ids');
  console.log(`${email} @ (${coords.latitude}, ${coords.longitude}) -> ${list ? list.length : 0} nearby users -- and JWT is ${token}` );

  sleep(1);
}