CREATE TABLE IF NOT EXISTS users_roles(
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    role_id INTEGER REFERENCES roles(id)
);