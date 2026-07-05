package com.example.databasemanager.unit.common.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.databasemanager.common.filter.FilterCriterion;
import com.example.databasemanager.common.filter.FilterOperation;
import com.example.databasemanager.common.filter.FilterRequest;
import com.example.databasemanager.common.filter.FilterSpecificationBuilder;
import com.example.databasemanager.common.filter.InvalidFilterException;
import com.example.databasemanager.user.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;

class FilterSpecificationBuilderTest {

    @Test
    void shouldReturnNonNullSpecForNullCriteria() {
        var spec = FilterSpecificationBuilder.build(User.class, null);
        assertThat(spec).isNotNull();
    }

    @Test
    void shouldReturnNonNullSpecForEmptyCriteria() {
        var spec = FilterSpecificationBuilder.build(User.class, List.of());
        assertThat(spec).isNotNull();
    }

    @Test
    void shouldRejectBlankFieldName() {
        var c = new FilterCriterion(FilterOperation.LIKE, "  ", "value");
        assertThatThrownBy(() -> FilterSpecificationBuilder.build(User.class, List.of(c)))
                .isInstanceOf(InvalidFilterException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void shouldRejectNullFieldName() {
        var c = new FilterCriterion(FilterOperation.LIKE, null, "value");
        assertThatThrownBy(() -> FilterSpecificationBuilder.build(User.class, List.of(c)))
                .isInstanceOf(InvalidFilterException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void shouldRejectInvalidFieldName() {
        var c = new FilterCriterion(FilterOperation.LIKE, "nonexistentField", "value");
        assertThatThrownBy(() -> FilterSpecificationBuilder.build(User.class, List.of(c)))
                .isInstanceOf(InvalidFilterException.class)
                .hasMessageContaining("Invalid filter field")
                .hasMessageContaining("nonexistentField");
    }

    @Test
    void shouldAcceptValidUserFields() {
        var c = new FilterCriterion(FilterOperation.LIKE, "userName", "test");
        var spec = FilterSpecificationBuilder.build(User.class, List.of(c));
        assertThat(spec).isNotNull();
    }

    @Test
    void shouldAcceptInheritedFields() {
        var c = new FilterCriterion(FilterOperation.DATE_RANGE, "created",
                java.util.Map.of("from", 0L));
        var spec = FilterSpecificationBuilder.build(User.class, List.of(c));
        assertThat(spec).isNotNull();
    }

    @Test
    void shouldValidateAllCriteriaFailFast() {
        var valid = new FilterCriterion(FilterOperation.LIKE, "userName", "test");
        var invalid = new FilterCriterion(FilterOperation.LIKE, "badField", "x");
        assertThatThrownBy(() -> FilterSpecificationBuilder.build(User.class, List.of(valid, invalid)))
                .isInstanceOf(InvalidFilterException.class)
                .hasMessageContaining("badField");
    }
}
