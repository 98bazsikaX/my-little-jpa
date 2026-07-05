package com.example.databasemanager.common.filter;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractFilter<T> {

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
            }
        }

        return Specification.allOf(specs);
    }

    protected Specification<T> like(String field, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String escaped = value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return (root, query, cb) ->
            cb.like(cb.lower(root.get(field)), "%" + escaped.toLowerCase(Locale.ROOT) + "%", '\\');
    }

    @SuppressWarnings("PMD.SuspiciousEqualsMethodName")
    protected Specification<T> equalTo(String field, Object value) {
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    protected Specification<T> dateBetween(String field, DateRange range) {
        if (range == null || (range.from() == null && range.to() == null)) {
            return null;
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (range.from() != null) {
                LocalDateTime fromDateTime = range.from().atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get(field), fromDateTime));
            }
            if (range.to() != null) {
                LocalDateTime toDateTime = range.to().plusDays(1).atStartOfDay();
                predicates.add(cb.lessThan(root.get(field), toDateTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
