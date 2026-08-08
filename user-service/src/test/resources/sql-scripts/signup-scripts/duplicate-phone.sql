INSERT INTO auth_credentials (user_id, user_email, user_password)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'existing@example.com',
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
    '22222222-2222-2222-2222-222222222222',
    'Existing User',
    '03001234567',
    25,
    'photo.jpg',
    'Existing User',
    'Male',
    'Karachi'
);