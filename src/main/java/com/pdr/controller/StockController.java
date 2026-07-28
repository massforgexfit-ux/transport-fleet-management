package com.pdr.controller;

import com.pdr.dao.StockDao;

import java.sql.SQLException;

public class StockController {
    private final StockDao stockDao = new StockDao();

    public boolean enregistrerEntree(long pieceId, Long technicienId, int quantite, String commentaire) throws SQLException {
        return stockDao.enregistrerEntree(pieceId, technicienId, quantite, commentaire);
    }

    public boolean enregistrerSortie(long pieceId, Long machineId, Long technicienId, int quantite, String commentaire) throws SQLException {
        return stockDao.enregistrerSortie(pieceId, machineId, technicienId, quantite, commentaire);
    }
}
