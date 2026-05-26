package com.fullstack.gstbillingwithinvoicemngmt.config;

import com.fullstack.gstbillingwithinvoicemngmt.service.UserInfoService;
import com.fullstack.gstbillingwithinvoicemngmt.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

    @Component
    @Slf4j
    public class JWTFilter extends OncePerRequestFilter {

        /**
         * Dependencies to be injected
         */
        private final JWTUtil jwtUtil;
        private final UserInfoService userInfoService;

        /**
         * A constructor used for constructor injection
         * <p>
         * Here use {@link Lazy} annotation for preventing circular dependencies
         *
         * @param jwtUtil         {@link JWTUtil}
         * @param userInfoService {@link UserInfoService}
         */
        public JWTFilter(@Lazy JWTUtil jwtUtil, @Lazy UserInfoService userInfoService) {
            this.jwtUtil = jwtUtil;
            this.userInfoService = userInfoService;
        }

        /**
         * The method will set {@link Authentication} to {@link SecurityContextHolder} class
         *
         * @param request     {@link HttpServletRequest}
         * @param response    {@link HttpServletResponse}
         * @param filterChain {@link FilterChain}
         */
        @Override
        @SneakyThrows
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
            long startTime = System.currentTimeMillis();

            try {
                String authHeader = request.getHeader("Authorization");
                String token = null;
                String userName = null;

                //Checking and Format
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                    userName = getUserName(token, response);
                }

                //Security
                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userInfoService.loadUserByUsername(userName);

                    if (Boolean.TRUE.equals(jwtUtil.validateToken(token, userDetails))) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                                UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    } else {
                        log.warn("Invalid JWT Token");
                        response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid JWT Token");
                    }
                }
                filterChain.doFilter(request, response);
            } finally {
                Long duration = System.currentTimeMillis() - startTime;
                log.info("A request from {} with HTTP method [{}] & request URL [{}] was completed in {} ms",
                        request.getRemoteAddr(), request.getMethod(), request.getRequestURI(), duration);
            }
        }

        /**
         * Private method for getting username from token
         *
         * @param token    jwt token
         * @param response {@link HttpServletResponse}
         * @return user name
         */
        @SneakyThrows
        private String getUserName(String token, HttpServletResponse response) {
            try {
                return jwtUtil.extractUsername(token);
            } catch (IllegalArgumentException e) {
                log.warn("Unable to get JWT token", e.getCause());
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Unable to get JWT token");
            } catch (ExpiredJwtException e) {
                log.warn("JWT Token Has Expired", e.getCause());
                response.sendError(HttpStatus.BAD_REQUEST.value(), "JWT Token Has Expired");
            } catch (MalformedJwtException e) {
                log.warn("Invalid JWT Token ", e.getCause());
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid JWT Token ");
            } catch (SignatureException e) {
                log.warn("JWT signature does not match locally computed signature", e.getCause());
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid JWT signature");
            } catch (Exception e) {
                log.warn("Server Error", e.getCause());
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");
            }
            return null;
        }
    }

