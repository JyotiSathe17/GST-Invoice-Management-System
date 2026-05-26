package com.fullstack.gstbillingwithinvoicemngmt.service;

import com.fullstack.gstbillingwithinvoicemngmt.dto.LoginRequest;
import com.fullstack.gstbillingwithinvoicemngmt.dto.LoginResponse;
import com.fullstack.gstbillingwithinvoicemngmt.dto.UserRequest;
import com.fullstack.gstbillingwithinvoicemngmt.dto.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserInfoService extends UserDetailsService {
    UserResponse registerUser(UserRequest request);

    LoginResponse validateUser(LoginRequest request);
}
