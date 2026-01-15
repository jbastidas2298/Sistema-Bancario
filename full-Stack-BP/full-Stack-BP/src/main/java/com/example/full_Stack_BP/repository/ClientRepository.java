package com.example.full_Stack_BP.repository;

import com.example.full_Stack_BP.domain.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends BaseRepository<Client>{
    Optional<Client> findByIdentification(String identification);

    @Query("SELECT c FROM Client c WHERE c.status = true ORDER BY c.name")
    List<Client> findAllActiveClients();

    @Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Client> findByNameContainingIgnoreCase(@Param("name") String name);
}
