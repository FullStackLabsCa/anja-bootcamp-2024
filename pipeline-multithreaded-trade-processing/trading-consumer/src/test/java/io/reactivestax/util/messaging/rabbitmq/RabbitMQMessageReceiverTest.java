package io.reactivestax.util.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQMessageReceiverTest {
    @Mock
    private RabbitMQChannelProvider rabbitMQChannelProviderMock;
    @Mock
    private Channel channelMock;
    @Mock
    private Envelope envelopeMock;
    @Mock
    GetResponse getResponseMock;
    @InjectMocks
    private RabbitMQMessageReceiver rabbitMQMessageReceiver;

    @Test
    void testReceiveMessage() throws IOException {
        try (MockedStatic<RabbitMQChannelProvider> rabbitMQChannelProviderMockedStatic = mockStatic(RabbitMQChannelProvider.class)) {
            rabbitMQChannelProviderMockedStatic.when(RabbitMQChannelProvider::getInstance).thenReturn(rabbitMQChannelProviderMock);
            when(rabbitMQChannelProviderMock.getReceiverChannel(anyString())).thenReturn(channelMock);
            when(channelMock.basicGet(anyString(), anyBoolean())).thenReturn(getResponseMock);
            when(getResponseMock.getBody()).thenReturn(new byte[10]);
            when(getResponseMock.getEnvelope()).thenReturn(envelopeMock);
            when(envelopeMock.getDeliveryTag()).thenReturn(1L);
            doNothing().when(channelMock).basicAck(anyLong(), anyBoolean());
            rabbitMQMessageReceiver = RabbitMQMessageReceiver.getInstance();
            rabbitMQMessageReceiver.receiveMessage("string");
            verify(rabbitMQChannelProviderMock, atMostOnce()).getReceiverChannel(anyString());
            verify(channelMock, atMostOnce()).basicGet(anyString(), anyBoolean());
            verify(getResponseMock, atMostOnce()).getBody();
            verify(getResponseMock, atMostOnce()).getEnvelope();
            verify(envelopeMock, atMostOnce()).getDeliveryTag();
            verify(channelMock, atMostOnce()).basicAck(anyLong(), anyBoolean());
            assertNotNull(rabbitMQMessageReceiver.getResponse());
        }
    }

    @Test
    void testReceiveMessageWithNullResponse() throws IOException {
        try (MockedStatic<RabbitMQChannelProvider> rabbitMQChannelProviderMockedStatic = mockStatic(RabbitMQChannelProvider.class)) {
            rabbitMQChannelProviderMockedStatic.when(RabbitMQChannelProvider::getInstance).thenReturn(rabbitMQChannelProviderMock);
            when(rabbitMQChannelProviderMock.getReceiverChannel(anyString())).thenReturn(channelMock);
            when(channelMock.basicGet(anyString(), anyBoolean())).thenReturn(null);
            assertTrue(rabbitMQMessageReceiver.receiveMessage("string").isEmpty());
            verify(rabbitMQChannelProviderMock, atMostOnce()).getReceiverChannel(anyString());
            verify(channelMock, atMostOnce()).basicGet(anyString(), anyBoolean());
            assertNull(rabbitMQMessageReceiver.getResponse());
        }
    }

    @Test
    void testReceiveMessageWithException() throws IOException {
        try (MockedStatic<RabbitMQChannelProvider> rabbitMQChannelProviderMockedStatic = mockStatic(RabbitMQChannelProvider.class)) {
            rabbitMQChannelProviderMockedStatic.when(RabbitMQChannelProvider::getInstance).thenReturn(rabbitMQChannelProviderMock);
            when(rabbitMQChannelProviderMock.getReceiverChannel(anyString())).thenReturn(channelMock);
            doThrow(IOException.class).when(channelMock).basicGet(anyString(), anyBoolean());
            assertTrue(rabbitMQMessageReceiver.receiveMessage("string").isEmpty());
            verify(rabbitMQChannelProviderMock, atMostOnce()).getReceiverChannel(anyString());
            verify(channelMock, atMostOnce()).basicGet(anyString(), anyBoolean());
            assertNull(rabbitMQMessageReceiver.getResponse());
        }
    }
}
