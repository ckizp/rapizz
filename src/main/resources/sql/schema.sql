CREATE TABLE pizzas (
  pizza_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  pizza_name VARCHAR(48) NOT NULL,
  base_price DECIMAL(5, 2) UNSIGNED NOT NULL
);

CREATE TABLE ingredients (
  ingredient_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  ingredient_name VARCHAR(24) NOT NULL
);

CREATE TABLE pizzas_ingredients (
  pizza_id INT NOT NULL,
  ingredient_id INT NOT NULL,
  PRIMARY KEY (pizza_id, ingredient_id),
  FOREIGN KEY (pizza_id) REFERENCES pizzas (pizza_id),
  FOREIGN KEY (ingredient_id) REFERENCES ingredients (ingredient_id)
);

CREATE TABLE delivery_drivers (
  driver_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  first_name VARCHAR(48) NOT NULL,
  last_name VARCHAR(48) NOT NULL,
  phone_number VARCHAR(16) NOT NULL UNIQUE
);

CREATE TABLE vehicles (
  vehicle_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  vehicle_type ENUM('CAR', 'MOTORCYCLE') NOT NULL,
  license_plate VARCHAR(12) NOT NULL UNIQUE
);

CREATE TABLE clients (
  client_id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(48) NOT NULL,
  last_name VARCHAR(48) NOT NULL,
  client_address VARCHAR(256),
  phone_number VARCHAR(16),
  amount DECIMAL(8,2) NOT NULL DEFAULT 0.0,
  loyalty_counter INT NOT NULL DEFAULT 0
);

CREATE TABLE orders (
  order_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  client_id INT NOT NULL,
  driver_id INT,
  vehicle_id INT,
  order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  order_status ENUM('PENDING', 'IN_PROGRESS', 'DELIVERED', 'CANCELED') NOT NULL DEFAULT 'PENDING',
  client_rating TINYINT DEFAULT NULL CHECK (client_rating BETWEEN 0 AND 5),
  free_reason ENUM('NOT_FREE', 'LOYALTY', 'LATE_DELIVERY') NOT NULL DEFAULT 'NOT_FREE',
  FOREIGN KEY (client_id) REFERENCES clients(client_id),
  FOREIGN KEY (driver_id) REFERENCES delivery_drivers(driver_id),
  FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id)
);

CREATE TABLE order_pizzas (
  order_item_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  order_id INT NOT NULL,
  pizza_id INT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  pizza_size ENUM('NAINE', 'HUMAINE', 'OGRESSE') NOT NULL,
  pizza_price DECIMAL(5, 2) NOT NULL,
  is_free BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (order_id) REFERENCES orders(order_id),
  FOREIGN KEY (pizza_id) REFERENCES pizzas(pizza_id)
);