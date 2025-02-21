package io.reactivestax.active.life.canada.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.reactivestax.active.life.canada.constant.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;

    private static final Function<GrantedAuthority, String> authToRoleFn =
            authority -> authority.getAuthority().replace("ROLE_", "").toLowerCase();

    public String generateTokenAndSetSecurityContext(String username) {

        UserDetails userDetails = userService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        List<String> claims = userDetails.getAuthorities().stream().map(authToRoleFn).toList();

        String token = JWT.create()
                .withSubject(username)
                .withClaim(SecurityConstants.SCOPES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
        log.info("JWT Token: {}", token);

        return token;
    }
}
