package com.miage.ebankbackend;

import com.miage.ebankbackend.dtos.*;
import com.miage.ebankbackend.entities.*;
import com.miage.ebankbackend.enums.AccountStatus;
import com.miage.ebankbackend.enums.OperationType;
import com.miage.ebankbackend.exceptions.BalanceNotSufficientException;
import com.miage.ebankbackend.exceptions.BankAccountNotFoundException;
import com.miage.ebankbackend.exceptions.CustomerNotFoundException;
import com.miage.ebankbackend.repositories.AccountOperationRepository;
import com.miage.ebankbackend.repositories.BankAccountRepository;
import com.miage.ebankbackend.repositories.CustomerRepository;
import com.miage.ebankbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EBankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBankBackendApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Hassane","Imane","Mohamed").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*9000,9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*9000,5.5, customer.getId());

                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts) {
                for (int i = 0; i < 10; i++) {
                    String accountId;
                    if (bankAccount instanceof SavingBankAccountDTO){
                        accountId = ((SavingBankAccountDTO) bankAccount).getId();
                    }
                    else {
                        accountId = ((CurrentBankAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId, 10000+Math.random()*120000,"Credit");
                    bankAccountService.debit(accountId, 10000+Math.random()*9000,"Debit");
                }
            }
        };
    }
}
