package ua.com.tracktor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ua.com.tracktor.data.AccountRepository;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.User;

import java.security.Principal;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserServiceWithDetails userService;

    public Account getAccountById(Long id){
        return accountRepository.getById(id);
    }

    @Nullable
    public Account getAccountByName(String name){
        return accountRepository.getOneByName(name).orElse(null);
    }

    public Account save(Account account){
        return accountRepository.save(account);
    }

    @Nullable
    public Account getAccountByPrincipal(Principal principal){
        Account account = null;

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        if (user != null) {
            account = user.getAccount();
        }

        return account;
    }
}
