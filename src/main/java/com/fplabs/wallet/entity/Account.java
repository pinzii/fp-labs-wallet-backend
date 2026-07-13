package com.fplabs.wallet.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // En FinTech JAMÁS usamos Double o Float por la pérdida de precisión en
    // decimales.
    // BigDecimal es el estándar absoluto de la industria.
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(nullable = false, length = 3)
    private String currency;

    // Aquí está el pilar del Bloqueo Optimista (si quisiéramos usarlo después)
    @Version
    private Integer version;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}