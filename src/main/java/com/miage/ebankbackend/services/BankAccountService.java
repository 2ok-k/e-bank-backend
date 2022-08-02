package com.miage.ebankbackend.services;

import com.miage.ebankbackend.dtos.*;
import com.miage.ebankbackend.entities.*;
import com.miage.ebankbackend.exceptions.BalanceNotSufficientException;
import com.miage.ebankbackend.exceptions.BankAccountNotFoundException;
import com.miage.ebankbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    //CREATTION DES METHODES PRINCIPALES
    //Pour créer un client
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    //Pour créer un compte
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    //Pour consulter des clients
    List<CustomerDTO> listCustomers();
    //Pour consulter un compte
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    //faire une opération de débit
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    //faire une opération de crédit
    void credit(String accountId,double amount,String description) throws BankAccountNotFoundException;
    //faire un virement
    void transfert(String accountIdSource,String accountIdDestination,double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    //pour l'utiliser dans l'interface
    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomers(String keyword);
}
