INSERT INTO tb_role (authority) VALUES (1);
INSERT INTO tb_role (authority) VALUES (2);

INSERT INTO tb_user (name, email, password) VALUES ('Alex', 'alex@gmail.com', '123');
INSERT INTO tb_user (name, email, password) VALUES ('Bob', 'bob@gmail.com', '123');
INSERT INTO tb_user (name, email, password) VALUES ('Maria', 'maria@gmail.com', '123');
INSERT INTO tb_user (name, email, password) VALUES ('Geraldo', 'geraldo@gmail.com', '123');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 2);

INSERT INTO tb_order (product_name, quantity, price, status, user_id) VALUES ('Computador', 1, 1000.00, 'PENDING', 1);
INSERT INTO tb_order (product_name, quantity, price, status, user_id) VALUES ('Sapato', 1, 250.00, 'PRODUCING', 3);
INSERT INTO tb_order (product_name, quantity, price, status, user_id) VALUES ('Ventilador', 1, 25.00, 'PRODUCING', 3);
INSERT INTO tb_order (product_name, quantity, price, status, user_id) VALUES ('Omega3', 1, 14.00, 'SENDING', 3);
INSERT INTO tb_order (product_name, quantity, price, status, user_id) VALUES ('Cafe', 1, 999.00, 'SENDING', 1);
INSERT INTO tb_order (product_name, quantity, price, status, user_id) VALUES ('Mesa', 1, 320.00, 'DELIVERED', 1);
INSERT INTO tb_order (product_name, quantity, price, status, user_id) VALUES ('Cadeira', 1, 790.00, 'DELIVERED', 1);

