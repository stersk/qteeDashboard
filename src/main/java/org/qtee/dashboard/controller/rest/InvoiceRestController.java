package org.qtee.dashboard.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.Invoice;
import org.qtee.dashboard.entity.User;
import org.qtee.dashboard.service.AccountService;
import org.qtee.dashboard.service.InvoiceService;
import org.qtee.dashboard.service.UserServiceWithDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
    private UserServiceWithDetails userService;

    @PostMapping(path="/save-all")
    public ResponseEntity<String> saveInvoices(Principal principal, @RequestBody List<Invoice> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        final Account account = user.getAccount();
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        data.stream().forEach(invoice -> {
            Invoice existingInvoice = invoiceService.getInvoice(invoice.getId());
            if (existingInvoice == null) {
                invoice.setNotified(false);
            } else {
                invoice.setNotified(existingInvoice.getNotified());
            }
            invoice.setAccount(account);

            invoiceService.save(invoice);
        });

        return new ResponseEntity("{}", HttpStatus.OK);
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteInvoice(Principal principal, @PathVariable(name = "id") UUID id) throws JsonProcessingException {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        final Account account = user.getAccount();
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        Invoice deletedInvoice = invoiceService.deleteInvoice(id);
        if (deletedInvoice == null) {
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            String response = mapper.writeValueAsString(deletedInvoice);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
