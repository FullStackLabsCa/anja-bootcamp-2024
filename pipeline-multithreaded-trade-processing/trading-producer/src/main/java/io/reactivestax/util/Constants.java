package io.reactivestax.util;

import lombok.Getter;

@Getter
public class Constants {
    public static final String RABBITMQ_MESSAGING_TECHNOLOGY = "rabbitmq";
    public static final String HIBERNATE_PERSISTENCE_TECHNOLOGY = "hibernate";
    public static final String JDBC_PERSISTENCE_TECHNOLOGY = "jdbc";
    public static final String INVALID_PERSISTENCE_TECHNOLOGY = "Invalid persistence technology.";
    public static final String INVALID_MESSAGING_TECHNOLOGY = "Invalid messaging technology.";
}
