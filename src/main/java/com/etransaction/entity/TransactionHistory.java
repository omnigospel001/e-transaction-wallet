package com.etransaction.entity;

import com.etransaction.common.CommonEntity;
import com.etransaction.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Table(name = "transactions")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory extends CommonEntity {

    private BigDecimal amount;
    private String firstName;
    private String lastName;
    private Long accountNumber;
    private String email;

    private String phone;

    private String transactionId;

    @ManyToOne(optional = false)
    private User user;

    private String remark;

    private TransactionType transactionType;
}
