package com.contact.management.module.contact;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.contact.management.module.contact.domain.Category;
import com.contact.management.module.contact.domain.ContactFilter;
import com.contact.management.module.contact.domain.ContactForm;
import com.contact.management.module.contact.domain.ContactPage;
import com.contact.management.module.contact.domain.ContactResponse;
import com.contact.management.module.contact.domain.ContactSort;
import com.contact.management.module.contact.domain.ContactStats;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactService service;
    private final ContactCsvExporter csvExporter;

    public ContactController(final ContactService service, final ContactCsvExporter csvExporter) {
        this.service = service;
        this.csvExporter = csvExporter;
    }

    @GetMapping
    public ContactPage search(
            @RequestParam(defaultValue = "") final String q,
            @RequestParam(required = false) final Category category,
            @RequestParam(defaultValue = "false") final boolean favorites,
            @RequestParam(defaultValue = "NAME") final ContactSort sort,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size) {
        return service.search(new ContactFilter(q, category, favorites, sort), page, size);
    }

    @GetMapping("/stats")
    public ContactStats getStats() {
        return service.getStats();
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<String> export(
            @RequestParam(defaultValue = "") final String q,
            @RequestParam(required = false) final Category category,
            @RequestParam(defaultValue = "false") final boolean favorites,
            @RequestParam(defaultValue = "NAME") final ContactSort sort) {
        final ContactFilter filter = new ContactFilter(q, category, favorites, sort);
        final List<ContactResponse> contacts = service.findAll(filter);
        final String csv = csvExporter.export(contacts);

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contacts.csv\"")
                .body(csv);
    }

    @GetMapping("/{id:[1-9][0-9]*}")
    public ContactResponse getById(@PathVariable final long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<ContactResponse> create(@Valid @RequestBody final ContactForm form) {
        final ContactResponse created = service.create(form);
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id:[1-9][0-9]*}")
    public ContactResponse update(@PathVariable final long id, @Valid @RequestBody final ContactForm form) {
        return service.update(id, form);
    }

    @PatchMapping("/{id:[1-9][0-9]*}/favorite")
    public ContactResponse toggleFavorite(@PathVariable final long id) {
        return service.toggleFavorite(id);
    }

    @DeleteMapping("/{id:[1-9][0-9]*}")
    public ResponseEntity<Void> delete(@PathVariable final long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
