-- Supprimer d'abord la table ligne_commande car elle dépend de commande
DROP TABLE IF EXISTS ligne_commande;

-- Ensuite, supprimer la table commande
DROP TABLE IF EXISTS commande;

-- Recréer la table commande avec les nouveaux attributs
CREATE TABLE commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_commande DATE NOT NULL,
    user_id INT,
    nom_client VARCHAR(100) NOT NULL,
    prenom_client VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Recréer la table ligne_commande
CREATE TABLE ligne_commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    commande_id INT,
    produit_id INT,
    quantite INT NOT NULL,
    prix_unitaire DOUBLE NOT NULL,
    FOREIGN KEY (commande_id) REFERENCES commande(id),
    FOREIGN KEY (produit_id) REFERENCES produit(id)
);

-- Insérer des données de test
INSERT INTO commande (date_commande, user_id, nom_client, prenom_client) VALUES 
(CURRENT_DATE, 1, 'Test', 'Client'); 