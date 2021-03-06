package ua.com.tracktor.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.tracktor.entity.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> getOneByName(String name);
}
