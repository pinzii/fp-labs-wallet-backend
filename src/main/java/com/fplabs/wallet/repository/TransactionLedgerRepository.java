package com.fplabs.wallet.repository;

import com.fplabs.wallet.entity.TransactionLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionLedgerRepository extends JpaRepository<TransactionLedger, UUID> {
    // Aquí implementaremos luego la verificación de la llave de idempotencia con
    // Redis o DB
}