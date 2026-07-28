package com.pdr.controller;

import com.pdr.dao.TechnicienDao;
import com.pdr.model.Technicien;

import java.sql.SQLException;
import java.util.List;

public class TechnicienController {
    private final TechnicienDao dao = new TechnicienDao();

    public List<Technicien> findAll() throws SQLException {
        return dao.findAll();
    }

    public void save(Technicien technicien) throws SQLException {
        if (technicien.getId() == 0) {
            dao.save(technicien);
        } else {
            dao.update(technicien);
        }
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
    }
}
