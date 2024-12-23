-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3308
-- Généré le : lun. 23 déc. 2024 à 16:06
-- Version du serveur : 8.0.31
-- Version de PHP : 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bibliotheque`
--

-- --------------------------------------------------------

--
-- Structure de la table `documents`
--

DROP TABLE IF EXISTS `documents`;
CREATE TABLE IF NOT EXISTS `documents` (
  `DOC_ID` bigint NOT NULL AUTO_INCREMENT,
  `DOC_TITRE` varchar(128) NOT NULL,
  `DOC_AUTEUR` varchar(128) DEFAULT NULL,
  `DOC_DESCRIPTION` text,
  `DOC_FICHE_TECHNIQUE` text,
  `DOC_DATE_PUBLICATION` datetime DEFAULT NULL,
  `DOC_QUANTITE` bigint NOT NULL,
  `DOC_QUANTITE_DISPO` bigint NOT NULL,
  `DOC_TYPE` enum('Livre','Magazine','Journal','Multimédia') NOT NULL,
  PRIMARY KEY (`DOC_ID`),
  KEY `idx_doc_titre` (`DOC_TITRE`),
  KEY `idx_doc_auteur` (`DOC_AUTEUR`),
  KEY `idx_doc_type` (`DOC_TYPE`)
) ;

--
-- Déchargement des données de la table `documents`
--

INSERT INTO `documents` (`DOC_ID`, `DOC_TITRE`, `DOC_AUTEUR`, `DOC_DESCRIPTION`, `DOC_FICHE_TECHNIQUE`, `DOC_DATE_PUBLICATION`, `DOC_QUANTITE`, `DOC_QUANTITE_DISPO`, `DOC_TYPE`) VALUES
(31, 'Seigneur des Anneaux', 'J.R.R. Tolkien', 'Un roman épique', 'Livre Aventure', '1954-07-29 00:00:00', 3, 2, 'Livre'),
(29, 'Star Wars Soundtrack', 'John Williams', 'Musique originale', NULL, '2024-12-17 00:00:00', 2, 1, 'Multimédia'),
(30, 'Harry Potter', 'J.K. Rowling', 'Un livre de magie', 'Livre Fantastique', '1997-06-26 00:00:00', 5, 5, 'Livre'),
(28, 'Le Monde', 'Rédaction Le Monde', 'Actualités', NULL, '2024-12-17 00:00:00', 2, 1, 'Journal'),
(26, 'Harry Potter', 'J.K. Rowling', 'Premier tome', NULL, '2024-12-17 00:00:00', 5, 5, 'Livre'),
(27, 'National Geographic', 'Editeur NatGeo', 'Science et Nature', NULL, '2024-12-17 00:00:00', 3, 2, 'Magazine'),
(22, 'Harry Potter', 'J.K. Rowling', 'Premier tome', NULL, '2024-12-17 00:00:00', 5, 5, 'Livre'),
(23, 'National Geographic', 'Editeur NatGeo', 'Science et Nature', NULL, '2024-12-17 00:00:00', 3, 2, 'Magazine'),
(24, 'Le Monde', 'Rédaction Le Monde', 'Actualités', NULL, '2024-12-17 00:00:00', 2, 1, 'Journal'),
(25, 'Star Wars Soundtrack', 'John Williams', 'Musique originale', NULL, '2024-12-17 00:00:00', 2, 1, 'Multimédia'),
(32, 'National Geographic', 'NatGeo', 'Magazine scientifique', 'Mensuelle', '2024-01-01 00:00:00', 10, 10, 'Magazine'),
(33, 'Le Monde', 'Rédaction Le Monde', 'Journal quotidien', 'Actualités', '2024-06-17 00:00:00', 7, 7, 'Journal'),
(34, 'Star Wars Soundtrack', 'John Williams', 'Musique originale du film', 'CD', '1977-05-25 00:00:00', 4, 4, 'Multimédia'),
(35, 'Inception Soundtrack', 'Hans Zimmer', 'Musique du film Inception', 'CD', '2010-07-16 00:00:00', 2, 2, 'Multimédia'),
(36, 'The Economist', 'The Economist Group', 'Analyse économique', 'Hebdomadaire', '2024-06-10 00:00:00', 8, 8, 'Magazine'),
(37, 'New York Times', 'NYT Team', 'Journal américain', 'Quotidien', '2024-06-17 00:00:00', 5, 5, 'Journal'),
(38, 'Code Complete', 'Steve McConnell', 'Livre sur les meilleures pratiques de codage', 'Livre Technique', '2004-06-09 00:00:00', 6, 6, 'Livre'),
(39, 'Interstellar Soundtrack', 'Hans Zimmer', 'Musique de film', 'CD', '2014-11-07 00:00:00', 3, 3, 'Multimédia');

--
-- Déclencheurs `documents`
--
DROP TRIGGER IF EXISTS `check_doc_quantite_insert`;
DELIMITER $$
CREATE TRIGGER `check_doc_quantite_insert` BEFORE INSERT ON `documents` FOR EACH ROW BEGIN
    IF NEW.DOC_QUANTITE_DISPO < 0 OR NEW.DOC_QUANTITE_DISPO > NEW.DOC_QUANTITE THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'DOC_QUANTITE_DISPO doit être compris entre 0 et DOC_QUANTITE';
    END IF;
END
$$
DELIMITER ;
DROP TRIGGER IF EXISTS `check_doc_quantite_update`;
DELIMITER $$
CREATE TRIGGER `check_doc_quantite_update` BEFORE UPDATE ON `documents` FOR EACH ROW BEGIN
    IF NEW.DOC_QUANTITE_DISPO < 0 OR NEW.DOC_QUANTITE_DISPO > NEW.DOC_QUANTITE THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'DOC_QUANTITE_DISPO doit être compris entre 0 et DOC_QUANTITE';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Structure de la table `emprunt`
--

DROP TABLE IF EXISTS `emprunt`;
CREATE TABLE IF NOT EXISTS `emprunt` (
  `EMPRUNT_ID` bigint NOT NULL AUTO_INCREMENT,
  `USER_ID` bigint NOT NULL,
  `DOC_ID` bigint NOT NULL,
  `DATE_EMPRUNT` datetime NOT NULL,
  `DATE_RETOUR` datetime DEFAULT NULL,
  `DATE_ECHEANCE` datetime NOT NULL,
  `FRAIS_RETARD` decimal(10,2) DEFAULT '0.00',
  `STATUS` varchar(128) DEFAULT 'Actif',
  PRIMARY KEY (`EMPRUNT_ID`),
  KEY `USER_ID` (`USER_ID`),
  KEY `DOC_ID` (`DOC_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `emprunt`
