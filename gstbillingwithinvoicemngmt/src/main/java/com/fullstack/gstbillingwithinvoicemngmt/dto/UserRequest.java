package com.fullstack.gstbillingwithinvoicemngmt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(@NotBlank(message = "Full name is must !!!")
                          String fullName,

                          @Email(message = "Email is must !!!!")
                          String email,

                          @NotBlank(message = "Please enter valid user name")
                          String userName,

                          @NotBlank(message = "Please enter valid password")
                          String password)  {
}
