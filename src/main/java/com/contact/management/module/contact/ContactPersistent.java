package com.contact.management.module.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.contact.management.component.jdbc.MyJdbcTemplate;
import com.contact.management.module.contact.domain.Category;
import com.contact.management.module.contact.domain.Contact;
import com.contact.management.module.contact.domain.ContactFilter;
import com.contact.management.module.contact.domain.ContactSearchResult;
import com.contact.management.utils.MapUtils;

@Component
public class ContactPersistent {

    private final MyJdbcTemplate jdbc;

    public ContactPersistent(final MyJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Contact findById(final long id) {
        return jdbc.queryForObject("""
                SELECT id, first_name, last_name, email, phone, company, job_title,
                       address, notes, category, favorite, created_at, updated_at
                FROM contacts
                WHERE id = :id
                """, MapUtils.of("id", id), this::mapContact);
    }

    public ContactSearchResult search(final ContactFilter filter, final int size, final long offset) {
        final Map<String, Object> parameters = new HashMap<>();
        final String where = buildWhere(filter, parameters);

        final List<Contact> contacts = jdbc.query("""
                SELECT id, first_name, last_name, email, phone, company, job_title,
                       address, notes, category, favorite, created_at, updated_at
                FROM contacts
                """ + where + """
                ORDER BY """ + filter.sort().orderBy + """
                LIMIT :limit OFFSET :offset
                """, withPagination(parameters, size, offset), this::mapContact);

        final long total = jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM contacts
                """ + where, parameters, Long.class, 0L);

        return new ContactSearchResult(contacts, total);
    }

    public List<Contact> findAll(final ContactFilter filter) {
        final Map<String, Object> parameters = new HashMap<>();
        final String where = buildWhere(filter, parameters);

        return jdbc.query("""
                SELECT id, first_name, last_name, email, phone, company, job_title,
                       address, notes, category, favorite, created_at, updated_at
                FROM contacts
                """ + where + """
                ORDER BY """ + filter.sort().orderBy, parameters, this::mapContact);
    }

    public boolean existsByEmail(final String email, final Long excludeId) {
        final String sql = excludeId == null
                ? """
                  SELECT 1
                  FROM contacts
                  WHERE LOWER(email) = :email
                  """
                : """
                  SELECT 1
                  FROM contacts
                  WHERE LOWER(email) = :email AND id <> :excludeId
                  """;

        final Map<String, Object> parameters = excludeId == null
                ? MapUtils.of("email", email.toLowerCase())
                : MapUtils.of("email", email.toLowerCase(), "excludeId", excludeId);

        return jdbc.exists(sql, parameters);
    }

    public Contact create(final Contact contact) {
        try {
            return jdbc.updateForObject("""
                    INSERT INTO contacts (
                        first_name, last_name, email, phone, company, job_title,
                        address, notes, category, favorite
                    )
                    VALUES (
                        :firstName, :lastName, :email, :phone, :company, :jobTitle,
                        :address, :notes, :category, :favorite
                    )
                    RETURNING id, first_name, last_name, email, phone, company, job_title,
                              address, notes, category, favorite, created_at, updated_at
                    """, toParameters(contact), this::mapContact, null);
        } catch (final RuntimeException exception) {
            if (hasUniqueViolation(exception)) {
                throw new DuplicateKeyException("A contact with this email already exists", exception);
            }

            throw exception;
        }
    }

    public Contact update(final long id, final Contact contact) {
        try {
            final int updated = jdbc.update("""
                    UPDATE contacts
                    SET first_name = :firstName,
                        last_name = :lastName,
                        email = :email,
                        phone = :phone,
                        company = :company,
                        job_title = :jobTitle,
                        address = :address,
                        notes = :notes,
                        category = :category,
                        favorite = :favorite,
                        updated_at = now()
                    WHERE id = :id
                    """, toParameters(id, contact));

            return updated == 0 ? null : findById(id);
        } catch (final RuntimeException exception) {
            if (hasUniqueViolation(exception)) {
                throw new DuplicateKeyException("A contact with this email already exists", exception);
            }

            throw exception;
        }
    }

    public Contact toggleFavorite(final long id) {
        return jdbc.updateForObject("""
                UPDATE contacts
                SET favorite = NOT favorite,
                    updated_at = now()
                WHERE id = :id
                RETURNING id, first_name, last_name, email, phone, company, job_title,
                          address, notes, category, favorite, created_at, updated_at
                """, MapUtils.of("id", id), this::mapContact, null);
    }

    public boolean delete(final long id) {
        return jdbc.update("DELETE FROM contacts WHERE id = :id", MapUtils.of("id", id)) > 0;
    }

    public long count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM contacts", Map.of(), Long.class, 0L);
    }

    public long countFavorites() {
        return jdbc.queryForObject("""
                SELECT COUNT(*)
                FROM contacts
                WHERE favorite = TRUE
                """, Map.of(), Long.class, 0L);
    }

    public Map<Category, Long> countByCategory() {
        final Map<Category, Long> result = new HashMap<>();

        jdbc.query("""
                SELECT category, COUNT(*) AS total
                FROM contacts
                GROUP BY category
                """, rs -> result.put(Category.valueOf(rs.getString("category")), rs.getLong("total")));

        return result;
    }

    private String buildWhere(final ContactFilter filter, final Map<String, Object> parameters) {
        final StringBuilder where = new StringBuilder();
        if (filter.hasKeyword()) {
            where.append("""
                    WHERE (
                        LOWER(COALESCE(first_name, '') || ' ' || COALESCE(last_name, '')) LIKE :keyword
                        OR LOWER(COALESCE(email, '')) LIKE :keyword
                        OR LOWER(COALESCE(phone, '')) LIKE :keyword
                        OR LOWER(COALESCE(company, '')) LIKE :keyword
                    )
                    """);
            parameters.put("keyword", "%" + filter.q().trim().toLowerCase() + "%");
        }

        if (filter.category() != null) {
            appendCondition(where, "category = :category");
            parameters.put("category", filter.category().name());
        }

        if (filter.favorites()) {
            appendCondition(where, "favorite = TRUE");
        }

        return where.toString();
    }

    private void appendCondition(final StringBuilder where, final String condition) {
        where.append(where.length() == 0 ? "WHERE " : "AND ")
                .append(condition)
                .append(' ');
    }

    private Map<String, Object> withPagination(final Map<String, Object> parameters,
            final int size, final long offset) {
        final Map<String, Object> paginated = new HashMap<>(parameters);
        paginated.put("limit", size);
        paginated.put("offset", offset);
        return paginated;
    }

    private Map<String, Object> toParameters(final Contact contact) {
        return MapUtils.of(
                "firstName", contact.firstName(),
                "lastName", contact.lastName(),
                "email", contact.email(),
                "phone", contact.phone(),
                "company", contact.company(),
                "jobTitle", contact.jobTitle(),
                "address", contact.address(),
                "notes", contact.notes(),
                "category", contact.category().name(),
                "favorite", contact.favorite());
    }

    private Map<String, Object> toParameters(final long id, final Contact contact) {
        final Map<String, Object> parameters = toParameters(contact);
        parameters.put("id", id);
        return parameters;
    }

    private Contact mapContact(final ResultSet rs) throws SQLException {
        final Timestamp createdAt = rs.getTimestamp("created_at");
        final Timestamp updatedAt = rs.getTimestamp("updated_at");

        return new Contact(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("company"),
                rs.getString("job_title"),
                rs.getString("address"),
                rs.getString("notes"),
                Category.valueOf(rs.getString("category")),
                rs.getBoolean("favorite"),
                createdAt == null ? null : createdAt.toInstant().toString(),
                updatedAt == null ? null : updatedAt.toInstant().toString());
    }

    private boolean hasUniqueViolation(final Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SQLException sqlException && "23505".equals(sqlException.getSQLState())) {
                return true;
            }
            current = current.getCause();
        }

        return false;
    }
}
