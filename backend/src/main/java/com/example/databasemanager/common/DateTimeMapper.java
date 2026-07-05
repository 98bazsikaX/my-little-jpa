package com.example.databasemanager.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Converts between {@link LocalDateTime} entities and Unix epoch milliseconds in
 * the API layer. All conversions assume UTC zone so that stored values are
 * timezone-agnostic.
 *
 * <p>Used by MapStruct mappers via {@code uses = \{DateTimeMapper.class\}}.
 */
public final class DateTimeMapper {

    private DateTimeMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Converts a UTC {@link LocalDateTime} to epoch milliseconds.
     *
     * @param dateTime the UTC date-time, may be {@code null}
     * @return epoch millis or {@code null} if input is {@code null}
     */
    public static Long toEpochMillis(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    /**
     * Converts epoch milliseconds to a UTC {@link LocalDateTime}.
     *
     * @param epochMillis Unix epoch milliseconds, may be {@code null}
     * @return UTC date-time or {@code null} if input is {@code null}
     */
    public static LocalDateTime toLocalDateTime(Long epochMillis) {
        if (epochMillis == null) {
            return null;
        }
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}
