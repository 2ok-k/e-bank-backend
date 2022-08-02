package com.miage.ebankbackend.entities;
import com.miage.ebankbackend.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //pour dire on a une héritage avec cette classe
@DiscriminatorColumn(name = "TYPE  ",length = 4) //spécifier qu'une colonne s'ajoutera en fonction du type de compte
@Entity
public class BankAccount {
    @Id
    private String id;
    private double balance;
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status; //AccountStatus: est un enumérateur de tous les status du compte
    @ManyToOne
    private Customer customer;//Un compte appartient à un client
    @OneToMany(mappedBy = "bankAccount",fetch = FetchType.LAZY)//LAZY:il envoi que ce qu'on lui demande|EAGER:Il envoi tout
    private List<AccountOperation> accountOperations;//un compte peut avoir plusieurs opérations
}
