package org.qtee.dashboard.controller.rest;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

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
    public ResponseEntity<String> saveShipments(Principal principal, @RequestBody List<Invoice> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        final Account account = user.getAccount();
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        data.stream().forEach(invoice -> {
            invoice.setAccount(account);
            invoiceService.save(invoice);
        });

        return new ResponseEntity("{}", HttpStatus.OK);
    }
}
