package ru.andef.andefracing.backend.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andef.andefracing.backend.data.entities.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByPhone(String phone);

    Optional<Client> findByPhone(String phone);
}