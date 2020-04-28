package ua.com.tracktor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.com.tracktor.data.UserRepository;
import ua.com.tracktor.entity.User;

@Service
public class UserServiceWithDetails implements UserDetailsService {
    private final UserRepository userRepo;

    @Autowired
    public UserServiceWithDetails(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(userName);

        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException(
                "User '" + userName + "' not found");
    }

    public User findById(Long id){
        return userRepo.findById(id).orElse(null);
    }
}
