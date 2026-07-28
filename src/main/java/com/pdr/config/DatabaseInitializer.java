package com.pdr.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public void initialize() throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS PieceRechange (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        code_article VARCHAR(80) NOT NULL UNIQUE,
                        description_piece TEXT NOT NULL,
                        unite VARCHAR(50) NOT NULL,
                        groupe_articles VARCHAR(50) NOT NULL,
                        quantite_consommee_historique INT NOT NULL DEFAULT 0,
                        sous_ensemble VARCHAR(255),
                        reference_constructeur VARCHAR(255),
                        piece_usure BOOLEAN NOT NULL DEFAULT FALSE,
                        stock_actuel INT NOT NULL DEFAULT 0,
                        stock_minimum INT NOT NULL DEFAULT 0,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS Machine (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        code VARCHAR(80) NOT NULL UNIQUE,
                        nom VARCHAR(150) NOT NULL,
                        emplacement VARCHAR(150),
                        description TEXT
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS Technicien (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        matricule VARCHAR(80) NOT NULL UNIQUE,
                        nom VARCHAR(100) NOT NULL,
                        prenom VARCHAR(100),
                        specialite VARCHAR(150),
                        telephone VARCHAR(50)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS EntreeStock (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        piece_id BIGINT NOT NULL,
                        technicien_id BIGINT,
                        quantite INT NOT NULL,
                        date_entree TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        commentaire TEXT,
                        CONSTRAINT fk_entree_piece FOREIGN KEY (piece_id) REFERENCES PieceRechange(id) ON DELETE CASCADE,
                        CONSTRAINT fk_entree_technicien FOREIGN KEY (technicien_id) REFERENCES Technicien(id) ON DELETE SET NULL,
                        CONSTRAINT chk_entree_quantite CHECK (quantite > 0)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS SortieStock (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        piece_id BIGINT NOT NULL,
                        machine_id BIGINT,
                        technicien_id BIGINT,
                        quantite INT NOT NULL,
                        date_sortie TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        commentaire TEXT,
                        CONSTRAINT fk_sortie_piece FOREIGN KEY (piece_id) REFERENCES PieceRechange(id) ON DELETE CASCADE,
                        CONSTRAINT fk_sortie_machine FOREIGN KEY (machine_id) REFERENCES Machine(id) ON DELETE SET NULL,
                        CONSTRAINT fk_sortie_technicien FOREIGN KEY (technicien_id) REFERENCES Technicien(id) ON DELETE SET NULL,
                        CONSTRAINT chk_sortie_quantite CHECK (quantite > 0)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS AlerteStock (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        piece_id BIGINT NOT NULL,
                        message VARCHAR(255) NOT NULL,
                        date_alerte TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        resolue BOOLEAN NOT NULL DEFAULT FALSE,
                        CONSTRAINT fk_alerte_piece FOREIGN KEY (piece_id) REFERENCES PieceRechange(id) ON DELETE CASCADE
                    )
                    """);
        }
    }
}
