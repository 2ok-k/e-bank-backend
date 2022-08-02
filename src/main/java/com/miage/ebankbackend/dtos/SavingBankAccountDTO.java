package com.miage.ebankbackend.dtos;
import com.miage.ebankbackend.enums.AccountStatus;
import lombok.Data;

import java.util.Date;

@Data
public class SavingBankAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status; //AccountStatus: est un enumérateur de tous les status du compte
    private CustomerDTO customerDTO;//Un compte appartient à un client
    private double interestRate;
}
