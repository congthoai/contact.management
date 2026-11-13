package com.contact.management.component.jdbc.connection;

import java.sql.Connection;
import java.sql.SQLException;

import com.contact.management.component.jdbc.ReadFrom;

public interface ConnectionProvider {
    Connection get(ReadFrom readFrom) throws SQLException;
}