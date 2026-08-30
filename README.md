Detail Preview of the entire app 
𝗦𝗬𝗦𝗧𝗘𝗠 𝗗𝗘𝗦𝗜𝗚𝗡 𝗗𝗜𝗔𝗚𝗥𝗔𝗠 𝗛𝗔𝗡𝗗 𝗗𝗥𝗔𝗪𝗡

<h2 align="center">System Design</h2>

<p align="center">
  <img src="https://github.com/user-attachments/assets/14a8b905-464b-48a8-abc3-6e02353d0883" width="1000">
</p>


𝗕𝗿𝗲𝗮𝗸𝗻𝗼𝘄𝗻 𝗼𝗳 𝗲𝗮𝗰𝗵 𝗠𝗶𝗰𝗿𝗼𝘀𝗲𝗿𝘃𝗶𝗰𝗲
𝟭) 𝗨𝘀𝗲𝗿-𝗦𝗲𝗿𝘃𝗶𝗱𝗲
Handel's the user data and login event

𝟮)𝗟𝗼𝗰𝗮𝘁𝗶𝗼𝗻 𝗦𝗲𝗿𝘃𝗶𝗰𝗲

- Stores the user's current latitude and longitude.
- Indexes user locations in Redis using GeoHash.
- Performs radius-based searches (e.g., within 10 km).
- Returns the list of nearby user IDs to the Orchestration Service.
- Does not cache search results; caching is handled by the Orchestration Service.

𝟯) 𝗢𝗿𝗰𝗵𝗲𝘀𝘁𝗿𝗮𝘁𝗶𝗼𝗻-𝗦𝗲𝗿𝘃𝗶𝗰𝗲

𝟰) 𝗣𝗮𝗶𝗿𝗶𝗻𝗴-𝗦𝗲𝗿𝘃𝗶𝗰𝗲

𝟱) 𝗠𝗮𝘁𝗰𝗵-𝗦𝗲𝗿𝘃𝗶𝗰𝗲
𝟲) 𝗡𝗼𝘁𝗶𝗳𝗶𝗰𝗮𝘁𝗶𝗼𝗻-𝗦𝗲𝗿𝘃𝗶𝗰𝗲 

𝟳) 𝗞𝗮𝗳𝗸𝗮-𝗖𝗼𝗻𝘁𝗮𝗶𝗻𝗲𝗿
shared network across all containers

𝟴) 𝗘𝘂𝗿𝗲𝗸𝗮-𝗦𝗲𝗿𝘃𝗶𝗰𝗲

9) DISTRIBUTED SQL
10) DOCKER SERVICE

11) role of redis

12) role of AWS SES

explanation of each part


OUTPUT
Swiper
img placeholder

# Load Testing

## Performance Optimization: Fixing a Redundant Auth Query

Load testing the login endpoint with k6 surfaced a hidden inefficiency: every request was querying auth_credentials twice — once internally during Spring Security authentication, and again to re-fetch the same user for JWT generation. The fix reused the already-authenticated principal instead of re-querying, cutting the login flow down to a single database call.

| Metric | Before | After |
|--------|--------|-------|
| Avg Latency | 1.00s | 597ms |
| p95 Latency | 1.29s | 804ms |
| Throughput | 20.06 req/s | 24.97 req/s |


## Performance Discovery: Connection Pool Exhaustion and Hashing Overhead Under Stress

Stress testing at higher concurrency (100 VUs) surfaced two additional bottlenecks beyond the query fix: the HikariCP connection pool was exhausting under load, causing requests to queue and eventually time out, and password hashing overhead was adding significant CPU cost per login at high concurrency. Increasing the pool's maximum size and tuning the hashing cost factor resolved both issues.

| Metric | Before | After | Change |
|---|---|---|---|
| Avg Latency | 597ms | 151.01ms | ↓ 74.7% |
| p95 Latency | 804ms | 249.39ms | ↓ 69.0% |
| Throughput | 24.97 req/s | 33.64 req/s | ↑ 34.7% |
| Failure Rate | 0.00% | 0.00% | — |


swiped
img placeholder


Future Expention ideas
