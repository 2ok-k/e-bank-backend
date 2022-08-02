package com.miage.ebankbackend.services;

import com.miage.ebankbackend.dtos.*;
import com.miage.ebankbackend.entities.*;
import com.miage.ebankbackend.enums.OperationType;
import com.miage.ebankbackend.exceptions.BalanceNotSufficientException;
import com.miage.ebankbackend.exceptions.BankAccountNotFoundException;
import com.miage.ebankbackend.exceptions.CustomerNotFoundException;
import com.miage.ebankbackend.mappers.BankAccountMapperImpl;
import com.miage.ebankbackend.repositories.AccountOperationRepository;
import com.miage.ebankbackend.repositories.BankAccountRepository;
import com.miage.ebankbackend.repositories.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j //Faire la journalisation(loguer des messages)
public class BankAccountServiceImpl implements BankAccountService{

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    private final BankAccountMapperImpl bankAccountMapper;

    public BankAccountServiceImpl(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository, BankAccountMapperImpl bankAccountMapper) {
        this.customerRepository = customerRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
        this.bankAccountMapper = bankAccountMapper;
    }


    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer");
        Customer customer = bankAccountMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);//On cree un nouveau client
        return bankAccountMapper.fromCustomer(savedCustomer);//On retourne ce client
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);//Rechercher le client par son ID,s'il  n'existe pas on met null

        //Si le client n'existe pas
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);

        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return bankAccountMapper.fromCurrentAccount(savedBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);//Rechercher le client par son ID,s'il  n'existe pas on met null

        //Si le client n'existe pas
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");

        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);

        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return bankAccountMapper.fromSavingAccount(savingAccount);
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> bankAccountMapper.fromCustomer(customer)).collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() ->new BankAccountNotFoundException("Bank account not found"));

        //Si c'est une instance de savingaccount
        if (bankAccount instanceof SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return bankAccountMapper.fromSavingAccount(savingAccount);
        }
        //dans le cas contraire
        else {
            CurrentAccount savingAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.fromCurrentAccount(savingAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() ->new BankAccountNotFoundException("Bank account not found"));

        if (bankAccount.getBalance()< amount)
            throw new BalanceNotSufficientException("Balance not sufficient");

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());

        accountOperation.setBankAccount(bankAccount);//Le compte sur lequel se passe l'opération

        accountOperationRepository.save(accountOperation);//Enregistrer l'opération
        bankAccount.setBalance(bankAccount.getBalance()-amount);//mise à jour du solde après un débit
        bankAccountRepository.save(bankAccount);//Enregistrement de la mise à jour
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() ->new BankAccountNotFoundException("Bank account not found"));

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());

        accountOperation.setBankAccount(bankAccount);//Le compte sur lequel se passe l'opération

        accountOperationRepository.save(accountOperation);//Enregistrer l'opération
        bankAccount.setBalance(bankAccount.getBalance()+amount);//mise à jour du solde après un crédit
        bankAccountRepository.save(bankAccount);//Enregistrement de la mise à jour

    }

    @Override
    public void transfert(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {

        debit(accountIdSource,amount,"Transfert to " + accountIdDestination);
        credit(accountIdDestination,amount,"Transfert from " + accountIdSource);
    }

    @Override //pour l'utiliser dans l'interface
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();

        List<BankAccountDTO>bankAccountDTOS  = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount){
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return bankAccountMapper.fromSavingAccount(savingAccount);
            }
            else {
                CurrentAccount savingAccount = (CurrentAccount) bankAccount;
                return bankAccountMapper.fromCurrentAccount(savingAccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer =  customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        return bankAccountMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Updating customer");
        Customer customer = bankAccountMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);//On cree un nouveau client
        return bankAccountMapper.fromCustomer(savedCustomer);//On retourne ce client
    }

    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op -> bankAccountMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null) throw new BankAccountNotFoundException("Account not found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> bankAccountMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.searchCustomer(keyword);
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> bankAccountMapper.fromCustomer(customer)).collect(Collectors.toList());
        return customerDTOS;
    }
}
