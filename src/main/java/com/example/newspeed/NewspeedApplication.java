package com.example.newspeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
public class NewspeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewspeedApplication.class, args);
    }

}
