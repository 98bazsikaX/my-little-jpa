package com.example.databasemanager.common.filter;

/**
 * A date range filter with optional lower and upper bounds expressed as UTC epoch
 * milliseconds. Used by {@link AbstractFilter} for {@link FilterType#DATE_RANGE}
 * fields.
 *
 * @param from inclusive start-of-day in UTC epoch millis, or {@code null} for unbounded
 * @param to   inclusive end-of-day in UTC epoch millis, or {@code null} for unbounded
 */
public record DateRange(Long from, Long to) {
}
