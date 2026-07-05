package com.example.databasemanager.common.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Top-level wrapper for the generic filter JSON body: {@code {"filters": [...]}}. */
@Getter
@Setter
public class FilterRequest {

    private List<FilterCriterion> filters;

    public FilterRequest() {
        this.filters = new ArrayList<>();
    }

    public FilterRequest(List<FilterCriterion> filters) {
        this.filters = filters;
    }
}
