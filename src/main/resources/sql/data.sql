INSERT INTO pizzas (pizza_name, base_price) VALUES
('Margherita', 8.50),
('Quatre Fromages', 10.50),
('Végétarienne', 9.50),
('Pepperoni', 9.00),
('Hawaïenne', 10.00),
('Calzone', 11.00),
('Napolitaine', 9.50),
('Quatre Saisons', 11.50),
('Mexicaine', 12.00),
('Carbonara', 11.50),
('Saumon', 13.50),
('BBQ Chicken', 12.50);

INSERT INTO ingredients (ingredient_name) VALUES
('Tomate'),
('Mozzarella'),
('Basilic'),
('Gorgonzola'),
('Parmesan'),
('Emmental'),
('Poivrons'),
('Champignons'),
('Oignons'),
('Olives'),
('Pepperoni'),
('Ananas'),
('Jambon'),
('Anchois'),
('Câpres'),
('Artichauts'),
('Bœuf haché'),
('Piment'),
('Maïs'),
('Crème fraîche'),
('Lardons'),
('Saumon fumé'),
('Poulet'),
('Sauce BBQ'),
('Oignon rouge');

INSERT INTO pizzas_ingredients (pizza_id, ingredient_id) VALUES
-- Margherita
(1, 1), -- Tomate
(1, 2), -- Mozzarella
(1, 3), -- Basilic

-- Quatre Fromages
(2, 1), -- Tomate
(2, 2), -- Mozzarella
(2, 4), -- Gorgonzola
(2, 5), -- Parmesan
(2, 6), -- Emmental

-- Végétarienne
(3, 1), -- Tomate
(3, 2), -- Mozzarella
(3, 7), -- Poivrons
(3, 8), -- Champignons
(3, 9), -- Oignons
(3, 10), -- Olives

-- Pepperoni
(4, 1), -- Tomate
(4, 2), -- Mozzarella
(4, 11), -- Pepperoni

-- Hawaïenne
(5, 1), -- Tomate
(5, 2), -- Mozzarella
(5, 12), -- Ananas
(5, 13), -- Jambon

-- Calzone
(6, 1), -- Tomate
(6, 2), -- Mozzarella
(6, 13), -- Jambon
(6, 8), -- Champignons

-- Napolitaine
(7, 1), -- Tomate
(7, 2), -- Mozzarella
(7, 14), -- Anchois
(7, 15), -- Câpres
(7, 10), -- Olives

-- Quatre Saisons
(8, 1), -- Tomate
(8, 2), -- Mozzarella
(8, 13), -- Jambon
(8, 8), -- Champignons
(8, 16), -- Artichauts
(8, 10), -- Olives

-- Mexicaine
(9, 1), -- Tomate
(9, 2), -- Mozzarella
(9, 17), -- Bœuf haché
(9, 7), -- Poivrons
(9, 18), -- Piment
(9, 19), -- Maïs

-- Carbonara
(10, 20), -- Crème fraîche
(10, 2), -- Mozzarella
(10, 21), -- Lardons
(10, 5), -- Parmesan
(10, 9), -- Oignons

-- Saumon
(11, 20), -- Crème fraîche
(11, 2), -- Mozzarella
(11, 22), -- Saumon fumé
(11, 9), -- Oignons

-- BBQ Chicken
(12, 24), -- Sauce BBQ
(12, 2), -- Mozzarella
(12, 23), -- Poulet
(12, 25), -- Oignon rouge
(12, 7); -- Poivrons

INSERT INTO delivery_drivers (first_name, last_name, phone_number) VALUES
('Jean', 'Dupont', '0612345678'),
('Marie', 'Laurent', '0623456789'),
('Thomas', 'Martin', '0634567890'),
('Sophie', 'Bernard', '0645678901'),
('Lucas', 'Petit', '0656789012');

INSERT INTO vehicles (vehicle_type, license_plate) VALUES
('CAR', 'AB-123-CD'),
('MOTORCYCLE', 'EF-456-GH'),
('CAR', 'IJ-789-KL'),
('MOTORCYCLE', 'MN-012-OP'),
('CAR', 'QR-345-ST');

INSERT INTO clients (first_name, last_name, client_address, phone_number, amount, loyalty_counter) VALUES
('Pierre', 'Dubois', '12 rue de Paris, 75001 Paris', '0711223344', 156.50, 3),
('Claire', 'Moreau', '24 avenue des Fleurs, 75002 Paris', '0722334455', 89.75, 1),
('Michel', 'Lefebvre', '7 rue du Commerce, 75015 Paris', '0733445566', 214.25, 4),
('Emilie', 'Leroy', '35 boulevard Saint-Michel, 75005 Paris', '0744556677', 45.00, 0),
('Julien', 'Roux', '18 rue de la République, 69001 Lyon', '0755667788', 178.90, 3),
('Camille', 'Simon', '42 rue Gambetta, 69003 Lyon', '0766778899', 129.40, 2),
('Antoine', 'Girard', '9 place Bellecour, 69002 Lyon', '0777889900', 67.25, 1),
('Lucie', 'Fontaine', '14 rue Victor Hugo, 69006 Lyon', '0788990011', 195.30, 3);

