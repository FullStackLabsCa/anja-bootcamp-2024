package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.enums.OtpLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testSave() {
        Customer customerEntity = getCustomerEntity();
        Customer customer = customerRepository.save(customerEntity);
        assertThat(customer).isNotNull();
        assertThat(customer).isInstanceOf(Customer.class);
        assertThat(customer.getCustomerId()).isNotNull();
        assertThat(customer.getFirstName()).isEqualTo(customerEntity.getFirstName());
        assertThat(customer.getLastName()).isEqualTo(customerEntity.getLastName());
        assertThat(customer.getCreatedAt()).isEqualTo(customerEntity.getCreatedAt());
        assertThat(customer.getUpdatedAt()).isEqualTo(customerEntity.getUpdatedAt());
        assertThat(customer.getOtpLock()).isEqualTo(customerEntity.getOtpLock());
    }

    @Test
    void testFindById() {
        Customer customer = customerRepository.save(getCustomerEntity());
        Optional<Customer> optionalCustomer = customerRepository.findById(customer.getCustomerId());
        optionalCustomer.ifPresent(customer1 -> {
            assertThat(customer1).isNotNull();
            assertThat(customer1).isInstanceOf(Customer.class);
            assertThat(customer1.getCustomerId()).isNotNull();
            assertThat(customer1.getFirstName()).isEqualTo(customer.getFirstName());
            assertThat(customer1.getLastName()).isEqualTo(customer.getLastName());
            assertThat(customer1.getCreatedAt()).isEqualTo(customer.getCreatedAt());
            assertThat(customer1.getUpdatedAt()).isEqualTo(customer.getUpdatedAt());
            assertThat(customer1.getOtpLock()).isEqualTo(customer.getOtpLock());
        });
    }

    @Test
    void testFindAllWithOtpLock() {
        List<Customer> allWithOtpLock = customerRepository.findAllWithOtpLock(OtpLock.NOT_LOCKED);
        assertThat(allWithOtpLock.size()).isNotZero();
    }

    private Customer getCustomerEntity() {
        return Customer.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .otpLock(OtpLock.NOT_LOCKED)
                .build();
    }
}
