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