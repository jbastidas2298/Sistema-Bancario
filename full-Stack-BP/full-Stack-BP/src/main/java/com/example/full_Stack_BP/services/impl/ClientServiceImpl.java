package com.example.full_Stack_BP.services.impl;

import com.example.full_Stack_BP.domain.Client;
import com.example.full_Stack_BP.dto.request.ClientRequestDTO;
import com.example.full_Stack_BP.dto.response.ClientResponseDTO;
import com.example.full_Stack_BP.enums.EnumError;
import com.example.full_Stack_BP.exception.ResourceNotFoundException;
import com.example.full_Stack_BP.handler.CustomException;
import com.example.full_Stack_BP.repository.ClientRepository;
import com.example.full_Stack_BP.services.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO clientRequest) {
        clientRepository.findByIdentification(clientRequest.getIdentification())
                .ifPresent(client -> {
                    log.error("Client with identification {} already exists",
                            clientRequest.getIdentification());
                    throw new CustomException(EnumError.CLIENT_ALREADY_EXISTING);
                });

        Client client = Client.builder()
                .name(clientRequest.getName())
                .gender(clientRequest.getGender())
                .age(clientRequest.getAge())
                .identification(clientRequest.getIdentification())
                .address(clientRequest.getAddress())
                .phone(clientRequest.getPhone())
                .password(passwordEncoder.encode(clientRequest.getPassword()))
                .status(clientRequest.getStatus() != null ? clientRequest.getStatus() : true)
                .build();

        Client savedClient = clientRepository.save(client);
        log.info("Client with id {} created successfully", savedClient.getId());
        return mapToResponseDTO(savedClient);
    }

    @Override
    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new CustomException(EnumError.CLIENT_NOT_FOUND));
        log.info("Client with id {} retrieved successfully", id);
        return mapToResponseDTO(client);
    }

    @Override
    public ClientResponseDTO getClientByIdentification(String identification) {
        Client client = clientRepository.findByIdentification(identification)
                .orElseThrow(() -> new CustomException(EnumError.CLIENT_NOT_FOUND));
        log.info("Client with identification {} retrieved successfully", identification);
        return mapToResponseDTO(client);
    }

    @Override
    public List<ClientResponseDTO> getAllClients() {
        log.info("Retrieving all clients");
        return clientRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientResponseDTO> getActiveClients() {
        log.info("Retrieving all active clients");
        return clientRepository.findAllActiveClients().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO clientRequest) {
        log.info("Updating client with id {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new CustomException(EnumError.CLIENT_NOT_FOUND));

        if (!client.getIdentification().equals(clientRequest.getIdentification())) {
            clientRepository.findByIdentification(clientRequest.getIdentification())
                    .ifPresent(existingClient -> {
                        log.error("Identification {} already exists",
                                clientRequest.getIdentification());
                        throw new CustomException(EnumError.CLIENT_ALREADY_EXISTING);
                    });
        }

        client.setName(clientRequest.getName());
        client.setGender(clientRequest.getGender());
        client.setAge(clientRequest.getAge());
        client.setIdentification(clientRequest.getIdentification());
        client.setAddress(clientRequest.getAddress());
        client.setPhone(clientRequest.getPhone());
        if (clientRequest.getPassword() != null && !clientRequest.getPassword().isEmpty()){
            client.setPassword(passwordEncoder.encode(clientRequest.getPassword()));
        }
        client.setStatus(clientRequest.getStatus());

        Client updatedClient = clientRepository.save(client);
        log.info("Client with id {} updated successfully", id);
        return mapToResponseDTO(updatedClient);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new CustomException(EnumError.CLIENT_NOT_FOUND));
        log.info("Deleting client with id {}", id);
        clientRepository.delete(client);
    }

    @Override
    public ClientResponseDTO deactivateClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new CustomException(EnumError.CLIENT_NOT_FOUND));
        log.info("Deactivating client with id {}", id);
        client.setStatus(false);
        Client updatedClient = clientRepository.save(client);
        log.info("Client with id {} deactivated successfully", id);
        return mapToResponseDTO(updatedClient);
    }

    @Override
    public List<ClientResponseDTO> searchClientsByName(String name) {
        log.info("Searching clients by name containing: {}", name);
        return clientRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ClientResponseDTO mapToResponseDTO(Client client) {
        log.info("Mapping Client entity to ClientResponseDTO for client id {}", client.getId());
        return ClientResponseDTO.builder()
                .id(client.getId())
                .name(client.getName())
                .gender(client.getGender())
                .age(client.getAge())
                .identification(client.getIdentification())
                .address(client.getAddress())
                .phone(client.getPhone())
                .status(client.getStatus())
                .build();
    }
}