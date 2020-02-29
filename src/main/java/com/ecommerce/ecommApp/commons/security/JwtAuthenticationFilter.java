package com.ecommerce.ecommApp.commons.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private JwtTokenProvider tokenProvider;
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        LoginRequest loginRequest = new LoginRequest();

        try {
            loginRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequest.class);

        } catch (IOException e) {
            loginRequest.setEmailId("");
            loginRequest.setPassword("");
            e.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(), loginRequest.getPassword(), new ArrayList<>());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) throws IOException {

        String token = tokenProvider.generateToken(authentication, new ArrayList<>());

        CustomerPrincipal customerDetails = (CustomerPrincipal) authentication.getPrincipal();
        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setCustomerId(customerDetails.getId());
        loginResponse.setCustomerName(customerDetails.getName());
        loginResponse.setEmailId(customerDetails.getEmail());
        loginResponse.setHeader(SecurityConstants.TOKEN_HEADER);
        loginResponse.setToken(SecurityConstants.TOKEN_PREFIX + token);

        response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(loginResponse));
    }
}