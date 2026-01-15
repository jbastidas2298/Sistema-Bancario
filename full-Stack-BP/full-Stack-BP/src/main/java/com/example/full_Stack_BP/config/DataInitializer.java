package com.example.full_Stack_BP.config;

import com.example.full_Stack_BP.domain.Client;
import com.example.full_Stack_BP.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initUsers() {
        return args -> {
            if (clientRepository.count() == 0) {
                Client client = Client.builder()
                        .name("Jose Lema")
                        .gender("M")
                        .age(30)
                        .identification("1317706123")
                        .address("Default Address")
                        .phone("0999999999")
                        .password(passwordEncoder.encode("1234"))
                        .status(true)
                        .build();

                clientRepository.save(client);
            }
        };
    }
}