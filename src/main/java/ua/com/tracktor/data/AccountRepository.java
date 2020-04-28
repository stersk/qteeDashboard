package ua.com.tracktor.data;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.com.tracktor.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
