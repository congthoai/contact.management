package com.contact.management.component.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParsedNamedQuery {

    public final String sql;
    public final List<String> keys;

    public ParsedNamedQuery(final String sql, final List<String> keys) {
        this.sql = sql;
        this.keys = Collections.unmodifiableList(keys);
    }

    public void apply(final PreparedStatement statement, final Map<String, Object> values) throws SQLException {
        String key;
        Object value;
        String str;

        final int size = keys.size();
        for (int i = 0; i < size; i++) {
            key = keys.get(i);
            if (!values.containsKey(key)) {
                throw new SQLException("Missing data for key '" + key + "': " + sql);
            }

            value = values.get(key);
            if (value instanceof String) {
                str = (String) value;

                if (str.contains("\u0000")) {
                    str = str.replaceAll("\u0000", "");
                }

                value = str;
            }

            statement.setObject(i + 1, value);
        }
    }
}