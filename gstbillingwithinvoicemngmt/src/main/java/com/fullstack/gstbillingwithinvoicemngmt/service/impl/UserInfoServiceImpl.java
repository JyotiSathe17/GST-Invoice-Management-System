package com.fullstack.gstbillingwithinvoicemngmt.service.impl;

import com.fullstack.gstbillingwithinvoicemngmt.dao.UserInfodao;
import com.fullstack.gstbillingwithinvoicemngmt.dto.LoginRequest;
import com.fullstack.gstbillingwithinvoicemngmt.dto.LoginResponse;
import com.fullstack.gstbillingwithinvoicemngmt.dto.UserRequest;
import com.fullstack.gstbillingwithinvoicemngmt.dto.UserResponse;
import com.fullstack.gstbillingwithinvoicemngmt.model.UserInfo;
import com.fullstack.gstbillingwithinvoicemngmt.service.UserInfoService;
import com.fullstack.gstbillingwithinvoicemngmt.util.JWTUtil;
import com.fullstack.gstbillingwithinvoicemngmt.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfodao userInfodao;
    private final PasswordEncoder encoder;
    private final AuthenticationManager manager;
    private final JWTUtil jwtUtil;

    public UserInfoServiceImpl(@Lazy UserInfodao userInfodao, @Lazy PasswordEncoder encoder,
                               @Lazy AuthenticationManager manager, @Lazy JWTUtil jwtUtil) {
        this.userInfodao = userInfodao;
        this.encoder = encoder;
        this.manager = manager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public UserResponse registerUser(UserRequest request) {
        userInfodao.checkUser(request.userName(), request.email());

        UserInfo info = Mapper.map(request, UserInfo.class);
        log.debug("User details: {}", info);
        info.setPassword(encoder.encode(request.password()));
        info = userInfodao.saveUser(info);

        return Mapper.map(info, UserResponse.class);
    }

    @Override
    public LoginResponse validateUser(LoginRequest request) {
        manager.authenticate(new UsernamePasswordAuthenticationToken(request.userName(), request.password()));

        UserInfo info = userInfodao.getUser(request.userName());
        UserResponse user = Mapper.map(info, UserResponse.class);
        log.debug("User '{}' found.. UserDetails: {}", user.userName(), user);
        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token, user);
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo info = userInfodao.getUser(username);
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ADMIN"));
        return new User(info.getUserName(), info.getPassword(), authorities);
    }
}
