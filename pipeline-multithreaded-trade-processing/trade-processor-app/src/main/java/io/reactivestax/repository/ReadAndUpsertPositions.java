package io.reactivestax.repository;

import io.reactivestax.model.Position;

import java.sql.Connection;
import java.sql.SQLException;

public interface ReadAndUpsertPositions {
    int[] lookupPositions(Position position, Connection connection) throws SQLException;

    void insertIntoPositions(Position position, Connection connection) throws SQLException;

    void updatePositions(Position position, Connection connection) throws SQLException;
}
