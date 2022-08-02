package com.miage.ebankbackend.mappers;

import com.miage.ebankbackend.dtos.AccountOperationDTO;
import com.miage.ebankbackend.dtos.CurrentBankAccountDTO;
import com.miage.ebankbackend.dtos.CustomerDTO;
import com.miage.ebankbackend.dtos.SavingBankAccountDTO;
import com.miage.ebankbackend.entities.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {
    //Les mappers vont permettre de transférer les données d'un objet vers un autre objet
    public CustomerDTO fromCustomer(Customer customer){
        CustomerDTO customerDTO = new CustomerDTO();
        //copier les propriétés d'un objet vers un autre
        BeanUtils.copyProperties(customer,customerDTO);//customer: la source | destination : customerDTO
        //customerDTO.setId(customer.getId());
        //customerDTO.setName(customerDTO.getName());
        //customerDTO.setEmail(customerDTO.getEmail());
        return customerDTO;
    }
    //L'inverse de la première méthode
    public Customer fromCustomerDTO(CustomerDTO customerDTO){
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO,customer);
        return customer;
    }

    //Compte épargne
    public SavingBankAccountDTO fromSavingAccount(SavingAccount savingAccount){
        SavingBankAccountDTO savingBankAccountDTO = new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount,savingBankAccountDTO);
        savingBankAccountDTO.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));//le client
        savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
        return savingBankAccountDTO;
    }

    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO){
        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(savingBankAccountDTO,savingAccount);
        savingAccount.setCustomer(fromCustomerDTO(savingBankAccountDTO.getCustomerDTO()));//le client
        return savingAccount;
    }


    //Compte courant
    public CurrentBankAccountDTO fromCurrentAccount(CurrentAccount currentAccount){
        CurrentBankAccountDTO currentBankAccountDTO = new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentAccount,currentBankAccountDTO);
        currentBankAccountDTO.setCustomerDTO(fromCustomer(currentAccount.getCustomer()));//le client
        currentBankAccountDTO.setType(currentAccount.getClass().getSimpleName());
        return currentBankAccountDTO;
    }

    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO){
        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDTO,currentAccount);
        currentAccount.setCustomer(fromCustomerDTO(currentBankAccountDTO.getCustomerDTO()));//le client
        return currentAccount;
    }


    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDTO accountOperationDTO = new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation,accountOperationDTO);
        return accountOperationDTO;
    }

    public AccountOperation fromAccountOperationDTO(AccountOperationDTO accountOperationDTO){
        AccountOperation accountOperation = new AccountOperation();
        BeanUtils.copyProperties(accountOperationDTO,accountOperation);
        return accountOperation;
    }


}
