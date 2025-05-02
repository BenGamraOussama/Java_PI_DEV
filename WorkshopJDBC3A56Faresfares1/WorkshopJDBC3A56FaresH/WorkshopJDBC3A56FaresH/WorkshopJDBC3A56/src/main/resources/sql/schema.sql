-- Table des catégories
CREATE TABLE IF NOT EXISTS categorie (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL
);

-- Table des produits
CREATE TABLE IF NOT EXISTS produit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    categorie_id INT,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    prix DOUBLE NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    image VARCHAR(255),
    FOREIGN KEY (categorie_id) REFERENCES categorie(id)
);

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CLIENT') DEFAULT 'CLIENT'
);

-- Table des commandes
CREATE TABLE IF NOT EXISTS commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_commande DATE NOT NULL,
    user_id INT,
    nom_client VARCHAR(100) NOT NULL,
    prenom_client VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Table des lignes de commande
CREATE TABLE IF NOT EXISTS ligne_commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    commande_id INT,
    produit_id INT,
    quantite INT NOT NULL,
    prix_unitaire DOUBLE NOT NULL,
    FOREIGN KEY (commande_id) REFERENCES commande(id),
    FOREIGN KEY (produit_id) REFERENCES produit(id)
);

-- Insertion de données de test
INSERT INTO categorie (nom) VALUES 
('Boissons'),
('Alimentation'),
('Fruits et Légumes');

INSERT INTO produit (categorie_id, nom, description, prix, disponible, image) VALUES 
(1, 'Eau minérale', 'Bouteille d\'eau 1.5L', 1.50, TRUE, 'eau.jpg'),
(2, 'Pain', 'Baguette tradition', 1.20, TRUE, 'pain.jpg'),
(3, 'Tomates', 'Tomates cerises', 2.50, TRUE, 'tomates.jpg');

INSERT INTO user (nom, prenom, email, password, role) VALUES 
('Admin', 'Admin', 'admin@example.com', 'admin123', 'ADMIN'),
('Client', 'Test', 'client@example.com', 'client123', 'CLIENT'); 