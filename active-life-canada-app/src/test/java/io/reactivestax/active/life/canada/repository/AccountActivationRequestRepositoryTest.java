package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.AccountActivationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class AccountActivationRequestRepositoryTest {

    @Autowired
    private AccountActivationRequestRepository accountActivationRequestRepository;

    @Test
    void testSaveAndFindByToken() {
        AccountActivationRequest accountActivationRequest = AccountActivationRequest.builder()
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID)
                .token(TestData.UUID_TOKEN)
                .build();
        AccountActivationRequest saved = accountActivationRequestRepository.save(accountActivationRequest);
        assertThat(saved).isNotNull();
        assertThat(saved.getAccountActivationRequestId()).isNotNull();
        assertThat(saved.getToken()).isEqualTo(accountActivationRequest.getToken());
        assertThat(saved.getFamilyMemberId()).isEqualTo(accountActivationRequest.getFamilyMemberId());

        Optional<AccountActivationRequest> accountActivationRequestOptional = accountActivationRequestRepository.findByToken(saved.getToken());
        assertThat(accountActivationRequestOptional).isPresent();
        accountActivationRequestOptional.ifPresent(accountActivationRequest1 -> {
            assertThat(accountActivationRequest1.getFamilyMemberId()).isEqualTo(accountActivationRequest.getFamilyMemberId());
            assertThat(accountActivationRequest1.getToken()).isEqualTo(accountActivationRequest.getToken());
        });
    }
}
