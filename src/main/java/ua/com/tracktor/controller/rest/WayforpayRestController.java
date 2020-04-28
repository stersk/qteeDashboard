package ua.com.tracktor.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.tracktor.entity.Invoice;
import ua.com.tracktor.service.AccountService;
import ua.com.tracktor.service.InvoiceService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@RestController
@RequestMapping("/wayforpay")
public class WayforpayRestController {
    @Autowired
    private Environment env;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private AccountService accountService;

    @PostMapping(path="/transaction")
    public ResponseEntity<String> transactionData(@RequestBody JsonNode data) {
        //TODO
        // 1. account hardcoded, need to refactor this
        // 2. add merchant signature check for incomming connections
        // 3. control refund (low priority task)

        JsonNode metricNode = data.get("merchantAccount");
        String merchantAccount = metricNode.asText("merchantAccount");

        if (metricNode != null && merchantAccount.equalsIgnoreCase(env.getProperty("wayforpay.account"))) {
            JsonNode transactionStatusNode = data.get("transactionStatus");
            String transactionStatus = transactionStatusNode.asText();

            if (transactionStatus != null && transactionStatus.equalsIgnoreCase("Approved")) {
                String number = data.get("orderReference").asText();

                JsonNode sumNode = data.get("amount");
                Double sum = (sumNode == null) ? 0l : sumNode.asDouble() * 100;

                JsonNode commissionRateNode = data.get("amount");
                Double commissionRate = (commissionRateNode == null) ? 0l : commissionRateNode.asDouble() * 100;

                JsonNode dateNode = data.get("createdDate");
                Long timestamp = (dateNode == null) ? 0l : dateNode.asLong();

//              Time already in UTC-timezone
//              LocalDateTime date =
//                        LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("UTC"));
                LocalDateTime date =
                       LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());



                ObjectMapper objectMapper = new ObjectMapper();
                String description  = "";
                try {
                    description  = objectMapper.writeValueAsString(data);
                } catch (JsonProcessingException e) {

                }

                Invoice invoice = invoiceService.getInvoiceByNumber(number);
                if (invoice == null) {
                    invoice = new Invoice();
                    invoice.setNumber(number);
                    invoice.setNotified(false);
                }

                invoice.setDate(date);
                invoice.setSum(sum.longValue());
                invoice.setCommissionRate(commissionRate.longValue());
                invoice.setDescription(description);
//                invoice.setAccount();
                invoiceService.save(invoice);
            }
        }

        return new ResponseEntity("", HttpStatus.OK);
    }
}