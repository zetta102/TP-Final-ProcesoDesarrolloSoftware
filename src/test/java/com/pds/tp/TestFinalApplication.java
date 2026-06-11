package com.pds.tp;

import org.springframework.boot.SpringApplication;

public class TestFinalApplication {

    static void main(String[] args) {
        SpringApplication.from(FinalApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
