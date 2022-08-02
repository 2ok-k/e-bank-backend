package com.miage.ebankbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CA")
@Entity
//CurrentAccount est un type de compte donc il hérite de BankAccount
public class CurrentAccount extends BankAccount {
    private double overDraft;
}
