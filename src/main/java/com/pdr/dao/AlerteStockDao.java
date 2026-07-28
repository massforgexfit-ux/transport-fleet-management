package com.pdr.dao;

import com.pdr.config.DatabaseConfig;
import com.pdr.model.AlerteStock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AlerteStockDao {
    public List<AlerteStock> findAll() throws SQLException {
        String sql = """
                SELECT a.id, a.piece_id, p.code_article, a.message, a.date_alerte, a.resolue
                FROM AlerteStock a
                JOIN PieceRechange p ON p.id = a.piece_id
                ORDER BY a.date_alerte DESC
                """;
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<AlerteStock> alertes = new ArrayList<>();
            while (resultSet.next()) {
                AlerteStock alerte = new AlerteStock();
                alerte.setId(resultSet.getLong("id"));
                alerte.setPieceId(resultSet.getLong("piece_id"));
                alerte.setCodeArticle(resultSet.getString("code_article"));
                alerte.setMessage(resultSet.getString("message"));
                Timestamp timestamp = resultSet.getTimestamp("date_alerte");
                alerte.setDateAlerte(timestamp == null ? null : timestamp.toLocalDateTime());
                alerte.setResolue(resultSet.getBoolean("resolue"));
                alertes.add(alerte);
            }
            return alertes;
        }
    }

    public void markResolved(long id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE AlerteStock SET resolue = TRUE WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}
