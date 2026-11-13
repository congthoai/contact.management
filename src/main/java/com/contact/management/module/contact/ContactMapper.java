package com.contact.management.module.contact;

import java.util.Locale;

import org.springframework.stereotype.Component;

import com.contact.management.module.contact.domain.Contact;
import com.contact.management.module.contact.domain.ContactForm;
import com.contact.management.module.contact.domain.ContactResponse;

@Component
public class ContactMapper {

    public Contact toContact(final ContactForm form) {
        return new Contact(
                0,
                trim(form.firstName()),
                blankToNull(form.lastName()),
                normaliseEmail(form.email()),
                blankToNull(form.phone()),
                blankToNull(form.company()),
                blankToNull(form.jobTitle()),
                blankToNull(form.address()),
                blankToNull(form.notes()),
                form.category(),
                form.favorite(),
                null,
                null);
    }

    public ContactResponse toResponse(final Contact contact) {
        return new ContactResponse(
                contact.id(),
                contact.firstName(),
                contact.lastName(),
                contact.fullName(),
                contact.email(),
                contact.phone(),
                contact.company(),
                contact.jobTitle(),
                contact.address(),
                contact.notes(),
                contact.category(),
                contact.favorite(),
                contact.createdAt(),
                contact.updatedAt());
    }

    public static String normaliseEmail(final String email) {
        final String value = blankToNull(email);
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }

    private String trim(final String value) {
        return value == null ? null : value.trim();
    }

    private static String blankToNull(final String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
