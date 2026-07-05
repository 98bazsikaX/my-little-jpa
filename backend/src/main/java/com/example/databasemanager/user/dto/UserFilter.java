package com.example.databasemanager.user.dto;

import com.example.databasemanager.common.filter.AbstractFilter;
import com.example.databasemanager.common.filter.DateRange;
import com.example.databasemanager.common.filter.FilterField;
import com.example.databasemanager.common.filter.FilterType;
import com.example.databasemanager.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Filter DTO for {@link User} queries. All fields are optional. String fields
 * use case-insensitive LIKE matching; date fields use UTC date range matching.
 */
@Getter
@Setter
@Builder
public class UserFilter extends AbstractFilter<User> {

    @FilterField
    private String userName;

    @FilterField
    private String email;

    @FilterField
    private String firstName;

    @FilterField
    private String lastName;

    @FilterField(type = FilterType.DATE_RANGE)
    private DateRange created;

    @FilterField(type = FilterType.DATE_RANGE)
    private DateRange lastLogin;

    @FilterField(type = FilterType.DATE_RANGE)
    private DateRange updated;
}