--

INSERT INTO `emprunt` (`EMPRUNT_ID`, `USER_ID`, `DOC_ID`, `DATE_EMPRUNT`, `DATE_RETOUR`, `DATE_ECHEANCE`, `FRAIS_RETARD`, `STATUS`) VALUES
(13, 9, 23, '2024-12-17 00:00:00', NULL, '2025-01-16 00:00:00', '0.00', 'Cloturee'),
(14, 9, 24, '2024-12-17 00:00:00', NULL, '2025-01-16 00:00:00', '0.00', 'Actif'),
(12, 9, 22, '2024-12-17 00:00:00', NULL, '2025-01-16 00:00:00', '0.00', 'Actif'),
(15, 9, 25, '2024-12-17 00:00:00', NULL, '2025-01-16 00:00:00', '0.00', 'Actif'),
(16, -1, 26, '2024-12-17 00:00:00', NULL, '2025-01-16 00:00:00', '0.00', 'Actif'),
(17, -1, 27, '2024-12-17 00:00:00', NULL, '2025-01-16 00:00:00', '0.00', 'Actif'),
(18, -1, 28, '2024-12-17 00:00:00', NULL, '2025-01-16 00:00:00', '0.00', 'Actif'),
(19, -1, 29, '2024-12-17 00:00:00', NULL, '2025-01-16 00:00:00', '0.00', 'Actif'),
(20, 1, 1, '2024-06-01 00:00:00', NULL, '2024-06-30 00:00:00', '0.00', 'Actif'),
(21, 2, 5, '2024-06-05 00:00:00', NULL, '2024-07-05 00:00:00', '0.00', 'Actif'),
(22, 3, 3, '2024-06-10 00:00:00', NULL, '2024-07-10 00:00:00', '0.00', 'Actif'),
(23, 15, 13, '2024-12-23 00:00:00', NULL, '2024-12-19 00:00:00', '0.00', 'Actif'),
(25, 15, 31, '2024-12-23 00:00:00', NULL, '2025-02-12 00:00:00', '0.00', 'Actif');

-- --------------------------------------------------------

--
-- Structure de la table `journaux`
--

