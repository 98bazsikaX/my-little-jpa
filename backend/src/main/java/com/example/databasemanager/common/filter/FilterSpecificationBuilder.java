package com.example.databasemanager.common.filter;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builds a JPA {@link Specification} from a list of generic {@link FilterCriterion}
 * entries. Validates field names against the entity class hierarchy and produces
 * predicates with AND semantics. Replaces the annotation-driven
 * {@code AbstractFilter} approach.
 */
public final class FilterSpecificationBuilder {

    private static final Map<Class<?>, Set<String>> FIELD_NAME_CACHE = new ConcurrentHashMap<>();

    private FilterSpecificationBuilder() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Builds a combined {@link Specification} from the given criteria list.
     * Returns a match-all specification for null or empty input.
     *
     * @param entityClass the JPA entity class being queried
     * @param criteria    list of filter criteria, may be null or empty
     * @param <T>         entity type
     * @return combined specification with AND semantics, never null
     * @throws InvalidFilterException if a criterion references an unknown field
     */
    public static <T> Specification<T> build(Class<T> entityClass, List<FilterCriterion> criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return Specification.where(null);
        }

        validateAll(entityClass, criteria);

        List<Specification<T>> specs = new ArrayList<>();
        for (FilterCriterion c : criteria) {
            Specification<T> spec = switch (c.getOperation()) {
                case LIKE -> buildLike(c.getField(), c.getValue());
                case EQUALS -> buildEquals(c.getField(), c.getValue());
                case DATE_RANGE -> buildDateRange(c.getField(), c.getValue());
                default -> throw new InvalidFilterException(
                        "Unsupported operation: " + c.getOperation());
            };
            if (spec != null) {
                specs.add(spec);
            }
        }

        return Specification.allOf(specs);
    }

    // -- Validation --

    private static <T> void validateAll(Class<T> entityClass, List<FilterCriterion> criteria) {
        Set<String> validFields = getFieldNames(entityClass);
        for (FilterCriterion c : criteria) {
            if (c.getField() == null || c.getField().isBlank()) {
                throw new InvalidFilterException("Filter field name must not be blank");
            }
            if (!validFields.contains(c.getField())) {
                throw new InvalidFilterException(
                        "Invalid filter field: '" + c.getField()
                                + "'. Valid fields for " + entityClass.getSimpleName()
                                + ": " + validFields);
            }
        }
    }

    private static <T> Set<String> getFieldNames(Class<T> entityClass) {
        return FIELD_NAME_CACHE.computeIfAbsent(entityClass, clazz -> {
            Set<String> names = new HashSet<>();
            Class<?> current = clazz;
            while (current != null && current != Object.class) {
                for (Field field : current.getDeclaredFields()) {
                    names.add(field.getName());
                }
                current = current.getSuperclass();
            }
            return Collections.unmodifiableSet(names);
        });
    }

    // -- Predicate builders --

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> Specification<T> buildLike(String field, Object value) {
        if (!(value instanceof String s) || s.isBlank()) {
            return null;
        }
        String escaped = s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(field)),
                        "%" + escaped.toLowerCase(Locale.ROOT) + "%", '\\');
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> Specification<T> buildEquals(String field, Object value) {
        if (value == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> Specification<T> buildDateRange(String field, Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            return null;
        }

        Object fromObj = map.get("from");
        Object toObj = map.get("to");

        Long from = toLong(fromObj);
        Long to = toLong(toObj);

        if (from == null && to == null) {
            return null;
        }

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (from != null) {
                LocalDate fromDate = Instant.ofEpochMilli(from)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();
                LocalDateTime fromDateTime = fromDate.atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get(field), fromDateTime));
            }
            if (to != null) {
                LocalDate toDate = Instant.ofEpochMilli(to)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();
                LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();
                predicates.add(cb.lessThan(root.get(field), toDateTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Long toLong(Object obj) {
        if (obj instanceof Number n) {
            return n.longValue();
        }
        return null;
    }
}
