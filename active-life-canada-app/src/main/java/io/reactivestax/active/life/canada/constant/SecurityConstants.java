package io.reactivestax.active.life.canada.constant;

public class SecurityConstants {

    private SecurityConstants() {
    }

    public static final String SECRET = "TestSecrEtKeyF0rJwtHash1ng";
    public static final long EXPIRATION_TIME = 900_000_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SCOPES = "scopes";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String HEADER_STRING = "Authorization";
}
