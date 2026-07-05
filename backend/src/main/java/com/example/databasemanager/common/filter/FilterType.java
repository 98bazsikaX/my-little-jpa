package com.example.databasemanager.common.filter;

/** SQL operation used by {@link FilterField} to build a {@code Specification} predicate. */
public enum FilterType {
    /** Case-insensitive partial match ({@code LIKE %value%}). */
    LIKE,
    /** Exact equality match. */
    EQUALS,
    /** Date range with optional from/to bounds. */
    DATE_RANGE
}
