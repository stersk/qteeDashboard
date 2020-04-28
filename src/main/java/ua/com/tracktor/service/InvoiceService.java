package ua.com.tracktor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.tracktor.data.InvoiceRepository;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Invoice;

import java.util.UUID;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public void save(Invoice invoice) {
        invoiceRepository.saveAndFlush(invoice);
    }

    public Invoice getInvoice(UUID id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    public Invoice deleteInvoice(UUID id) {
        Invoice deletedInvoice = invoiceRepository.findById(id).orElse(null);
        if (deletedInvoice != null) {
            invoiceRepository.delete(deletedInvoice);
        }

        return deletedInvoice;
    }

    public Long getTotalSum(Account account){
        return invoiceRepository.getTotalSum(account);
    }

    public Invoice getLastInvoice(Account account) {
        return invoiceRepository.findFirstByAccountOrderByDateDesc(account).orElse(null);
    }
}
