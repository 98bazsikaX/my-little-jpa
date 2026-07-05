package com.example.databasemanager.common.filter;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base class for filter DTOs that build JPA {@link Specification} predicates
 * via reflection. Subclasses declare filterable fields with {@link FilterField}
 * annotations. The {@link #toSpecification()} method reads annotations and
 * builds the corresponding predicates automatically.
 *
 * @param <T> the entity type being filtered
 */
public abstract class AbstractFilter<T> {

    /**
     * Builds a {@link Specification} from all non-null {@link FilterField}-annotated
     * fields in the subclass. Combines predicates with AND semantics.
     *
     * @return combined specification, never {@code null}
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    public Specification<T> toSpecification() {
        List<Specification<T>> specs = new ArrayList<>();

        for (Field field : this.getClass().getDeclaredFields()) {
            FilterField ann = field.getAnnotation(FilterField.class);
            if (ann == null) {
                continue;
            }

            field.setAccessible(true);
            Object value;
            try {
                value = field.get(this);
            } catch (IllegalAccessException e) {
                continue;
            }
            if (value == null) {
                continue;
            }

            String column = ann.value().isEmpty() ? field.getName() : ann.value();

            switch (ann.type()) {
                case LIKE -> {
                    Specification<T> spec = like(column, (String) value);
                    if (spec != null) {
                        specs.add(spec);
                    }
                }
                case EQUALS -> specs.add(equalTo(column, value));
                case DATE_RANGE -> {
                    Specification<T> spec = dateBetween(column, (DateRange) value);
                    if (spec != null) {
                        specs.add(spec);
                    }
                }
                default -> throw new IllegalArgumentException(
                        "Unsupported filter type: " + ann.type());
            }
        }

        return Specification.allOf(specs);
    }

    /**
     * Builds a case-insensitive LIKE predicate. Escapes {@code \}, {@code %}, and
     * {@code _} characters in the input value.
     */
    protected Specification<T> like(String field, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String escaped = value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return (root, query, cb) ->
            cb.like(cb.lower(root.get(field)), "%" + escaped.toLowerCase(Locale.ROOT) + "%", '\\');
    }

    /** Builds an exact equality predicate. */
    @SuppressWarnings("PMD.SuspiciousEqualsMethodName")
    protected Specification<T> equalTo(String field, Object value) {
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    /**
     * Builds a date-range predicate. Epoch-millis bounds are converted to UTC
     * dates. {@code from} is inclusive start-of-day; {@code to} is inclusive
     * end-of-day.
     */
    protected Specification<T> dateBetween(String field, DateRange range) {
        if (range == null || (range.from() == null && range.to() == null)) {
            return null;
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (range.from() != null) {
                LocalDate fromDate = Instant.ofEpochMilli(range.from())
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();
                LocalDateTime fromDateTime = fromDate.atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get(field), fromDateTime));
            }
            if (range.to() != null) {
                LocalDate toDate = Instant.ofEpochMilli(range.to())
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();
                LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();
                predicates.add(cb.lessThan(root.get(field), toDateTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
