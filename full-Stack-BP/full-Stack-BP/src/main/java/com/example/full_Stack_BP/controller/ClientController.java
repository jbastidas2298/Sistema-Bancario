package com.example.full_Stack_BP.controller;

import com.example.full_Stack_BP.dto.request.ClientRequestDTO;
import com.example.full_Stack_BP.dto.response.ClientResponseDTO;
import com.example.full_Stack_BP.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Clients", description = "Client management endpoints")
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Create a new client")
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(
            @RequestBody ClientRequestDTO request) {

        return new ResponseEntity<>(
                clientService.createClient(request),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Get client by id")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(
            @PathVariable Long id) {

        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @Operation(summary = "Get client by identification")
    @GetMapping("/identification/{identification}")
    public ResponseEntity<ClientResponseDTO> getByIdentification(
            @PathVariable String identification) {

        return ResponseEntity.ok(
                clientService.getClientByIdentification(identification)
        );
    }

    @Operation(summary = "Get all clients")
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @Operation(summary = "Get all active clients")
    @GetMapping("/active")
    public ResponseEntity<List<ClientResponseDTO>> getActiveClients() {
        return ResponseEntity.ok(clientService.getActiveClients());
    }

    @Operation(summary = "Update client")
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable Long id,
            @RequestBody ClientRequestDTO request) {

        return ResponseEntity.ok(
                clientService.updateClient(id, request)
        );
    }

    @Operation(summary = "Deactivate client")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ClientResponseDTO> deactivateClient(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                clientService.deactivateClient(id)
        );
    }

    @Operation(summary = "Delete client")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Long id) {

        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search clients by name")
    @GetMapping("/search")
    public ResponseEntity<List<ClientResponseDTO>> searchByName(
            @RequestParam String name) {

        return ResponseEntity.ok(
                clientService.searchClientsByName(name)
        );
    }
}