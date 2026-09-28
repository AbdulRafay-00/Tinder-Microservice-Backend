// import http from 'k6/http';
// import { check, sleep } from 'k6';

// const GATEWAY = 'http://localhost:8089';
// const LOGIN_URL = `${GATEWAY}/user-service/login/portal`;
// const DISCOVERY_URL = `${GATEWAY}/orchestration/discovery`;

// const TOTAL_SEEDED_USERS = 250;
// const SEEDED_PASSWORD = '123456789';

// // Multiple density zones — dense urban core, moderate suburbs, sparse rural.
// const ZONES = [
//     { name: 'downtown', lat: 24.8607, lng: 67.0011, radiusKm: 10, weight: 50 },
//     { name: 'suburb-a', lat: 24.9200, lng: 67.0800, radiusKm: 20, weight: 25 },
//     { name: 'suburb-b', lat: 24.8000, lng: 66.9700, radiusKm: 20, weight: 15 },
//     { name: 'rural', lat: 25.0500, lng: 67.1500, radiusKm: 50, weight: 10 },
// ];

// function pickZone() {
//     const totalWeight = ZONES.reduce((sum, z) => sum + z.weight, 0);
//     let r = Math.random() * totalWeight;
//     for (const zone of ZONES) {
//         if (r < zone.weight) return zone;
//         r -= zone.weight;
//     }
//     return ZONES[0];
// }

// export const options = {
//     scenarios: {
//         discovery_stress_test: {
//             executor: 'ramping-vus',
//             startVUs: 0,
//             stages: [
//                 { duration: '30s', target: 100 },  // ramp to known-good baseline
//                 { duration: '1m', target: 300 },  // push past baseline
//                 { duration: '1m', target: 300 },  // hold at 300 to see if it stabilizes or degrades
//                 { duration: '1m', target: 500 },  // push further — find the breaking point
//                 { duration: '1m', target: 500 },  // hold at 500
//                 { duration: '30s', target: 0 },    // ramp down
//             ],
//         },
//     },
//     thresholds: {
//         'http_req_failed': ['rate<0.20'],
//     },
// };

// let token = null;
// let email = null;
// let coords = null;

// function randomPointNear(centerLat, centerLng, radiusKm) {
//     const r = radiusKm * Math.sqrt(Math.random());
//     const theta = Math.random() * 2 * Math.PI;
//     const dLat = (r / 111.32) * Math.sin(theta);
//     const dLng = (r / (111.32 * Math.cos(centerLat * Math.PI / 180))) * Math.cos(theta);
//     return {
//         latitude: +(centerLat + dLat).toFixed(6),
//         longitude: +(centerLng + dLng).toFixed(6),
//     };
// }

// function login() {
//     const userIndex = Math.floor(Math.random() * TOTAL_SEEDED_USERS) + 1;
//     email = `loadtest${userIndex}@test.com`;

//     const payload = JSON.stringify({ email, password: SEEDED_PASSWORD });
//     const params = { headers: { 'Content-Type': 'application/json' } };

//     const res = http.post(LOGIN_URL, payload, params);

//     check(res, { 'login status is 200': (r) => r.status === 200 });

//     token = res.body;
// }

// export default function discoveryStressTest() {
//     if (!token) {
//         login();
//     }

//     if (!coords) {
//         const zone = pickZone();
//         coords = randomPointNear(zone.lat, zone.lng, zone.radiusKm);
//     }

//     const discoveryPayload = JSON.stringify({
//         latitude: coords.latitude,
//         longitude: coords.longitude,
//     });

//     const discoveryParams = {
//         headers: {
//             Authorization: `Bearer ${token}`,
//             'Content-Type': 'application/json',
//         },
//     };

//     const res = http.post(DISCOVERY_URL, discoveryPayload, discoveryParams);

//     const ok = check(res, { 'discovery status is 200': (r) => r.status === 200 });

//     if (ok) {
//         const list = res.json('nearby_user_ids');
//         console.log(`${email} @ (${coords.latitude}, ${coords.longitude}) -> ${list ? list.length : 0} nearby users`);
//     } else {
//         console.log(`${email} -> FAILED status=${res.status} body=${res.body}`);
//     }

//     sleep(1);
// }





import http from 'k6/http';
import { check, sleep } from 'k6';

const GATEWAY = 'http://localhost:8089';
const LOGIN_URL = `${GATEWAY}/user-service/login/portal`;
const DISCOVERY_URL = `${GATEWAY}/orchestration/discovery`;

const TOTAL_SEEDED_USERS = 250;
const SEEDED_PASSWORD = '123456789';

// Users are simulated as being near one of several real Pakistani cities —
// each city uses the app's actual fixed 15km search radius. This models
// real usage (population naturally clustered around cities) without
// inventing any density logic the app itself doesn't have.
const SEARCH_RADIUS_KM = 15; // matches the app's actual fixed search radius

const CITIES = [
    { name: 'Karachi', lat: 24.8607, lng: 67.0011 },
    { name: 'Lahore', lat: 31.5497, lng: 74.3436 },
    { name: 'Islamabad', lat: 33.6844, lng: 73.0479 },
    { name: 'Rawalpindi', lat: 33.5651, lng: 73.0169 },
];

function pickCity() {
    return CITIES[Math.floor(Math.random() * CITIES.length)];
}

export const options = {
    scenarios: {
        discovery_stress_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 100 },  // ramp to known-good baseline
                { duration: '1m', target: 300 },  // push past baseline
                { duration: '1m', target: 300 },  // hold at 300 to see if it stabilizes or degrades
                { duration: '1m', target: 500 },  // push further — find the breaking point
                { duration: '1m', target: 500 },  // hold at 500
                { duration: '30s', target: 0 },    // ramp down
            ],
        },
    },
    thresholds: {

        'http_req_failed': ['rate<0.20'],
    },
};

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

export default function discoveryStressTest() {
    if (!token) {
        login();
    }

    if (!coords) {
        const city = pickCity();
        coords = randomPointNear(city.lat, city.lng, SEARCH_RADIUS_KM);
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

    const ok = check(res, { 'discovery status is 200': (r) => r.status === 200 });

    if (ok) {
        const list = res.json('nearby_user_ids');
        console.log(`${email} @ (${coords.latitude}, ${coords.longitude}) -> ${list ? list.length : 0} nearby users`);
    } else {
        console.log(`${email} -> FAILED status=${res.status} body=${res.body}`);
    }

    sleep(1);
}