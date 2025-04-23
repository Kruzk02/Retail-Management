BEGIN TRANSACTION;

INSERT INTO roles(name) VALUES('ADMIN'), ('STAFF'), ('MANAGER') ON CONFLICT (name) DO NOTHING;
INSERT INTO privileges(name) VALUES('READ'), ('WRITE') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles_privileges(role_id, privilege_id) VALUES
    (1, 1), (1, 2),
    (2, 1),
    (3, 1), (3, 2);
COMMIT;