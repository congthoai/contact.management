package com.contact.management.component.jdbc.connection;

import java.sql.Connection;
import java.sql.SQLException;


import com.contact.management.component.jdbc.ReadFrom;
import com.zaxxer.hikari.HikariDataSource;

public class PGConnectionProvider implements ConnectionProvider {

    private static final long RetryTimeout = 10_000; // 10 seconds

    private final HikariDataSource primary;
    private final HikariDataSource secondary;

    private final int secondaryErrorLimit;
    private int secondaryErrorCounter;
    private long startTime;

    public PGConnectionProvider(HikariDataSource primmary, HikariDataSource secondary) {
        this.primary = primmary;
        this.secondary = secondary;
        secondaryErrorLimit = 5 / 4; // 25%
    }

    @Override
    public Connection get(final ReadFrom readFrom) throws SQLException {
        if (secondary == null) {
            return primary.getConnection();
        }

        if (readFrom == ReadFrom.Primary) {
            return primary.getConnection();
        } else if (readFrom == ReadFrom.Secondary) {
            return secondary.getConnection();
        } else if (readFrom == ReadFrom.PreferPrimary) {
            try {
                return primary.getConnection();
            } catch (final SQLException e) {
                return secondary.getConnection();
            }
        } else if (readFrom == ReadFrom.PreferSecondary) {
            if (secondaryErrorCounter >= secondaryErrorLimit && System.currentTimeMillis() - startTime < RetryTimeout) {
                return primary.getConnection();
            }

            try {
                final Connection conn = secondary.getConnection();
                if (secondaryErrorCounter != 0) {
                    secondaryErrorCounter = 0;
                    startTime = 0;
                }

                return conn;
            } catch (final SQLException e) {
                secondaryErrorCounter++;
                startTime = System.currentTimeMillis();

                return primary.getConnection();
            }
        }

        return primary.getConnection();
    }

}