CREATE TABLE IF NOT EXISTS roles_privileges(
    role_id INTEGER REFERENCES roles(id),
    privilege_id INTEGER REFERENCES privileges(id)
);