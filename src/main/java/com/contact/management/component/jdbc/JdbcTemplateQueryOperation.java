package com.contact.management.component.jdbc;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.contact.management.component.jdbc.consumer.RowAccumulator;
import com.contact.management.component.jdbc.consumer.RowMapper;

public interface JdbcTemplateQueryOperation {

    default <T> List<T> query(final String sql, final Class<T> clazz) {
        return query(ReadFrom.PreferPrimary, sql, clazz);
    }

    default <T> List<T> query(final ReadFrom readFrom, final String sql, final Class<T> clazz) {
        return query(readFrom, sql, Map.of(), clazz);
    }

    default <T> List<T> query(final String sql, final Map<String, Object> data, final Class<T> clazz) {
        return query(ReadFrom.PreferPrimary, sql, data, clazz);
    }

    <T> List<T> query(final ReadFrom readFrom, final String sql, final Map<String, Object> data, final Class<T> clazz);

    default <T> List<T> query(final String sql, final RowMapper<T> mapper) {
        return query(ReadFrom.PreferPrimary, sql, mapper);
    }

    <T> List<T> query(final ReadFrom readFrom, final String sql, final RowMapper<T> mapper);

    default <T> T query(final String sql, final Supplier<T> initData, final RowAccumulator<T> mapper) {
        return query(ReadFrom.PreferPrimary, sql, initData, mapper);
    }

    default <T> T query(final ReadFrom readFrom, final String sql, final Supplier<T> initData,
            final RowAccumulator<T> mapper) {
        return query(readFrom, sql, Map.of(), initData, mapper);
    }

    default <T> T query(final String sql, final Map<String, Object> data, final Supplier<T> initData,
            final RowAccumulator<T> mapper) {
        return query(ReadFrom.PreferPrimary, sql, data, initData, mapper);
    }

    <T> T query(final ReadFrom readFrom, final String sql, final Map<String, Object> data, final Supplier<T> initData,
            final RowAccumulator<T> mapper);

    default <T> List<T> query(final String sql, final Map<String, Object> data, final RowMapper<T> mapper) {
        return query(ReadFrom.PreferPrimary, sql, data, mapper);
    }

    <T> List<T> query(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final RowMapper<T> mapper);
}