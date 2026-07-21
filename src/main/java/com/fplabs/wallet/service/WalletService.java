package com.fplabs.wallet.service;

import com.fplabs.wallet.entity.Account;
import com.fplabs.wallet.entity.TransactionLedger;
import com.fplabs.wallet.repository.AccountRepository;
import com.fplabs.wallet.repository.TransactionLedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletService {

    private final AccountRepository accountRepository;
    private final TransactionLedgerRepository ledgerRepository;

    public WalletService(AccountRepository accountRepository, TransactionLedgerRepository ledgerRepository) {
        this.accountRepository = accountRepository;
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional // Garantiza atomicidad: si algo falla, se hace ROLLBACK automático
    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String idempotencyKey) {

        // 👇👇 ELIMINA EL ERROR INICIAL TEMPORALMENTE PARA VER QUÉ HAY EN LA BD 👇👇
        long totalCuentas = accountRepository.count();
        System.out.println("🚨🚨 TOTAL DE CUENTAS EN LA BD: " + totalCuentas);

        accountRepository.findAll().forEach(
                acc -> System.out.println("💳 Cuenta -> ID: " + acc.getId() + " | Saldo: " + acc.getBalance()));

        // 1. Validación básica de negocio
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a transferir debe ser mayor a cero");
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("No se permiten transferencias a la misma cuenta");
        }

        // 2. Prevención de Deadlocks: Ordenamiento de bloqueos por ID
        UUID firstLockId = fromAccountId.compareTo(toAccountId) < 0 ? fromAccountId : toAccountId;
        UUID secondLockId = firstLockId.equals(fromAccountId) ? toAccountId : fromAccountId;

        System.out.println("====== DEBUGGING TRANSFERENCIA ======");
        System.out.println("From ID recibido: " + fromAccountId);
        System.out.println("To ID recibido: " + toAccountId);
        System.out.println("First Lock ID calculado: " + firstLockId);
        System.out.println("======================================");

        // Adquirimos los bloqueos pesimistas en el orden estricto (PostgreSQL encola
        // aquí los hilos)
        Account firstAccount = accountRepository.findByIdWithPessimisticLock(firstLockId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + firstLockId));

        Account secondAccount = accountRepository.findByIdWithPessimisticLock(secondLockId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + secondLockId));

        // Reasignamos las referencias correctas para la lógica de negocio
        Account fromAccount = firstAccount.getId().equals(fromAccountId) ? firstAccount : secondAccount;
        Account toAccount = firstAccount.getId().equals(toAccountId) ? firstAccount : secondAccount;

        // 3. Verificar fondos suficientes
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Fondos insuficientes en la cuenta de origen");
        }

        // 4. Ejecutar la operación en los balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Guardamos los nuevos saldos
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // 5. Registrar los movimientos en el Libro Mayor (Ledger) - Inmutabilidad
        TransactionLedger debitEntry = TransactionLedger.builder()
                .accountId(fromAccountId)
                .amount(amount.negate()) // Negativo para débito
                .transactionType("TRANSFER_OUT")
                .status("COMPLETED")
                .idempotencyKey(idempotencyKey + "_DEBIT")
                .build();

        TransactionLedger creditEntry = TransactionLedger.builder()
                .accountId(toAccountId)
                .amount(amount) // Positivo para crédito
                .transactionType("TRANSFER_IN")
                .status("COMPLETED")
                .idempotencyKey(idempotencyKey + "_CREDIT")
                .build();

        ledgerRepository.save(debitEntry);
        ledgerRepository.save(creditEntry);
    }
}
