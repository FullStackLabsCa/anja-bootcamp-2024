package io.reactivestax.ems;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmsAppApplicationTest {

    @Test
    void contextLoads() {
        // This test will pass if the application context starts successfully
        EmsAppApplication.main(new String[] {});
    }
}