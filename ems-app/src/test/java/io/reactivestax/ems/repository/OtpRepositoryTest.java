package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.OtpMessage;
import io.reactivestax.ems.enums.NotificationMethod;
import io.reactivestax.ems.enums.OtpStatus;
import io.reactivestax.ems.util.DataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class OtpRepositoryTest {

    @Autowired
    private OtpRepository otpRepository;

    @Test
    void testSave() {
        OtpMessage otpEntity = getOtpEntity();
        OtpMessage saved = otpRepository.save(otpEntity);
        assertThat(saved).isNotNull();
        assertThat(saved).isInstanceOf(OtpMessage.class);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOtpStatus()).isEqualTo(otpEntity.getOtpStatus());
        assertThat(saved.getOtp()).isEqualTo(otpEntity.getOtp());
        assertThat(saved.getPhone()).isEqualTo(otpEntity.getPhone());
        assertThat(saved.getVerificationAttempt()).isEqualTo(otpEntity.getVerificationAttempt());
        assertThat(saved.getNotificationMethod()).isEqualTo(otpEntity.getNotificationMethod());
        assertThat(saved.getCustomerId()).isEqualTo(otpEntity.getCustomerId());
    }

    @Test
    void testSaveAll() {
        List<OtpMessage> otpMessageList = saveMultipleRecords();
        List<OtpMessage> otpMessageList1 = otpRepository.saveAll(otpMessageList);
        assertThat(otpMessageList).isEqualTo(otpMessageList1);
    }

    @Test
    void testFindAllByCustomerIdAndOtpStatus(){
        List<OtpMessage> otpMessageList = saveMultipleRecords();
        List<OtpMessage> allByCustomerIdAndOtpStatus = otpRepository.findAllByCustomerIdAndOtpStatus(DataProvider.ID_STRING, OtpStatus.GENERATED);
        assertThat(otpMessageList).isEqualTo(allByCustomerIdAndOtpStatus);
    }

    @Test
    void testFindNotDiscardedAndNotVerifiedOtpMessageByCustomerId(){
        List<OtpMessage> otpMessageList = saveMultipleRecords();
        List<OtpMessage> messageList = otpRepository.findNotDiscardedAndNotVerifiedOtpMessageByCustomerId(DataProvider.ID_STRING);
        assertThat(otpMessageList).isEqualTo(messageList);
    }

    private List<OtpMessage> saveMultipleRecords(){
        List<OtpMessage> otpMessageList = new ArrayList<>();
        otpMessageList.add(getOtpEntity());
        otpMessageList.add(getOtpEntity());
        otpMessageList.add(getOtpEntity());
        return otpRepository.saveAll(otpMessageList);
    }

    private OtpMessage getOtpEntity() {
        return OtpMessage.builder()
                .otpStatus(OtpStatus.GENERATED)
                .otp(DataProvider.OTP_1)
                .phone(DataProvider.CONTACT)
                .verificationAttempt(0)
                .notificationMethod(NotificationMethod.SMS)
                .customerId(DataProvider.ID_STRING)
                .build();
    }
}
