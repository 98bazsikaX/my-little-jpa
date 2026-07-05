package com.example.databasemanager.common.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field in an {@link AbstractFilter} subclass as a filterable column.
 * The base class reads annotated fields via reflection and builds a JPA
 * {@code Specification} automatically.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterField {

    /** Override column name. Defaults to the field name. */
    String value() default "";

    /** SQL operation type for this filter. Defaults to {@link FilterType#LIKE}. */
    FilterType type() default FilterType.LIKE;
}
