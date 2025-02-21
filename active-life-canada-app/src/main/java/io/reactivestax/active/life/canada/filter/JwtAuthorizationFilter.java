package io.reactivestax.active.life.canada.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.reactivestax.active.life.canada.constant.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthentication(request);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);
        if (token != null) {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()))
                    .build()
                    .verify(token.replace(SecurityConstants.TOKEN_PREFIX, ""));
            String user = decodedJWT.getSubject();
            List<SimpleGrantedAuthority> simpleGrantedAuthorities = decodedJWT.getClaim(SecurityConstants.SCOPES)
                    .asList(String.class)
                    .stream().map(claim -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + claim.toUpperCase()))
                    .toList();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, simpleGrantedAuthorities);
            }
        }

        return null;
    }
}
