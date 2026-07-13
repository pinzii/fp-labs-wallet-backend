package com.fplabs.wallet.repository;

import com.fplabs.wallet.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    // El clásico buscador por ID para lecturas simples (sin bloqueo)
    Optional<Account> findById(UUID id);

    // NUESTRA ARMA SECRETA: El buscador con Bloqueo Pesimista
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithPessimisticLock(@Param("id") UUID id);
}