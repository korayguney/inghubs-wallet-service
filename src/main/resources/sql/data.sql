INSERT INTO customer (id, username, password, firstname, lastname, tckn, created_date, created_by)
VALUES (1, 'customer1', '{bcrypt}$2a$10$/FFxtj2BeQziZZIO0pA18e2st8V4W.kaJbqIbrYL1dJHtlINRQwBK', 'Ayse', 'Turkmen', '12345678901', CURRENT_TIMESTAMP, 'system');

INSERT INTO customer (id, username, password, firstname, lastname, tckn, created_date, created_by)
VALUES (2, 'customer2', '{bcrypt}$2a$10$/FFxtj2BeQziZZIO0pA18e2st8V4W.kaJbqIbrYL1dJHtlINRQwBK', 'Ali', 'Kaya', '98765432101', CURRENT_TIMESTAMP, 'system');

INSERT INTO employee (id, username, password, firstname, lastname, department, created_date, created_by)
VALUES (1, 'admin', '{bcrypt}$2a$10$/FFxtj2BeQziZZIO0pA18e2st8V4W.kaJbqIbrYL1dJHtlINRQwBK', 'Emre', 'Toprak', 'IT', CURRENT_TIMESTAMP, 'system');
