package ru.fintech.translator;

import org.springframework.boot.SpringApplication;

public class TestTranslatorFintechApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
