package org.qtee.dashboard.service;

import org.qtee.dashboard.data.InvoiceRepository;
import org.qtee.dashboard.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

    public void save(Invoice invoice) {
        invoiceRepository.saveAndFlush(invoice);
    }
}
