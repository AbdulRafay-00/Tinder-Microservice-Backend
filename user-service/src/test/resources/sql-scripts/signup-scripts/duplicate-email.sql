INSERT INTO auth_credentials (user_id, user_email, user_password)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'test@example.com',
    '$2b$10$RGVZ2U9ROTMddQiRHRX4pe95cGPJbCRhyKTw8MsZOYeCTP.UR0.L.'
);

INSERT INTO user_profiledb (
    user_id,
    name,
    phone_number,
    age,
    photo_url,
    bio,
    gender,
    location
)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Existing User',
    '03999999999',
    25,
    'photo.jpg',
    'Existing User',
    'Male',
    'Karachi'
);