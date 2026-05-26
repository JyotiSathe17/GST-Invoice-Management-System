package com.fullstack.gstbillingwithinvoicemngmt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties
public record UserResponse(Long id, String fullName, String email, String userName) {
}
