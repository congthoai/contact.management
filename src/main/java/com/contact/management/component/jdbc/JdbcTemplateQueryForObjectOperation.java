package com.contact.management.component.jdbc;

import java.util.Map;

import com.contact.management.component.jdbc.consumer.RowMapper;

public interface JdbcTemplateQueryForObjectOperation {

    default <T> T queryForObject(final String sql, final Class<T> clazz) {
        return queryForObject(ReadFrom.PreferPrimary, sql, clazz);
    }

    default <T> T queryForObject(final ReadFrom readFrom, final String sql, final Class<T> clazz) {
        return queryForObject(readFrom, sql, Map.of(), clazz);
    }

    default <T> T queryForObject(final String sql, final Map<String, Object> data, final Class<T> clazz) {
        return queryForObject(ReadFrom.PreferPrimary, sql, data, clazz, null);
    }

    default <T> T queryForObject(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final Class<T> clazz) {
        return queryForObject(readFrom, sql, data, clazz, null);
    }

    default <T> T queryForObject(final String sql, final Map<String, Object> data, final Class<T> clazz,
            final T defaultValue) {
        return queryForObject(ReadFrom.PreferPrimary, sql, data, clazz, defaultValue);
    }

    <T> T queryForObject(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final Class<T> clazz, final T defaultValue);

    default <T> T queryForObject(final String sql, final RowMapper<T> mapper) {
        return queryForObject(ReadFrom.PreferPrimary, sql, mapper);
    }

    default <T> T queryForObject(final ReadFrom readFrom, final String sql, final RowMapper<T> mapper) {
        return queryForObject(readFrom, sql, Map.of(), mapper);
    }

    default <T> T queryForObject(final String sql, final Map<String, Object> data, final RowMapper<T> mapper) {
        return queryForObject(ReadFrom.PreferPrimary, sql, data, mapper);
    }

    default <T> T queryForObject(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final RowMapper<T> mapper) {
        return queryForObject(readFrom, sql, data, mapper, null);
    }

    default <T> T queryForObject(final String sql, final Map<String, Object> data, final RowMapper<T> mapper,
            final T defaultValue) {
        return queryForObject(ReadFrom.PreferPrimary, sql, data, mapper, defaultValue);
    }

    <T> T queryForObject(final ReadFrom readFrom, final String sql, final Map<String, Object> data,
            final RowMapper<T> mapper, final T defaultValue);
}