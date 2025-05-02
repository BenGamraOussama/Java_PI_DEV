package org.example.pi__dev_.services;

import org.example.pi__dev_.enteties.RDV;

import java.sql.SQLException;
import java.util.List;

public interface Iservice<T> {

    List<T> readList() throws SQLException;

    RDV add(T t) throws SQLException;

    void update(T t) throws SQLException;
}
