package ua.com.tracktor.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Invoice;

import java.time.LocalDateTime;
import java.util.List;
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

    @Query(value =  "SELECT " +
            "invoice " +
            "FROM " +
            "   Invoice invoice " +
            "WHERE " +
            "invoice.date < :date AND invoice.sum is null")
    List<Invoice> getNonusedInvoicesBefore(@Param("date") LocalDateTime date);

    Optional<Invoice> findFirstByAccountAndSumNotNullOrderByDateDesc(Account account);

    Optional<Invoice> getOneByNumber(String number);
}
