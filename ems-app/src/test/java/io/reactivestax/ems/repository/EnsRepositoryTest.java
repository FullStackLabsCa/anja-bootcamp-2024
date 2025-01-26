package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.EnsMessage;
import io.reactivestax.ems.enums.MessageStatus;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.util.DataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class EnsRepositoryTest {

    @Autowired
    private EnsRepository ensRepository;

    @Test
    void testSave() {
        EnsMessage ensMessage = EnsMessage.builder()
                .customerId(DataProvider.ID_STRING)
                .message(DataProvider.MESSAGE)
                .messageStatus(MessageStatus.NOT_SENT)
                .notificationMethod(NotificationMethod.SMS)
                .phone(DataProvider.CONTACT)
                .build();
        EnsMessage savedEnsMessage = ensRepository.save(ensMessage);
        assertThat(savedEnsMessage).isNotNull();
        assertThat(savedEnsMessage).isInstanceOf(EnsMessage.class);
        assertThat(savedEnsMessage.getId()).isNotNull();
        assertThat(savedEnsMessage.getCustomerId()).isEqualTo(ensMessage.getCustomerId());
        assertThat(savedEnsMessage.getMessage()).isEqualTo(ensMessage.getMessage());
        assertThat(savedEnsMessage.getMessageStatus()).isEqualTo(ensMessage.getMessageStatus());
        assertThat(savedEnsMessage.getNotificationMethod()).isEqualTo(ensMessage.getNotificationMethod());
        assertThat(savedEnsMessage.getPhone()).isEqualTo(ensMessage.getPhone());
    }
}
