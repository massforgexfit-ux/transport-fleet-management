package com.pdr.controller;

import com.pdr.dao.MachineDao;
import com.pdr.model.Machine;

import java.sql.SQLException;
import java.util.List;

public class MachineController {
    private final MachineDao dao = new MachineDao();

    public List<Machine> findAll() throws SQLException {
        return dao.findAll();
    }

    public void save(Machine machine) throws SQLException {
        if (machine.getId() == 0) {
            dao.save(machine);
        } else {
            dao.update(machine);
        }
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
    }
}
