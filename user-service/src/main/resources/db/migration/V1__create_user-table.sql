CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY key,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    phoneNumber TEXT
);