package com.example.full_Stack_BP.services;

import com.example.full_Stack_BP.dto.request.ClientRequestDTO;
import com.example.full_Stack_BP.dto.response.ClientResponseDTO;

import java.util.List;

public interface ClientService {
    ClientResponseDTO createClient(ClientRequestDTO clientRequest);
    ClientResponseDTO getClientById(Long id);
    ClientResponseDTO getClientByIdentification(String identification);
    List<ClientResponseDTO> getAllClients();
    List<ClientResponseDTO> getActiveClients();
    ClientResponseDTO updateClient(Long id, ClientRequestDTO clientRequest);
    void deleteClient(Long id);
    ClientResponseDTO deactivateClient(Long id);
    List<ClientResponseDTO> searchClientsByName(String name);
}