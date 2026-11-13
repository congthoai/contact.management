package com.contact.management.module.contact;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.contact.management.module.contact.domain.ContactResponse;

@Component
public class ContactCsvExporter {

    private static final Pattern FormulaStart = Pattern.compile("^[=+\\-@\\t\\r]");
    private static final Pattern PhoneLike = Pattern.compile("^[+\\-]?[0-9 ()\\-.]+$");

    private static final String Header =
            "First name,Last name,Email,Phone,Company,Job title,Address,Category,Favorite,Notes";

    public String export(final List<ContactResponse> contacts) {
        final StringBuilder csv = new StringBuilder(Header).append("\r\n");

        for (final ContactResponse contact : contacts) {
            final String row = Stream.of(
                            contact.firstName(),
                            contact.lastName(),
                            contact.email(),
                            contact.phone(),
                            contact.company(),
                            contact.jobTitle(),
                            contact.address(),
                            contact.category().label,
                            contact.favorite() ? "yes" : "no",
                            contact.notes())
                    .map(ContactCsvExporter::escape)
                    .collect(Collectors.joining(","));

            csv.append(row).append("\r\n");
        }

        return csv.toString();
    }

    private static String escape(final String rawValue) {
        if (rawValue == null) {
            return "";
        }

        String value = rawValue;
        if (FormulaStart.matcher(value).find() && !PhoneLike.matcher(value).matches()) {
            value = "'" + value;
        }

        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
