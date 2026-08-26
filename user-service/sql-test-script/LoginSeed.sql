CREATE TABLE IF NOT EXISTS auth_credentials (
    user_id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    user_password VARCHAR(72) NOT NULL
);

DELIMITER $$

CREATE PROCEDURE seed_loadtest_users()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 250 DO
        INSERT INTO auth_credentials (user_id, user_email, user_password)
        VALUES (
            UUID(),
            CONCAT('loadtest', i, '@test.com'),
            '$2a$12$ItfQKCvLZhVNlkLAfPCVhOKTTUSMms1tJWAmHuaRRCHDT4hf3qEOu'
        );
        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;

CALL seed_loadtest_users();
DROP PROCEDURE seed_loadtest_users;