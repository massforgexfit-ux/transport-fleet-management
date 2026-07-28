package com.pdr.dao;

import com.pdr.config.DatabaseConfig;
import com.pdr.model.Technicien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TechnicienDao {
    public void save(Technicien technicien) throws SQLException {
        String sql = "INSERT INTO Technicien (matricule, nom, prenom, specialite, telephone) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, technicien);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    technicien.setId(keys.getLong(1));
                }
            }
        }
    }

    public void update(Technicien technicien) throws SQLException {
        String sql = "UPDATE Technicien SET matricule = ?, nom = ?, prenom = ?, specialite = ?, telephone = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, technicien);
            statement.setLong(6, technicien.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Technicien WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public List<Technicien> findAll() throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Technicien ORDER BY nom, prenom");
             ResultSet resultSet = statement.executeQuery()) {
            List<Technicien> techniciens = new ArrayList<>();
            while (resultSet.next()) {
                techniciens.add(map(resultSet));
            }
            return techniciens;
        }
    }

    private void bind(PreparedStatement statement, Technicien technicien) throws SQLException {
        statement.setString(1, technicien.getMatricule());
        statement.setString(2, technicien.getNom());
        statement.setString(3, technicien.getPrenom());
        statement.setString(4, technicien.getSpecialite());
        statement.setString(5, technicien.getTelephone());
    }

    private Technicien map(ResultSet resultSet) throws SQLException {
        Technicien technicien = new Technicien();
        technicien.setId(resultSet.getLong("id"));
        technicien.setMatricule(resultSet.getString("matricule"));
        technicien.setNom(resultSet.getString("nom"));
        technicien.setPrenom(resultSet.getString("prenom"));
        technicien.setSpecialite(resultSet.getString("specialite"));
        technicien.setTelephone(resultSet.getString("telephone"));
        return technicien;
    }
}
