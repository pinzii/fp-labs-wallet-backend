package com.fplabs.wallet.controller;

import com.fplabs.wallet.controller.dto.TransferRequest;
import com.fplabs.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        try {
            // Ejecutamos la transferencia en nuestro motor transaccional
            walletService.transfer(
                    request.getFromAccountId(),
                    request.getToAccountId(),
                    request.getAmount(),
                    request.getIdempotencyKey());

            // Respuesta exitosa (HTTP 200)
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Transferencia procesada exitosamente"));

        } catch (IllegalArgumentException e) {
            // Errores de validación de negocio (HTTP 400)
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()));
        } catch (IllegalStateException e) {
            // Estado de negocio inválido, ej. fondos insuficientes (HTTP 422)
            return ResponseEntity.status(422).body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()));
        } catch (Exception e) {
            // Errores inesperados de servidor (HTTP 500)
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Ocurrió un error al procesar la transacción: " + e.getMessage()));
        }
    }
}