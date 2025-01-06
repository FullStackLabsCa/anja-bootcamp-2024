package io.reactivestax.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"io.reactivestax.aspect", "io.reactivestax.service"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {
}
