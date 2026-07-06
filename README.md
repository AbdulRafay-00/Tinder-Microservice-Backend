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
