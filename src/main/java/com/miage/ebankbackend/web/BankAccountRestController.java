package com.miage.ebankbackend.web;

import com.miage.ebankbackend.dtos.*;
import com.miage.ebankbackend.entities.AccountOperation;
import com.miage.ebankbackend.exceptions.BalanceNotSufficientException;
import com.miage.ebankbackend.exceptions.BankAccountNotFoundException;
import com.miage.ebankbackend.mappers.BankAccountMapperImpl;
import com.miage.ebankbackend.repositories.AccountOperationRepository;
import com.miage.ebankbackend.services.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin("*")
public class BankAccountRestController {
    private final BankAccountService bankAccountService;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapperImpl bankAccountMapper;

    public BankAccountRestController(BankAccountService bankAccountService, AccountOperationRepository accountOperationRepository, BankAccountMapperImpl bankAccountMapper) {
        this.bankAccountService = bankAccountService;
        this.accountOperationRepository = accountOperationRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    @GetMapping("/account/{id}")
    public BankAccountDTO getBankAcount(@PathVariable(name = "id")String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts(){
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/account/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId){
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/account/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(@PathVariable String accountId, @RequestParam(name = "size",defaultValue = "5") int size, @RequestParam(name = "page",defaultValue = "0")int page) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }

    @PostMapping("/account/debit")
    private DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/account/credit")
    private CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/account/transfer")
    private void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfert(transferRequestDTO.getAccountSource(),transferRequestDTO.getAccountDestination(),transferRequestDTO.getAmount()); 
    }
}
