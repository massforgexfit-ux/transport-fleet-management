package com.pdr.dao;

import com.pdr.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockDao {
    public boolean enregistrerEntree(long pieceId, Long technicienId, int quantite, String commentaire) throws SQLException {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive.");
        }
        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);
            try {
                insertEntree(connection, pieceId, technicienId, quantite, commentaire);
                updateStock(connection, pieceId, quantite);
                boolean alert = createAlertIfNeeded(connection, pieceId);
                connection.commit();
                return alert;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    public boolean enregistrerSortie(long pieceId, Long machineId, Long technicienId, int quantite, String commentaire) throws SQLException {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive.");
        }
        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int stock = currentStock(connection, pieceId);
                if (quantite > stock) {
                    throw new IllegalStateException("Sortie impossible : stock disponible insuffisant.");
                }
                insertSortie(connection, pieceId, machineId, technicienId, quantite, commentaire);
                updateStock(connection, pieceId, -quantite);
                boolean alert = createAlertIfNeeded(connection, pieceId);
                connection.commit();
                return alert;
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private void insertEntree(Connection connection, long pieceId, Long technicienId, int quantite, String commentaire) throws SQLException {
        String sql = "INSERT INTO EntreeStock (piece_id, technicien_id, quantite, commentaire) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, pieceId);
            setNullableLong(statement, 2, technicienId);
            statement.setInt(3, quantite);
            statement.setString(4, commentaire);
            statement.executeUpdate();
        }
    }

    private void insertSortie(Connection connection, long pieceId, Long machineId, Long technicienId, int quantite, String commentaire) throws SQLException {
        String sql = "INSERT INTO SortieStock (piece_id, machine_id, technicien_id, quantite, commentaire) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, pieceId);
            setNullableLong(statement, 2, machineId);
            setNullableLong(statement, 3, technicienId);
            statement.setInt(4, quantite);
            statement.setString(5, commentaire);
            statement.executeUpdate();
        }
    }

    private int currentStock(Connection connection, long pieceId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT stock_actuel FROM PieceRechange WHERE id = ? FOR UPDATE")) {
            statement.setLong(1, pieceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("stock_actuel");
                }
                throw new IllegalArgumentException("Piece introuvable.");
            }
        }
    }

    private void updateStock(Connection connection, long pieceId, int delta) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE PieceRechange SET stock_actuel = stock_actuel + ? WHERE id = ?")) {
            statement.setInt(1, delta);
            statement.setLong(2, pieceId);
            statement.executeUpdate();
        }
    }

    private boolean createAlertIfNeeded(Connection connection, long pieceId) throws SQLException {
        String select = "SELECT code_article, stock_actuel, stock_minimum FROM PieceRechange WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setLong(1, pieceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next() || resultSet.getInt("stock_actuel") > resultSet.getInt("stock_minimum")) {
                    return false;
                }
                String message = "Stock minimum atteint pour " + resultSet.getString("code_article");
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO AlerteStock (piece_id, message) VALUES (?, ?)")) {
                    insert.setLong(1, pieceId);
                    insert.setString(2, message);
                    insert.executeUpdate();
                }
                return true;
            }
        }
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }
}
