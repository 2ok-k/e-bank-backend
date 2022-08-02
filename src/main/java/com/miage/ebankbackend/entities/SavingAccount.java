package com.miage.ebankbackend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("SA")
@Entity
//SavingAccount est un type de compte donc il hérite de BankAccount
public class SavingAccount extends BankAccount {
    private double interestRate;
}
