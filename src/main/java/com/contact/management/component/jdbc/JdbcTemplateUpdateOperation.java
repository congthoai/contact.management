package com.contact.management.component.jdbc;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.contact.management.component.jdbc.consumer.RowAccumulator;
import com.contact.management.component.jdbc.consumer.RowMapper;

public interface JdbcTemplateUpdateOperation {

    default boolean boolUpdate(final String sql) {
        return update(sql) > 0;
    }

    default boolean boolUpdate(final String sql, final Map<String, Object> data) {
        return update(sql, data) > 0;
    }

    default int update(final String sql) {
        return update(sql, Map.of());
    }

    int update(final String sql, final Map<String, Object> data);

    default <T> T update(final String sql, final Supplier<T> initData, final RowAccumulator<T> mapper) {
        return update(sql, Map.of(), initData, mapper);
    }

    <T> T update(final String sql, final Map<String, Object> data, final Supplier<T> initData,
            final RowAccumulator<T> mapper);

    default <T> List<T> update(final String sql, final Class<T> clazz) {
        return update(sql, Map.of(), clazz);
    }

    <T> List<T> update(final String sql, final Map<String, Object> data, final Class<T> clazz);

    default <T> List<T> update(final String sql, final RowMapper<T> mapper) {
        return update(sql, Map.of(), mapper);
    }

    <T> List<T> update(final String sql, final Map<String, Object> data, final RowMapper<T> mapper);

    default <T> T updateForObject(final String sql, final Class<T> clazz) {
        return updateForObject(sql, Map.of(), clazz);
    }

    default <T> T updateForObject(final String sql, final Map<String, Object> data, final Class<T> clazz) {
        return updateForObject(sql, data, clazz, null);
    }

    <T> T updateForObject(final String sql, final Map<String, Object> data, final Class<T> clazz, final T defaultValue);

    default <T> T updateForObject(final String sql, final RowMapper<T> mapper) {
        return updateForObject(sql, Map.of(), mapper);
    }

    default <T> T updateForObject(final String sql, final Map<String, Object> data, final RowMapper<T> mapper) {
        return updateForObject(sql, data, mapper, null);
    }

    <T> T updateForObject(final String sql, final Map<String, Object> data, final RowMapper<T> mapper,
            final T defaultValue);
}