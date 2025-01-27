INSERT INTO customer (customer_id, first_name, last_name, otp_lock, created_at, updated_at)
VALUES 
  ('6e6a7d4b-1eaa-4e23-9fd3-8b5d0840d3f1', 'John', 'Doe', 'NOT_LOCKED', NOW(), NOW()),
  ('7bde2d0a-743d-41c3-bd5b-f8590de9095c', 'Jane', 'Smith', 'NOT_LOCKED', NOW(), NOW());

INSERT INTO contact (contact_id, customer_id, contact_type, contact_value, created_at, updated_at)
VALUES 
  ('1e5b848f-b0a2-4c53-b911-21e07e2c45c2', '6e6a7d4b-1eaa-4e23-9fd3-8b5d0840d3f1', 'EMAIL', 'john.doe@example.com', NOW(), NOW()),
  ('2c1a6c2f-4f62-4d7a-b9b3-e3e117fe5c79', '6e6a7d4b-1eaa-4e23-9fd3-8b5d0840d3f1', 'PHONE', '+12345678901', NOW(), NOW()),
  ('b6f9f469-8088-476e-b6cb-7c2f194ce928', '7bde2d0a-743d-41c3-bd5b-f8590de9095c', 'EMAIL', 'jane.smith@example.com', NOW(), NOW()),
  ('cf3aef82-7f4d-4c82-810e-dcd07a0b9176', '7bde2d0a-743d-41c3-bd5b-f8590de9095c', 'PHONE', '+18765432101', NOW(), NOW());
