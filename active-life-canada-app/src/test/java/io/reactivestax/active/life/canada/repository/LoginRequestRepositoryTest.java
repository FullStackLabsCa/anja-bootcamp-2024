package io.reactivestax.active.life.canada.repository;

import io.reactivestax.active.life.canada.constant.TestData;
import io.reactivestax.active.life.canada.entity.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class LoginRequestRepositoryTest {

    @Autowired
    private LoginRequestRepository loginRequestRepository;

    @Test
    void testSave() {
        LoginRequest loginRequest = LoginRequest.builder()
                .loginToken(TestData.UUID_TOKEN_STRING)
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID).build();
        LoginRequest savedLoginRequest = loginRequestRepository.save(loginRequest);

        assertThat(savedLoginRequest).isNotNull();
        assertThat(savedLoginRequest.getLoginRequestId()).isNotNull();
    }

    @Test
    void testFindByLoginToken(){
        LoginRequest loginRequest = LoginRequest.builder()
                .loginToken(UUID.randomUUID().toString())
                .familyMemberId(TestData.FAMILY_MEMBER_ID_UUID).build();
        LoginRequest savedLoginRequest = loginRequestRepository.save(loginRequest);
        Optional<LoginRequest> loginRequestOptional = loginRequestRepository.findByLoginToken(savedLoginRequest.getLoginToken());

        assertThat(loginRequestOptional).isPresent();
        loginRequestOptional.ifPresent(loginRequest1 -> {
            assertThat(loginRequest1.getLoginRequestId()).isNotNull();
            assertThat(loginRequest1.getLoginToken()).isEqualTo(loginRequest.getLoginToken());
        });
    }
}
