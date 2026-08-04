INSERT INTO auth_credentials (user_id, user_email, user_password)
VALUES (
    '33333333-3333-3333-3333-333333333333',
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
    '33333333-3333-3333-3333-333333333333',
    'Test User',
    '03999999999',
    25,
    'photo.jpg',
    'Existing User',
    'Male',
    'Karachi'
);