package io.reactivestax.util.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.reactivestax.util.ApplicationPropertiesUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQChannelProviderTest {

    @Mock
    Channel channel;

    @Mock
    Connection connection;

    @InjectMocks
    private RabbitMQChannelProvider rabbitMQChannelProviderWithMocks;

    private final RabbitMQChannelProvider rabbitMQChannelProvider = RabbitMQChannelProvider.getInstance();

    @BeforeEach
    void setUp() {
        ApplicationPropertiesUtils applicationPropertiesUtils = ApplicationPropertiesUtils.getInstance(
                "applicationTest.properties");
        applicationPropertiesUtils.loadApplicationProperties("applicationTest.properties");
    }

    @AfterEach
    void tearDown(){
        Mockito.reset(connection, channel);
    }

    @Test
    void testGetSenderChannel() {
        Channel senderChannel = rabbitMQChannelProvider.getSenderChannel();
        assertNotNull(senderChannel);
    }

    @Test
    void testGetSenderChannelWithIOException() throws IOException {
        doReturn(channel).when(connection).createChannel();
        doThrow(IOException.class).when(channel).exchangeDeclare(anyString(), anyString());
        rabbitMQChannelProviderWithMocks.getSenderChannel();
        assertThrows(IOException.class, () -> channel.exchangeDeclare(anyString(), anyString()));
    }
}
