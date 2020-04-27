package org.qtee.dashboard.data;

import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    @Query(value =  "SELECT " +
                    "SUM(invoice.sum) " +
                    "FROM " +
                    "   Invoice invoice " +
                    "WHERE " +
                    "invoice.account = :account")
    Long getTotalSum(@Param("account") Account account);
}
