package org.qtee.dashboard.data;

import org.qtee.dashboard.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
