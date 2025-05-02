-- Voir la structure actuelle de la table
SHOW CREATE TABLE produit;

-- Désactiver temporairement les contraintes de clé étrangère
SET FOREIGN_KEY_CHECKS = 0;

-- Vider d'abord la table ligne_commande
DELETE FROM ligne_commande;

-- Vider la table produit
DELETE FROM produit;

-- Modifier la structure de la table
ALTER TABLE produit
DROP PRIMARY KEY,
MODIFY COLUMN id INT AUTO_INCREMENT,
ADD PRIMARY KEY (id),
MODIFY COLUMN nom VARCHAR(100) NOT NULL,
MODIFY COLUMN description TEXT,
MODIFY COLUMN prix DECIMAL(10,2) NOT NULL,
ADD COLUMN IF NOT EXISTS stock INT NOT NULL DEFAULT 0 AFTER prix,
ADD COLUMN IF NOT EXISTS image VARCHAR(255) AFTER stock;

-- Insérer les produits de test
INSERT INTO produit (nom, description, prix, stock, image) VALUES
('Ordinateur Portable', 'PC portable performant avec processeur i7', 999.99, 10, 'laptop.png'),
('Smartphone', 'Smartphone dernier cri avec appareil photo haute résolution', 699.99, 15, 'phone.png'),
('Tablette', 'Tablette légère et puissante', 499.99, 8, 'tablet.png'),
('Casque Audio', 'Casque sans fil avec réduction de bruit', 199.99, 20, 'headphones.png'),
('Souris Gaming', 'Souris ergonomique pour gamers', 79.99, 30, 'mouse.png');

-- Réactiver les contraintes de clé étrangère
SET FOREIGN_KEY_CHECKS = 1;

-- Vérifier la structure finale de la table
SHOW CREATE TABLE produit;
DESCRIBE produit; 