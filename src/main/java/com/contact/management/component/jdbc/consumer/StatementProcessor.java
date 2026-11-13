package com.contact.management.component.jdbc.consumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.contact.management.component.jdbc.ParsedNamedQuery;

@FunctionalInterface
public interface StatementProcessor {

    void accept(ParsedNamedQuery namedStatement, PreparedStatement statement) throws SQLException;

}