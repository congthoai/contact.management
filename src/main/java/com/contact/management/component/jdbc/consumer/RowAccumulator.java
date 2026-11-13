package com.contact.management.component.jdbc.consumer;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowAccumulator<T> {
    void accept(ResultSet rs, T data) throws SQLException;
}