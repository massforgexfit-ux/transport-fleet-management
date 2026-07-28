package com.pdr.controller;

import com.pdr.dao.AlerteStockDao;
import com.pdr.model.AlerteStock;

import java.sql.SQLException;
import java.util.List;

public class AlerteController {
    private final AlerteStockDao dao = new AlerteStockDao();

    public List<AlerteStock> findAll() throws SQLException {
        return dao.findAll();
    }

    public void resolve(long id) throws SQLException {
        dao.markResolved(id);
    }
}
