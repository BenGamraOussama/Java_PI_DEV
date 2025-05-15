# 🌾 HopeNest - Santé mentale 

Ce projet a pour objectif de proposer une plateforme de **santé mentale** pour les jeunes et les adultes.  
L'application permet de consulter des ressources en ligne, de suivre des traitements personnalisés, de participer à des activités et d'accéder à des contenus multimédias tels que des **podcasts** et des vidéos.  
Nous proposons également une sélection de **produits naturels** pour favoriser le bien-être mental et physique.

---

## 📚 Table des matières

- [🌟 Fonctionnalités](#-fonctionnalités)
- [🔐 Gestion des Utilisateurs](#-gestion-des-utilisateurs)
- [💻 Technologies Utilisées](#-technologies-utilisées)
- [🛠️ Installation](#-installation)
- [🚀 Utilisation](#-utilisation)
- [👥 Rôles Utilisateurs](#-rôles-utilisateurs)
- [📬 Notifications](#-notifications)
- [📄 Licence](#-licence)
- [👨‍💻 Crédits](#-crédits)
- [🔖 Topics GitHub](#-topics-github-à-configurer-dans-le-dépôt)
- [☁️ Hébergement](#️-hébergement-facultatif)

---

## 🌟 Fonctionnalités

### 📢 Gestion des consultations
- CRUD complet pour consultations, traitements et rendez_vous
- Filtrage dynamique
- Création d'une réunion Zoom pour chaque consultation
- Dashboard administrateur pour approuver ou supprimer des consultations
- Notification email d'Acceptation ou de Refus

### 👨‍🌾 Gestion des activités
- Notification SMS via Twilio API lors de l'ajout d'une nouvelle activité
- Modification et Suppression d'une activité 

### 📅 Gestion des ressources 
- Création ,modification et Suppression des ressources
- Intégration **Google traduction** pour les articles
- Filtrage dynamique des ressources par catégorie via AJAX.

### 🛒 Gestion Produits & Catégories
- Ajout, édition et visualisation des produits
- Classement par catégorie
- Suivi des quantités disponibles
- Détails d’un produit 
- Intégration **Google traduction** pour les descriptions

### 💳 Gestion Commandes & Paiements
- Panier et gestion des commandes
- Intégration **Stripe** pour le paiement
- Historique des commandes clients
- 

---

## 🔐 Gestion des Utilisateurs

Dans ce projet, la gestion des utilisateurs couvre **quatre types d'espaces** : **Admin, Patient, Fournisseur et Psychiatre**.

- Chaque utilisateur dispose d’un **profil personnel** pour afficher ses informations et modifier son mot de passe.
- L’inscription inclut une **vérification par email**.
- L’administrateur peut **ajouter manuellement des utilisateurs**, avec un mot de passe généré automatiquement, envoyé par **email** ou **SMS (Twilio)**.
- Fonctionnalité **mot de passe oublié** avec envoi d’un **code de réinitialisation par email**.
- Opérations **CRUD complètes** pour la gestion des comptes utilisateurs.
- Possibilité de **bannir** ou **débannir** un compte.
- Une section **statistiques utilisateurs** permet d’afficher les données depuis la base (nombre par rôle, comptes actifs/inactifs, etc.).
- Vérification par email avec lien de confirmation
---

## 💻 Technologies Utilisées

- **Java 17** (JavaFX Desktop)
- **Symfony 6.2** (Web)
- **PHP 8** (XAMPP)
- **MySQL** (Base de données)
- **JDBC / Doctrine ORM**
- **Apache PDFBox** (PDF export)
- **ZXing** (QR code)
- **Twilio API** (SMS)
- **Stripe API** (Paiements)
- **JavaMail / Symfony Mailer** (Emails)
- **PurgoMalum API** (Filtrage de langage inapproprié)


---

## 🛠️ Installation

### Prérequis
- Java 17
- PHP (pour Symfony)
- Composer & Maven
- XAMPP (MySQL)

### Étapes

1. Cloner les dépôts :
```bash
git clone https://github.com/BenGamraOussama/Java_PI_DEV.git
git clone https://github.com/BenGamraOussama/symfony_PI_DEV.git
```

2. Créer la base de données via les scripts dans `/sql`.

3. Configurer la connexion à la base :
   - JavaFX : `MyDatabase.java`
   - Symfony : fichier `.env`

4. Installer les dépendances :
```bash
composer install
mvn clean install
```

5. Lancer l'application JavaFX :
```bash
MainFX.java
```

6. Démarrer le serveur Symfony :
```bash
symfony server:start
```

---

## 🚀 Utilisation

- L'application bureau permet aux psychiatres et patients de gérer les consultations, les prescriptions et les commandes localement..
- L’interface web est principalement destinée à l’administration (gestion des utilisateurs, produits, et commandes).
- Les fournisseurs utilisent également l’interface web pour la gestion des produits.
- Les deux communiquent avec la base via JDBC (Java) ou Doctrine (Symfony).

---

## 👥 Rôles Utilisateurs

| Rôle        | Fonctions principales |
|-------------|------------------------|
| **Psychiatre** | Gère patients, consultations, prescriptions, suivi thérapeutique |
| **Patient**  | Réserve consultations, passe commandes, |
| **Fournisseur** | Gère des produits |
| **Admin**   | Gère les utilisateurs,produits, commandes,  |

---

## 📬 Notifications

- 📩 Email : Ajouter une nouvelle activité, reset password, ajouter user
- 📱 SMS : Ajouter une nouvelle activité, mot de passe généré


---


## 📄 Licence

Ce projet est développé à des **fins pédagogiques** dans le cadre du module **PIDEV 3A** à **Esprit School of Engineering**. Toute utilisation commerciale est interdite.

---

## 👨‍💻 Crédits

Développé par l’équipe **NovaTeam** :
- Khazri hajer 
- Farah zouaoui  
- Oussama denguir 
- Oussama ben gamra 
- Fares belgacem  
- Ghofrane idriss

---

## 🔖 Topics GitHub à configurer dans le dépôt

```
java, javafx, symfony, php, mysql,  api-integration, xampp, stripe, twilio, crud, 
```

---

## ☁️ Hébergement (facultatif)

- Base de données locale via **XAMPP**
- Application JavaFX lancée localement
- Interface Symfony hébergée localement via `symfony server:start`
---


### 🤖 Fonctionnalités supplémentaires

- **API de génération d’image ** 
- **Chatbot intégré** pour répondre aux **questions des exercices** 

---
