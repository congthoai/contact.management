package com.contact.management.module.contact;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.contact.management.domain.ResponseCode;
import com.contact.management.domain.ResponseException;
import com.contact.management.module.contact.domain.Category;
import com.contact.management.module.contact.domain.Contact;
import com.contact.management.module.contact.domain.ContactFilter;
import com.contact.management.module.contact.domain.ContactPage;
import com.contact.management.module.contact.domain.ContactResponse;
import com.contact.management.module.contact.domain.ContactSearchResult;
import com.contact.management.module.contact.domain.ContactStats;
import com.contact.management.module.contact.domain.ContactForm;

@Service
public class ContactService {

    private static final int MaxPageSize = 100;

    private final ContactRepository repository;
    private final ContactMapper mapper;

    public ContactService(final ContactRepository repository, final ContactMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ContactPage search(final ContactFilter filter, final int page, final int size) {
        validatePage(page, size);

        final long offset = (long) page * size;
        final ContactSearchResult result = repository.search(filter, size, offset);
        final List<ContactResponse> content = result.contacts()
                .stream()
                .map(mapper::toResponse)
                .toList();
        final int totalPages = result.totalElements() == 0
                ? 0
                : (int) Math.ceil((double) result.totalElements() / size);

        return new ContactPage(content, new ContactPage.Page(size, result.totalElements(), totalPages, page));
    }

    public List<ContactResponse> findAll(final ContactFilter filter) {
        return repository.findAll(filter)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ContactResponse getById(final long id) {
        return mapper.toResponse(findContact(id));
    }

    public ContactResponse create(final ContactForm form) {
        final Contact contact = mapper.toContact(form);
        ensureEmailAvailable(contact.email(), null);

        try {
            return mapper.toResponse(repository.create(contact));
        } catch (final DuplicateKeyException exception) {
            throw duplicateEmail();
        }
    }

    public ContactResponse update(final long id, final ContactForm form) {
        findContact(id);

        final Contact contact = mapper.toContact(form);
        ensureEmailAvailable(contact.email(), id);

        try {
            final Contact updated = repository.update(id, contact);
            if (updated == null) {
                throw notFound(id);
            }

            return mapper.toResponse(updated);
        } catch (final DuplicateKeyException exception) {
            throw duplicateEmail();
        }
    }

    public ContactResponse toggleFavorite(final long id) {
        final Contact contact = repository.toggleFavorite(id);
        if (contact == null) {
            throw notFound(id);
        }

        return mapper.toResponse(contact);
    }

    public void delete(final long id) {
        if (!repository.delete(id)) {
            throw notFound(id);
        }
    }

    public ContactStats getStats() {
        final long total = repository.count();
        final long favorites = repository.countFavorites();
        final Map<Category, Long> byCategory = repository.countByCategory();

        return new ContactStats(total, favorites, byCategory);
    }

    private Contact findContact(final long id) {
        final Contact contact = repository.findById(id);
        if (contact == null) {
            throw notFound(id);
        }

        return contact;
    }

    private void ensureEmailAvailable(final String email, final Long excludeId) {
        if (email != null && repository.existsByEmail(email, excludeId)) {
            throw duplicateEmail();
        }
    }

    private void validatePage(final int page, final int size) {
        if (page < 0) {
            throw ResponseException.wrongParam("page");
        }

        if (size < 1 || size > MaxPageSize) {
            throw ResponseException.wrongParam("size");
        }
    }

    private ResponseException notFound(final long id) {
        return ResponseException.error(ResponseCode.NothingFound, "Contact with id " + id + " not found");
    }

    private ResponseException duplicateEmail() {
        return ResponseException.error(ResponseCode.Conflict, "Another contact already uses this email");
    }
}
