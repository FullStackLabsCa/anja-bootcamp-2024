package io.reactivestax.active.life.canada.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.reactivestax.active.life.canada.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class JwtService {

    public String generateToken(String username){
        String token = JWT.create()
                .withSubject(username)
//                .withClaim("scopes", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));
        log.info("JWT Token: {}", token);

        return token;
    }
}
