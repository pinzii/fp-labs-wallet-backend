package com.fplabs.wallet.controller;

import com.fplabs.wallet.controller.dto.TransferRequest;
import com.fplabs.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
// Permitimos que el frontend de Angular (puerto 4200) consulte esta API sin
// errores de CORS
@CrossOrigin(origins = "http://localhost:4200")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> executeTransfer(@RequestBody TransferRequest request) {

        // 1. Ejecutamos la transferencia.
        // Si falla por negocio o base de datos, la excepción "sube" al
        // GlobalExceptionHandler automáticamente.
        walletService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                request.getIdempotencyKey());

        // 2. Si todo sale bien, retorna el 200 OK
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Transferencia procesada exitosamente"));
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getAccountBalance(@PathVariable UUID accountId) {

        BigDecimal balance = walletService.getBalance(accountId);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "accountId", accountId,
                "balance", balance));
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable UUID accountId) {

        java.util.List<?> transactions = walletService.getTransactionHistory(accountId);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "accountId", accountId,
                "transactions", transactions));
    }
}