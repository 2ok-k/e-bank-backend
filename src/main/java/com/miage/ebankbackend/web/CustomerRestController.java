package com.miage.ebankbackend.web;

import com.miage.ebankbackend.dtos.CustomerDTO;
import com.miage.ebankbackend.entities.Customer;
import com.miage.ebankbackend.exceptions.CustomerNotFoundException;
import com.miage.ebankbackend.services.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    private final BankAccountService bankAccountService;

    public CustomerRestController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/customers")
    public List<CustomerDTO> customers (){
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/search")
    public List<CustomerDTO> searchCustomers (@RequestParam(name = "keyword",defaultValue = "") String keyword){
        return bankAccountService.searchCustomers("%"+keyword+"%"); 
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id")Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers/add")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){//@RequestBody : On va récupérer les données du customerDTO on va les recupérer à partir du corps de la requete en format JSON
        return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/update/{customerId}")//quand l'attribut porte le meme nom que celui dans l'url on est pas obligé de specifié "name"
    public CustomerDTO updateCustomer(@PathVariable Long customerId,@RequestBody CustomerDTO customerDTO){
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/delete/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        bankAccountService.deleteCustomer(id);
    }

}
