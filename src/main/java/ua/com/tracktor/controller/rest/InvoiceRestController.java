package ua.com.tracktor.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.tracktor.dto.Invoice1CDTO;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Invoice;
import ua.com.tracktor.service.AccountService;
import ua.com.tracktor.service.InvoiceService;
import ua.com.tracktor.service.MetricService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services/invoice")
public class InvoiceRestController {
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MetricService metricService;

    @PostMapping(path="/save-all")
    public ResponseEntity<String> saveInvoices(Principal principal, @RequestBody List<Invoice1CDTO> data) {
        final Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        data.stream().map(Invoice1CDTO::toInvoice).forEach(invoice -> {
            Invoice existingInvoice = invoiceService.getInvoice(invoice.getId());
            if (existingInvoice == null) {
                invoice.setNotified(false);
            } else {
                invoice.setNotified(existingInvoice.getNotified());
            }
            invoice.setAccount(account);

            invoiceService.save(invoice);
        });

        metricService.recalculateMetrics(account);

        return new ResponseEntity("{}", HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteInvoice(Principal principal, @PathVariable(name = "id") UUID id) throws JsonProcessingException {
        final Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        Invoice deletedInvoice = invoiceService.deleteInvoice(id);
        if (deletedInvoice == null) {
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        } else {
            metricService.recalculateMetrics(account);

            ObjectMapper mapper = new ObjectMapper();
            String response = mapper.writeValueAsString(new Invoice1CDTO(deletedInvoice));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
