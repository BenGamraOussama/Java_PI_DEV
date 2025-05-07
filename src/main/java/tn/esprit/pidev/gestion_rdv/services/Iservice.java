package tn.esprit.pidev.gestion_rdv.services;

import java.sql.SQLException;
import java.util.List;

public interface Iservice<T> {

    List<T> readList() throws SQLException;

    void add(T t) throws SQLException;

    void update(T t) throws SQLException;
}
