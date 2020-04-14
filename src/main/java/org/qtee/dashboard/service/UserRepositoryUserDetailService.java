package org.qtee.dashboard.service;

import org.qtee.dashboard.data.UserRepository;
import org.qtee.dashboard.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserRepositoryUserDetailService implements UserDetailsService {
    private UserRepository userRepo;

    @Autowired
    public UserRepositoryUserDetailService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(userName);
        System.out.println(userName);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException(
                "User '" + userName + "' not found");
    }
}
