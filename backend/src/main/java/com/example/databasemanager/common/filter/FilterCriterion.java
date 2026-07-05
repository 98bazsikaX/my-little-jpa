package com.example.databasemanager.common.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single filter criterion in a {@link FilterRequest}. The {@code value} field
 * is polymorphic: a JSON string for {@code LIKE}/{@code EQUALS}, a JSON object
 * with {@code from}/{@code to} keys for {@code DATE_RANGE}.
 */
@Getter
@Setter
@NoArgsConstructor
public class FilterCriterion {

    private FilterOperation operation;
    private String field;
    private Object value;

    public FilterCriterion(FilterOperation operation, String field, Object value) {
        this.operation = operation;
        this.field = field;
        this.value = value;
    }
}
