package com.pdr.dao;

import com.pdr.config.DatabaseConfig;
import com.pdr.model.Machine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MachineDao {
    public void save(Machine machine) throws SQLException {
        String sql = "INSERT INTO Machine (code, nom, emplacement, description) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, machine);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    machine.setId(keys.getLong(1));
                }
            }
        }
    }

    public void update(Machine machine) throws SQLException {
        String sql = "UPDATE Machine SET code = ?, nom = ?, emplacement = ?, description = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, machine);
            statement.setLong(5, machine.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Machine WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public List<Machine> findAll() throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Machine ORDER BY code");
             ResultSet resultSet = statement.executeQuery()) {
            List<Machine> machines = new ArrayList<>();
            while (resultSet.next()) {
                machines.add(map(resultSet));
            }
            return machines;
        }
    }

    private void bind(PreparedStatement statement, Machine machine) throws SQLException {
        statement.setString(1, machine.getCode());
        statement.setString(2, machine.getNom());
        statement.setString(3, machine.getEmplacement());
        statement.setString(4, machine.getDescription());
    }

    private Machine map(ResultSet resultSet) throws SQLException {
        Machine machine = new Machine();
        machine.setId(resultSet.getLong("id"));
        machine.setCode(resultSet.getString("code"));
        machine.setNom(resultSet.getString("nom"));
        machine.setEmplacement(resultSet.getString("emplacement"));
        machine.setDescription(resultSet.getString("description"));
        return machine;
    }
}
