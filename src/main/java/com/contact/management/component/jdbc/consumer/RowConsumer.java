package com.contact.management.component.jdbc.consumer;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowConsumer {
    void accept(ResultSet rs) throws SQLException;
}