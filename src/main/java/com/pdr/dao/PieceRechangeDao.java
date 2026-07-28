package com.pdr.dao;

import com.pdr.config.DatabaseConfig;
import com.pdr.model.PieceRechange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PieceRechangeDao {
    public void save(PieceRechange piece) throws SQLException {
        String sql = """
                INSERT INTO PieceRechange
                (code_article, description_piece, unite, groupe_articles, quantite_consommee_historique,
                 sous_ensemble, reference_constructeur, piece_usure, stock_actuel, stock_minimum)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    description_piece = VALUES(description_piece),
                    unite = VALUES(unite),
                    groupe_articles = VALUES(groupe_articles),
                    quantite_consommee_historique = VALUES(quantite_consommee_historique),
                    sous_ensemble = VALUES(sous_ensemble),
                    reference_constructeur = VALUES(reference_constructeur),
                    piece_usure = VALUES(piece_usure)
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindPiece(statement, piece);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    piece.setId(keys.getLong(1));
                }
            }
        }
    }

    public void update(PieceRechange piece) throws SQLException {
        String sql = """
                UPDATE PieceRechange
                SET code_article = ?, description_piece = ?, unite = ?, groupe_articles = ?,
                    quantite_consommee_historique = ?, sous_ensemble = ?, reference_constructeur = ?,
                    piece_usure = ?, stock_actuel = ?, stock_minimum = ?
                WHERE id = ?
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindPiece(statement, piece);
            statement.setLong(11, piece.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM PieceRechange WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public List<PieceRechange> findAll() throws SQLException {
        String sql = "SELECT * FROM PieceRechange ORDER BY code_article";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapAll(resultSet);
        }
    }

    public List<PieceRechange> search(String query) throws SQLException {
        String sql = """
                SELECT * FROM PieceRechange
                WHERE code_article LIKE ? OR description_piece LIKE ? OR groupe_articles LIKE ? OR sous_ensemble LIKE ?
                ORDER BY code_article
                """;
        String pattern = "%" + query + "%";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                statement.setString(i, pattern);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapAll(resultSet);
            }
        }
    }

    public Optional<PieceRechange> findById(long id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM PieceRechange WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty();
            }
        }
    }

    private void bindPiece(PreparedStatement statement, PieceRechange piece) throws SQLException {
        statement.setString(1, piece.getCodeArticle());
        statement.setString(2, piece.getDescriptionPiece());
        statement.setString(3, piece.getUnite());
        statement.setString(4, piece.getGroupeArticles());
        statement.setInt(5, piece.getQuantiteConsommeeHistorique());
        statement.setString(6, emptyToNull(piece.getSousEnsemble()));
        statement.setString(7, emptyToNull(piece.getReferenceConstructeur()));
        statement.setBoolean(8, piece.isPieceUsure());
        statement.setInt(9, piece.getStockActuel());
        statement.setInt(10, piece.getStockMinimum());
    }

    private List<PieceRechange> mapAll(ResultSet resultSet) throws SQLException {
        List<PieceRechange> pieces = new ArrayList<>();
        while (resultSet.next()) {
            pieces.add(map(resultSet));
        }
        return pieces;
    }

    private PieceRechange map(ResultSet resultSet) throws SQLException {
        PieceRechange piece = new PieceRechange();
        piece.setId(resultSet.getLong("id"));
        piece.setCodeArticle(resultSet.getString("code_article"));
        piece.setDescriptionPiece(resultSet.getString("description_piece"));
        piece.setUnite(resultSet.getString("unite"));
        piece.setGroupeArticles(resultSet.getString("groupe_articles"));
        piece.setQuantiteConsommeeHistorique(resultSet.getInt("quantite_consommee_historique"));
        piece.setSousEnsemble(resultSet.getString("sous_ensemble"));
        piece.setReferenceConstructeur(resultSet.getString("reference_constructeur"));
        piece.setPieceUsure(resultSet.getBoolean("piece_usure"));
        piece.setStockActuel(resultSet.getInt("stock_actuel"));
        piece.setStockMinimum(resultSet.getInt("stock_minimum"));
        return piece;
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
