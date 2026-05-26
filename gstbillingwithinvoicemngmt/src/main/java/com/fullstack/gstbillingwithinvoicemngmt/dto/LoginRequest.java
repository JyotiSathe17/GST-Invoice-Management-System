package com.fullstack.gstbillingwithinvoicemngmt.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank(message = "Please enter valid user name")
                           String userName,

                           @NotBlank(message = "Please enter valid password")
                           String password) {
}