DROP TABLE IF EXISTS `journaux`;
CREATE TABLE IF NOT EXISTS `journaux` (
  `DOC_ID` bigint NOT NULL,
  `DATE_PUBLICATION_SPECIFIQUE` datetime NOT NULL,
  PRIMARY KEY (`DOC_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `journaux`
--

INSERT INTO `journaux` (`DOC_ID`, `DATE_PUBLICATION_SPECIFIQUE`) VALUES
(28, '2024-12-17 00:00:00'),
(24, '2024-12-17 00:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `livres`
--

DROP TABLE IF EXISTS `livres`;
CREATE TABLE IF NOT EXISTS `livres` (
  `DOC_ID` bigint NOT NULL,
  `NB_PAGES` bigint DEFAULT NULL,
  `GENRE_LITTERAIRE` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`DOC_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `livres`
--

INSERT INTO `livres` (`DOC_ID`, `NB_PAGES`, `GENRE_LITTERAIRE`) VALUES
(26, 320, 'Fantastique'),
(22, 320, 'Fantastique');

-- --------------------------------------------------------

--
-- Structure de la table `magazines`
--

DROP TABLE IF EXISTS `magazines`;
CREATE TABLE IF NOT EXISTS `magazines` (
  `DOC_ID` bigint NOT NULL,
  `FREQUENCE_PUBLICATION` enum('quotidien','hebdomadaire','bimensuelle','mensuelle','bimestrielle','trimestrielle','semestrielle','annuelle','occasionnelle') NOT NULL,
  `NUMERO_PARUTION` bigint DEFAULT NULL,
  `EDITEUR` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`DOC_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `magazines`
--

INSERT INTO `magazines` (`DOC_ID`, `FREQUENCE_PUBLICATION`, `NUMERO_PARUTION`, `EDITEUR`) VALUES
(27, 'mensuelle', 150, 'Nat Geo'),
(23, 'mensuelle', 150, 'Nat Geo');

-- --------------------------------------------------------

--
-- Structure de la table `multimedia`
--

DROP TABLE IF EXISTS `multimedia`;
CREATE TABLE IF NOT EXISTS `multimedia` (
  `DOC_ID` bigint NOT NULL,
  `TYPE_MULTIMEDIA` enum('CD','DVD') NOT NULL,
  `DUREE_TOTALE` bigint DEFAULT NULL,
  PRIMARY KEY (`DOC_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `multimedia`
--

INSERT INTO `multimedia` (`DOC_ID`, `TYPE_MULTIMEDIA`, `DUREE_TOTALE`) VALUES
(29, 'CD', 90),
(25, 'CD', 90);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `USER_ID` bigint NOT NULL AUTO_INCREMENT,
  `USER_NOM` varchar(128) NOT NULL,
  `USER_PRENOM` varchar(128) NOT NULL,
  `USER_EMAIL` varchar(128) NOT NULL,
  `USER_TEL` varchar(15) DEFAULT NULL,
  `ROLE` enum('ADMIN','USER') DEFAULT 'USER',
  `PASSWORD` varchar(128) NOT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `USER_EMAIL` (`USER_EMAIL`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`USER_ID`, `USER_NOM`, `USER_PRENOM`, `USER_EMAIL`, `USER_TEL`, `ROLE`, `PASSWORD`) VALUES
(9, 'Dupont', 'Jean', 'jean.dupont@example.com', '0612345678', 'USER', 'test'),
(10, 'Martin', 'Sophie', 'sophie.martin@example.com', '0623456789', 'USER', 'test'),
(11, 'lahmar', 'tabai', 'lahmartabai@yahoo.fr', '0612345678', 'USER', 'test'),
(12, 'Malgras', 'Yasmin', 'MalgrasYasmin@yahoo.fr', '0623456789', 'USER', 'test'),
(13, 'Kaouane', 'Irania', 'KaouaneIrania@yahoo.fr', '0634567890', 'USER', 'test'),
(14, 'Leroy', 'Claire', 'claire.leroy@yahoo.fr', '0645678901', 'USER', 'test'),
(15, 'Morel', 'Pierre', 'pierre.morel@yahoo.fr', '0656789012', 'USER', 'test'),
(16, 'tabai', 'lahmar', 'tabai@yahoo.fr', '0600000000', 'ADMIN', 'test'),
(17, 'Admin', 'System', 'admin@example.com', '0600000000', 'ADMIN', 'test');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
