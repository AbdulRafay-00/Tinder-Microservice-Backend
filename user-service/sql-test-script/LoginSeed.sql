CREATE TABLE IF NOT EXISTS auth_credentials (
    user_id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    user_password VARCHAR(72) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_profiledb (
    user_id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    age INT NOT NULL,
    photo_url VARCHAR(255) NOT NULL,
    bio VARCHAR(500) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    location VARCHAR(255) NOT NULL
);

DELIMITER $$

CREATE PROCEDURE seed_loadtest_users()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE uid VARCHAR(36);
    WHILE i <= 250 DO
        SET uid = UUID();

        INSERT INTO auth_credentials (user_id, user_email, user_password)
        VALUES (uid, CONCAT('loadtest', i, '@test.com'),
                '$2a$12$ItfQKCvLZhVNlkLAfPCVhOKTTUSMms1tJWAmHuaRRCHDT4hf3qEOu');

        INSERT INTO user_profiledb (user_id, name, phone_number, age, photo_url, bio, gender, location)
        VALUES (uid, CONCAT('LoadUser', i), CONCAT('030000', LPAD(i, 5, '0')), 25,
                'https://placehold.co/200x200', 'Load test user', 'MALE', 'Karachi');

        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;

CALL seed_loadtest_users();
DROP PROCEDURE seed_loadtest_users;