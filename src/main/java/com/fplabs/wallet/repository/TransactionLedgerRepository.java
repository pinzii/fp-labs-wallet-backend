package com.fplabs.wallet.repository;

import com.fplabs.wallet.entity.TransactionLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TransactionLedgerRepository extends JpaRepository<TransactionLedger, UUID> {

    // Spring crea el "SELECT * FROM transaction_ledger WHERE account_id = ?"
    List<TransactionLedger> findByAccountId(UUID accountId);
}