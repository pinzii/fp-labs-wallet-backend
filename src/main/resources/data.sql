INSERT INTO app_user (id, external_auth_id, email, first_name, last_name, legal_id_type, legal_id_number, status)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'auth|123456', 'felipe.pinzon@example.com', 'Felipe', 'Pinzón', 'CC', '10203040', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO account (id, user_id, balance, currency, version)
VALUES 
('11111111-1111-4111-a111-111111111111', '550e8400-e29b-41d4-a716-446655440000', 1000.0000, 'USD', 0),
('22222222-2222-4222-a222-222222222222', '550e8400-e29b-41d4-a716-446655440000', 500.0000, 'USD', 0)
ON CONFLICT (id) DO NOTHING;