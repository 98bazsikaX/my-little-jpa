package com.example.databasemanager.common.filter;

/** SQL operation used by {@link FilterCriterion} to build a JPA {@code Specification} predicate. */
public enum FilterOperation {
    /** Case-insensitive partial match ({@code LIKE %value%}). */
    LIKE,
    /** Exact equality match. */
    EQUALS,
    /** Date range with optional from/to epoch-millis bounds. */
    DATE_RANGE
}
