package com.contact.management.component.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.contact.management.component.jdbc.connection.ConnectionProvider;
import com.contact.management.component.jdbc.consumer.RowAccumulator;
import com.contact.management.component.jdbc.consumer.RowConsumer;
import com.contact.management.component.jdbc.consumer.RowMapper;
import com.contact.management.component.jdbc.consumer.StatementConsumer;
import com.contact.management.component.jdbc.consumer.StatementProcessor;

public class MyJdbcTemplate
        implements JdbcTemplateQueryOperation, JdbcTemplateQueryForObjectOperation, JdbcTemplateUpdateOperation {

    private static final Logger logger = LogManager.getLogger();

    protected static final ThreadLocal<Connection> transactionConn = new ThreadLocal<>();

    protected final ConnectionProvider connectionProvider;

    private final NamedQueryParser parser;

    public MyJdbcTemplate(final ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        this.parser = new NamedQueryParser();
    }

    public MyJdbcTemplate(final ConnectionProvider connectionProvider, final int preparedStatementCacheQueries) {
        this.connectionProvider = connectionProvider;
        this.parser = new NamedQueryParser(preparedStatementCacheQueries);
    }

    private <T> T getStatement(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final StatementConsumer<T> callback) {
        return processStatement(readFrom, sql, callback, (namedStatement, statement) -> {
            namedStatement.apply(statement, data);
        });
    }

    private <T> T getBatchStatement(final ReadFrom readFrom, final String sql, final List<Map<String, Object>> data,
            final StatementConsumer<T> callback) {
        return processStatement(readFrom, sql, callback, (namedStatement, statement) -> {
            for (final Map<String, Object> item : data) {
                namedStatement.apply(statement, item);
                statement.addBatch();
            }
        });
    }

    private <T> T processStatement(final ReadFrom readFrom, final String sql, final StatementConsumer<T> callback,
            final StatementProcessor statementConsumer) {
        final ParsedNamedQuery namedStatement = parser.parse(sql);
        final Connection transConn = transactionConn.get();

        if (transConn == null) {
            try (Connection conn = connectionProvider.get(readFrom);
                    PreparedStatement statement = conn.prepareStatement(namedStatement.sql)) {
                statementConsumer.accept(namedStatement, statement);

                return callback.accept(statement);
            } catch (final SQLException e) {
                logger.error("JDBC error", e);

                throw new RuntimeException(e);
            }
        } else { // in transaction mode
            try (PreparedStatement statement = transConn.prepareStatement(namedStatement.sql)) {
                statementConsumer.accept(namedStatement, statement);

                return callback.accept(statement);
            } catch (final SQLException e) {
                logger.error("JDBC error", e);

                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public <T> List<T> query(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final Class<T> clazz) {
        return getStatement(readFrom, sql, data, statement -> {
            final List<T> container = new ArrayList<>();

            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                final T object = convert(rs, clazz);
                if (object != null) {
                    container.add(object);
                }
            }

            return container;
        });
    }

    public void batchQuery(final String sql, final RowConsumer consumer) {
        batchQuery(ReadFrom.PreferPrimary, sql, consumer);
    }

    public void batchQuery(final ReadFrom readFrom, final String sql, final RowConsumer consumer) {
        batchQuery(readFrom, sql, Map.of(), consumer);
    }

    public void batchQuery(final String sql, final Map<String, Object> data, final RowConsumer consumer) {
        batchQuery(ReadFrom.PreferPrimary, sql, data, consumer);
    }

    public void batchQuery(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final RowConsumer consumer) {
        getStatement(readFrom, sql, data, statement -> {
            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                consumer.accept(rs);
            }

            return null;
        });
    }

    public <T> T batchQuery(final String sql, final Supplier<T> initData, final RowAccumulator<T> mapper) {
        return batchQuery(ReadFrom.PreferPrimary, sql, initData, mapper);
    }

    public <T> T batchQuery(final ReadFrom readFrom, final String sql, final Supplier<T> initData,
            final RowAccumulator<T> mapper) {
        return batchQuery(readFrom, sql, Map.of(), initData, mapper);
    }

    public <T> T batchQuery(final String sql, final Map<String, Object> data, final Supplier<T> initData,
            final RowAccumulator<T> mapper) {
        return batchQuery(ReadFrom.PreferPrimary, sql, data, initData, mapper);
    }

    public <T> T batchQuery(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final Supplier<T> initData, final RowAccumulator<T> mapper) {
        return getStatement(readFrom, sql, data, statement -> {
            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();

            final T container = initData.get();
            while (rs.next()) {
                mapper.accept(rs, container);
            }

            return container;
        });
    }

    @Override
    public <T> List<T> query(final ReadFrom readFrom, final String sql, final RowMapper<T> mapper) {
        return getStatement(readFrom, sql, Map.of(), statement -> {
            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();

            final List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapper.accept(rs));
            }

            return result;
        });
    }

    @Override
    public <T> T query(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final Supplier<T> initData, final RowAccumulator<T> mapper) {
        return getStatement(readFrom, sql, data, statement -> {
            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();

            final T container = initData.get();
            while (rs.next()) {
                mapper.accept(rs, container);
            }

            return container;
        });
    }

    @Override
    public <T> List<T> query(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final RowMapper<T> mapper) {
        return getStatement(readFrom, sql, data, statement -> {
            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();

            final List<T> container = new ArrayList<>();
            while (rs.next()) {
                container.add(mapper.accept(rs));
            }

            return container;
        });
    }

    @Override
    public <T> T queryForObject(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final Class<T> clazz, final T defaultValue) {
        return getStatement(readFrom, sql, data, statement -> {
            statement.setFetchSize(1);

            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                final T result = convert(rs, clazz);
                if (result != null) {
                    return result;
                }

                return defaultValue;
            }

            return defaultValue;
        });
    }

    @Override
    public <T> T queryForObject(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final RowMapper<T> mapper, final T defaultValue) {
        return getStatement(readFrom, sql, data, statement -> {
            statement.setFetchSize(1);

            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapper.accept(rs);
            }

            return defaultValue;
        });
    }

    public boolean exists(final String sql, final Map<String, Object> data) {
        return exists(ReadFrom.PreferPrimary, sql, data);
    }

    public boolean exists(final ReadFrom readFrom, final String sql, final Map<String, Object> data) {
        return getStatement(readFrom, sql, data, statement -> {
            statement.setFetchSize(1);

            // execute select SQL statement
            final ResultSet rs = statement.executeQuery();

            return rs.next();
        });
    }

    @Override
    public int update(final String sql, final Map<String, Object> data) {
        return getStatement(ReadFrom.Primary, sql, data, PreparedStatement::executeUpdate);
    }

    @Override
    public <T> T update(final String sql, final Map<String, Object> data, final Supplier<T> initData,
            final RowAccumulator<T> mapper) {
        return query(ReadFrom.Primary, sql, data, initData, mapper);
    }

    @Override
    public <T> List<T> update(final String sql, final Map<String, Object> data, final Class<T> clazz) {
        return query(ReadFrom.Primary, sql, data, clazz);
    }

    @Override
    public <T> List<T> update(final String sql, final Map<String, Object> data, final RowMapper<T> mapper) {
        return query(ReadFrom.Primary, sql, data, mapper);
    }

    @Override
    public <T> T updateForObject(final String sql, final Map<String, Object> data, final Class<T> clazz,
            final T defaultValue) {
        return queryForObject(ReadFrom.Primary, sql, data, clazz, defaultValue);
    }

    @Override
    public <T> T updateForObject(final String sql, final Map<String, Object> data, final RowMapper<T> mapper,
            final T defaultValue) {
        return queryForObject(ReadFrom.Primary, sql, data, mapper, defaultValue);
    }

    public List<Integer> batchUpdate(final String sql, final List<Map<String, Object>> data) {
        return getBatchStatement(ReadFrom.Primary, sql, data, statement -> Arrays.stream(statement.executeBatch())
                .boxed()
                .collect(Collectors.toList()));
    }

    @SuppressWarnings("unchecked")
    private <T> T convert(final ResultSet rs, final Class<T> clazz) throws SQLException {
        if (clazz == Double.class) {
            return (T) Double.valueOf(rs.getDouble(1));
        } else if (clazz == Float.class) {
            return (T) Float.valueOf(rs.getFloat(1));
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(rs.getLong(1));
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(rs.getInt(1));
        } else if (clazz == String.class) {
            return (T) rs.getString(1);
        } else if (clazz == Boolean.class) {
            return (T) (rs.getBoolean(1) ? Boolean.TRUE : Boolean.FALSE);
        } else if (clazz == Timestamp.class) {
            return (T) rs.getTimestamp(1);
        } else {
            throw new IllegalArgumentException();
        }
    }
}