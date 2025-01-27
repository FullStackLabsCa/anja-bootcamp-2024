package io.reactivestax.message.processor.repository;

import io.reactivestax.message.processor.domain.EnsMessage;
import io.reactivestax.message.processor.enums.NotificationMethod;
import io.reactivestax.message.processor.respository.EnsMessageRepository;
import io.reactivestax.message.processor.util.DataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class EnsMessageRepositoryTest {

    @Autowired
    private EnsMessageRepository ensMessageRepository;

    @Test
    void testFindById() {
        EnsMessage ensMessage = EnsMessage.builder()
                .phone(DataProvider.CONTACT)
                .notificationMethod(NotificationMethod.SMS)
                .customerId(DataProvider.ID_STRING)
                .message(DataProvider.MESSAGE)
                .build();
        EnsMessage saved = ensMessageRepository.save(ensMessage);
        Optional<EnsMessage> ensMessageOptional = ensMessageRepository.findById(saved.getId());
        assertThat(ensMessageOptional).isPresent();
        ensMessageOptional.ifPresent(ensMessage1 -> {
            assertThat(ensMessage1.getMessage()).isEqualTo(saved.getMessage());
            assertThat(ensMessage1.getPhone()).isEqualTo(saved.getPhone());
            assertThat(ensMessage1.getCustomerId()).isEqualTo(saved.getCustomerId());
            assertThat(ensMessage1.getNotificationMethod()).isEqualTo(saved.getNotificationMethod());
        });
    }
}