INSERT INTO orders (client_id, driver_id, vehicle_id, order_date, order_status, client_rating, free_reason) VALUES
(1, 1, 1, '2025-05-20 12:30:00', 'DELIVERED', 5, 'NOT_FREE'),
(2, 2, 2, '2025-05-19 19:15:00', 'DELIVERED', 4, 'NOT_FREE'),
(3, 3, 3, '2025-05-18 20:45:00', 'DELIVERED', 5, 'NOT_FREE'),
(4, 4, 4, '2025-05-17 18:30:00', 'DELIVERED', 3, 'NOT_FREE'),
(5, 5, 5, '2025-05-16 21:00:00', 'DELIVERED', 4, 'NOT_FREE'),
(1, 2, 2, '2025-05-15 19:45:00', 'DELIVERED', 5, 'LOYALTY'),
(3, 4, 4, '2025-05-14 20:15:00', 'DELIVERED', 2, 'LATE_DELIVERY'),
(6, 1, 1, '2025-05-21 11:30:00', 'IN_PROGRESS', NULL, 'NOT_FREE'),
(7, 3, 3, '2025-05-21 11:45:00', 'IN_PROGRESS', NULL, 'NOT_FREE'),
(8, NULL, NULL, '2025-05-21 12:00:00', 'PENDING', NULL, 'NOT_FREE'),
(2, NULL, NULL, '2025-05-21 12:15:00', 'PENDING', NULL, 'NOT_FREE'),
(5, NULL, NULL, '2025-05-20 19:30:00', 'CANCELED', NULL, 'NOT_FREE'),
(4, 5, 5, '2025-05-10 18:45:00', 'DELIVERED', 5, 'NOT_FREE'),
(6, 2, 2, '2025-05-09 20:30:00', 'DELIVERED', 4, 'NOT_FREE'),
(7, 1, 1, '2025-05-08 19:00:00', 'DELIVERED', 5, 'LOYALTY');

INSERT INTO order_pizzas (order_id, pizza_id, quantity, pizza_size, pizza_price, is_free) VALUES
-- Order 1: Margherita (Large) + Pepperoni (Medium)
(1, 1, 1, 'OGRESSE', 12.75, FALSE),
(1, 4, 1, 'HUMAINE', 9.00, FALSE),
-- Order 2: Quatre Fromages (Medium)
(2, 2, 2, 'HUMAINE', 10.50, FALSE),
-- Order 3: Végétarienne (Small) + Napolitaine (Medium) + BBQ Chicken (Large)
(3, 3, 1, 'NAINE', 7.60, FALSE),
(3, 7, 1, 'HUMAINE', 9.50, FALSE),
(3, 12, 1, 'OGRESSE', 18.75, FALSE),
-- Order 4: Calzone (Medium)
(4, 6, 2, 'HUMAINE', 11.00, FALSE),
-- Order 5: Mexicaine (Large) + Carbonara (Medium)
(5, 9, 1, 'OGRESSE', 18.00, FALSE),
(5, 10, 1, 'HUMAINE', 11.50, FALSE),
-- Order 6: Margherita (Medium) - Free for loyalty
(6, 1, 1, 'HUMAINE', 8.50, TRUE),
-- Order 7: Saumon (Large) - Partial discount due to late delivery
(7, 11, 1, 'OGRESSE', 10.00, FALSE),
-- Order 8 (in progress): Quatre Saisons (Medium) + Hawaïenne (Small)
(8, 8, 1, 'HUMAINE', 11.50, FALSE),
(8, 5, 1, 'NAINE', 8.00, FALSE),
-- Order 9 (in progress): BBQ Chicken (Large)
(9, 12, 2, 'OGRESSE', 18.75, FALSE),
-- Order 10 (pending): Pepperoni (Medium) + Carbonara (Medium)
(10, 4, 1, 'HUMAINE', 9.00, FALSE),
(10, 10, 1, 'HUMAINE', 11.50, FALSE),
-- Order 11 (pending): Quatre Fromages (Small) + Végétarienne (Small)
(11, 2, 1, 'NAINE', 8.40, FALSE),
(11, 3, 1, 'NAINE', 7.60, FALSE),
-- Order 12 (canceled): Margherita (Large)
(12, 1, 1, 'OGRESSE', 12.75, FALSE),
-- Order 13: Calzone (Large) + Napolitaine (Medium)
(13, 6, 1, 'OGRESSE', 16.50, FALSE),
(13, 7, 1, 'HUMAINE', 9.50, FALSE),
-- Order 14: Saumon (Medium) + Quatre Saisons (Small)
(14, 11, 1, 'HUMAINE', 13.50, FALSE),
(14, 8, 1, 'NAINE', 9.20, FALSE),
-- Order 15: Margherita (Medium) - Free for loyalty
(15, 1, 1, 'HUMAINE', 8.50, TRUE);