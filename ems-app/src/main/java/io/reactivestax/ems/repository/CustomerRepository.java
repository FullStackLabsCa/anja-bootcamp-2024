package io.reactivestax.ems.repository;

import io.reactivestax.ems.domain.Customer;
import io.reactivestax.ems.enums.OtpLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Query("Select c from Customer c where otpLock = ?1")
    List<Customer> findAllWithOtpLock(OtpLock otpLock);
}
