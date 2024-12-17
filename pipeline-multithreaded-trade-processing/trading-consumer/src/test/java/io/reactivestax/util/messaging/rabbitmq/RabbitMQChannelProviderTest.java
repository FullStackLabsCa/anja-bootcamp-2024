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
import java.util.concurrent.TimeoutException;

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
    void testGetReceiverChannel() {
        Channel senderChannel = rabbitMQChannelProvider.getReceiverChannel("queue_name");
        assertNotNull(senderChannel);
    }

    @Test
    void testGetReceiverChannelWithIOException() throws IOException {
        doReturn(channel).when(connection).createChannel();
        doThrow(IOException.class).when(channel).queueDeclare(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), any());
        rabbitMQChannelProviderWithMocks.getReceiverChannel("queue_name");
        assertThrows(IOException.class, () -> channel.queueDeclare(anyString(), anyBoolean(), anyBoolean(), anyBoolean(), any()));
    }

    @Test
    void testGetReceiverChannelWithTimeoutException() throws IOException {
        doReturn(channel).when(connection).createChannel();
        doThrow(TimeoutException.class).when(channel).exchangeDeclare(anyString(), anyString());
        rabbitMQChannelProviderWithMocks.getReceiverChannel("queue_name");
        assertThrows(TimeoutException.class, () -> channel.exchangeDeclare(anyString(), anyString()));
    }
}
