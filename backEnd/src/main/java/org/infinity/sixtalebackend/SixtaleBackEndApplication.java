package org.infinity.sixtalebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class    SixtaleBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(SixtaleBackEndApplication.class, args);
    }
}
