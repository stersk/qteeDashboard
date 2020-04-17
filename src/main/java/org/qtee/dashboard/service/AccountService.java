package org.qtee.dashboard.service;

import org.qtee.dashboard.data.AccountRepository;
import org.qtee.dashboard.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account getAccountById(Long id){
        return accountRepository.getOne(id);
    }
}
