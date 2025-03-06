package io.reactivestax.active.life.canada.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.reactivestax.active.life.canada.constant.SecurityConstants;
import io.reactivestax.active.life.canada.constant.ShortConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;
    private final AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceOAuth2AuthorizedClientManager;

    private static final Function<GrantedAuthority, String> authToRoleFn =
            authority -> authority.getAuthority().replace(SecurityConstants.ROLE_PREFIX, "").toLowerCase();

    public String generateToken(String username) {
        UserDetails userDetails = userService.loadUserByUsername(username);
        List<String> claims = userDetails.getAuthorities().stream().map(authToRoleFn).toList();

        String token = JWT.create()
                .withSubject(username)
                .withClaim(SecurityConstants.SCOPES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
        log.info("JWT Token: {}", token);

        return token;
    }

    public String getAccessToken() {
        Optional<OAuth2AuthorizedClient> oAuth2AuthorizedClientOptional =
                Optional.ofNullable(authorizedClientServiceOAuth2AuthorizedClientManager.authorize(
                        OAuth2AuthorizeRequest.withClientRegistrationId(ShortConstant.OKTA)
                                .principal(ShortConstant.CLIENT).build()
                ));
        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientOptional.orElseThrow();

        return ShortConstant.BEARER + oAuth2AuthorizedClient.getAccessToken().getTokenValue();
    }
}
