package registration.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.stereotype.Component;
import registration.model.Role;
import registration.model.User;
import registration.service.TokenAuthenticationService;

import javax.servlet.FilterChain;
import java.util.stream.Collectors;


public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
    TokenAuthenticationService tokenAuthenticationService;

    public JWTLoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
    }

    public JWTLoginFilter(String url, AuthenticationManager authManager,TokenAuthenticationService tokenAuthenticationService) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
        this.tokenAuthenticationService=tokenAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {
        AccountCredentials creds = new ObjectMapper()
                .readValue(req.getInputStream(), AccountCredentials.class);
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getEmail(),
                        creds.getPassword(),
                        Collections.emptyList()
                )
        );
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {
        User user=new User();
        user.setEmail(auth.getName());
        user.setRoles(auth.getAuthorities().stream().map(s-> new Role(s.getAuthority())).collect(Collectors.toList()));
        tokenAuthenticationService
                .addAuthentication(res, user);
        SecurityContext context=SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }
}

