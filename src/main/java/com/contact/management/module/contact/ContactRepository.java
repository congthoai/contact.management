package com.contact.management.module.contact;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.contact.management.module.contact.domain.Category;
import com.contact.management.module.contact.domain.Contact;
import com.contact.management.module.contact.domain.ContactFilter;
import com.contact.management.module.contact.domain.ContactSearchResult;

@Repository
public class ContactRepository {

    private final ContactPersistent persistent;

    public ContactRepository(final ContactPersistent persistent) {
        this.persistent = persistent;
    }

    Contact findById(final long id) {
        return persistent.findById(id);
    }

    ContactSearchResult search(final ContactFilter filter, final int size, final long offset) {
        return persistent.search(filter, size, offset);
    }

    List<Contact> findAll(final ContactFilter filter) {
        return persistent.findAll(filter);
    }

    boolean existsByEmail(final String email, final Long excludeId) {
        return persistent.existsByEmail(email, excludeId);
    }

    Contact create(final Contact contact) {
        return persistent.create(contact);
    }

    Contact update(final long id, final Contact contact) {
        return persistent.update(id, contact);
    }

    Contact toggleFavorite(final long id) {
        return persistent.toggleFavorite(id);
    }

    boolean delete(final long id) {
        return persistent.delete(id);
    }

    long count() {
        return persistent.count();
    }

    long countFavorites() {
        return persistent.countFavorites();
    }

    Map<Category, Long> countByCategory() {
        return persistent.countByCategory();
    }
}
