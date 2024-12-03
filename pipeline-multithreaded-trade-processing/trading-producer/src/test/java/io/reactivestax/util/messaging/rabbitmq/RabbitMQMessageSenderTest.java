package io.reactivestax.util.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.type.exception.MessageDeliveryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQMessageSenderTest {

    @Mock
    RabbitMQChannelProvider rabbitMQChannelProviderMock;

    @Mock
    Channel channelMock;

    @InjectMocks
    RabbitMQMessageSender rabbitMQMessageSender = RabbitMQMessageSender.getInstance();

    @AfterEach
    void tearDown() {
        Mockito.reset(rabbitMQChannelProviderMock, channelMock);
    }

    @Test
    void testSendMessage() throws IOException {
        try (MockedStatic<RabbitMQChannelProvider> rabbitMQChannelProviderMockedStatic = mockStatic(RabbitMQChannelProvider.class)) {
            rabbitMQChannelProviderMockedStatic.when(RabbitMQChannelProvider::getInstance).thenReturn(rabbitMQChannelProviderMock);
            doReturn(channelMock).when(rabbitMQChannelProviderMock).getSenderChannel();
            doNothing().when(channelMock).basicPublish(any(), any(), any(), any());
            rabbitMQMessageSender.sendMessage("queueName", "message");
            verify(rabbitMQChannelProviderMock, times(1)).getSenderChannel();
            verify(channelMock, times(1)).basicPublish(any(), any(), any(), any());
        }
    }

    @Test
    void testSendMessageWithIOException() throws IOException {
        try (MockedStatic<RabbitMQChannelProvider> rabbitMQChannelProviderMockedStatic = mockStatic(RabbitMQChannelProvider.class)) {
            rabbitMQChannelProviderMockedStatic.when(RabbitMQChannelProvider::getInstance).thenReturn(rabbitMQChannelProviderMock);
            doReturn(channelMock).when(rabbitMQChannelProviderMock).getSenderChannel();
            doThrow(IOException.class).when(channelMock).basicPublish(any(), any(), any(), any());
            assertThrows(MessageDeliveryException.class, () -> rabbitMQMessageSender.sendMessage("queueName", "message"));
            verify(rabbitMQChannelProviderMock, times(1)).getSenderChannel();
            verify(channelMock, times(1)).basicPublish(any(), any(), any(), any());
        }
    }
}
