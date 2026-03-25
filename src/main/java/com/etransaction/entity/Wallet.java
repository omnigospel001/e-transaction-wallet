package com.etransaction.entity;


import com.etransaction.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Table(name = "wallet")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @Positive(message = "amount must be positive")
    @Min(value = 100, message = "Amount must not be less than 100 Naira")
    @Max(value = 5_000_000, message = "Amount must not be greater than 5 Million")
    private BigDecimal amount;

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 30, message = "First Name must be between 3 to 30 letters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 30, message = "Last Name must be between 3 to 30 letters")
    private String lastName;

    @NotNull(message = "Account Number is required")
    private Long accountNumber;

    @NotBlank(message = "Email is required")
    @Size(min = 3, max = 30)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(min = 3, max = 30)
    private String phone;

    private String transactionId;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

}
