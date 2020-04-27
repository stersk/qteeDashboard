package org.qtee.dashboard.service;

import org.qtee.dashboard.data.InvoiceRepository;
import org.qtee.dashboard.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
