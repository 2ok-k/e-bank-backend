package com.miage.ebankbackend.entities;

import com.miage.ebankbackend.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType type;//L'operation peut etre un debit ou un  credit donc on va utiliser les énumerateurs
    @ManyToOne
    private BankAccount bankAccount;//Une opération concerne un compte

    private String description;
}
