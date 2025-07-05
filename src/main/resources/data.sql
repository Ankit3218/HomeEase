INSERT INTO service (title, description, price, image_url) VALUES
('AC Repair', 'Air conditioner installation and repair', 1299.00, '/images/ac.jpg'),
('Carpenter', 'Wood furniture repair and fitting', 799.00, '/images/carpenter.jpg'),
('Pest Control', 'Termite, cockroach and mosquito control', 899.00, '/images/pestcontrol.jpg'),
('Gardening', 'Lawn mowing and gardening maintenance', 599.00, '/images/gardening.jpg'),
('Painting', 'Wall painting, polishing and coating', 1499.00, '/images/painting.jpg'),
('Appliance Repair', 'Washing machine, fridge, and microwave repair', 999.00, '/images/appliances.jpg'),
('Bathroom Cleaning', 'Deep cleaning of bathroom tiles and fittings', 599.00, '/images/bathroom.jpg'),
('Kitchen Cleaning', 'Degreasing kitchen tiles, chimney and sink', 699.00, '/images/kitchen.jpg');

INSERT INTO admin (username, password) VALUES ('admin', 'admin123');
INSERT INTO role (name) VALUES ('ROLE_USER');
INSERT INTO role (name) VALUES ('ROLE_ADMIN');
