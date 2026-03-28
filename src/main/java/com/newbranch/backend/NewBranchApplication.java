package com.newbranch.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NewBranchApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewBranchApplication.class, args);
    }
}
