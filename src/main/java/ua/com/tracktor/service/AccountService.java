package ua.com.tracktor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ua.com.tracktor.data.AccountRepository;
import ua.com.tracktor.entity.Account;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account getAccountById(Long id){
        return accountRepository.getOne(id);
    }

    @Nullable
    public Account getAccountByName(String name){
        return accountRepository.getOneByName(name).orElse(null);
    }

    public Account save(Account account){
        return accountRepository.save(account);
    }
}
