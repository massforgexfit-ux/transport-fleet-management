package com.pdr.controller;

import com.pdr.dao.PieceRechangeDao;
import com.pdr.model.PieceRechange;
import com.pdr.service.PieceImportService;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

public class PieceController {
    private final PieceRechangeDao dao = new PieceRechangeDao();
    private final PieceImportService importService = new PieceImportService();

    public List<PieceRechange> findAll() throws SQLException {
        return dao.findAll();
    }

    public List<PieceRechange> search(String query) throws SQLException {
        return query == null || query.isBlank() ? dao.findAll() : dao.search(query.trim());
    }

    public void save(PieceRechange piece) throws SQLException {
        if (piece.getId() == 0) {
            dao.save(piece);
        } else {
            dao.update(piece);
        }
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
    }

    public int importFile(Path path) throws Exception {
        return importService.importFile(path);
    }
}
