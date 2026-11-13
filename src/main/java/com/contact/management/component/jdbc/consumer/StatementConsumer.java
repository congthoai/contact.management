package com.contact.management.component.jdbc.consumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementConsumer<T> {
    T accept(PreparedStatement statement) throws SQLException;
}