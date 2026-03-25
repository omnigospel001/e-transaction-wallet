package com.etransaction.request;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @Positive(message = "Amount must be positive")
    @Min(value = 100, message = "Amount is too low")
    @Max(value = 5_000_000, message = "Amount is too high")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Account number is required")
    @Positive(message = "Must be positive number")
    private Long accountNumber;

    @Lob
    private String remark;

}
