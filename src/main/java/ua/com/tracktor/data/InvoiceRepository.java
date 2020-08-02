package ua.com.tracktor.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Invoice;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    @Query(value =  "SELECT " +
                    "SUM(invoice.sum) " +
                    "FROM " +
                    "   Invoice invoice " +
                    "WHERE " +
                    "invoice.account = :account")
    Long getTotalSum(@Param("account") Account account);

    Optional<Invoice> findFirstByAccountAndSumNotNullOrderByDateDesc(Account account);

    Optional<Invoice> getOneByNumber(String number);
}
