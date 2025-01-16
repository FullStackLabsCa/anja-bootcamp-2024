package io.reactivestax.tradingproducer.configuration;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
@ComponentScan(basePackages = "io.reactivestax.tradingproducer")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${activemq.broker.url}")
    private String brokerUrl;

    @Value("${activemq.broker.url}")
    private String brokerUsername;

    @Value("${activemq.broker.url}")
    private String brokerPassword;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(brokerUrl, brokerUsername, brokerPassword);
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        jmsTemplate.setPubSubDomain(false);
        return jmsTemplate;
    }
}
