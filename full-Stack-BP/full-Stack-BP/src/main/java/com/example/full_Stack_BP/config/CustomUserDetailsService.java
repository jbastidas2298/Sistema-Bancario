package com.example.full_Stack_BP.config;

import com.example.full_Stack_BP.domain.Client;
import com.example.full_Stack_BP.enums.EnumError;
import com.example.full_Stack_BP.handler.CustomException;
import com.example.full_Stack_BP.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String identification)
            throws UsernameNotFoundException {

        Client client = clientRepository.findByIdentification(identification)
                .orElseThrow(() ->
                        new CustomException(EnumError.CLIENT_NOT_FOUND));

        if (!client.getStatus()) {
            throw new CustomException(EnumError.CLIENT_INACTIVE);
        }

        return User.builder()
                .username(client.getIdentification())
                .password(client.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
