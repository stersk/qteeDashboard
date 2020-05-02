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
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Invoice;
import ua.com.tracktor.service.AccountService;
import ua.com.tracktor.service.InvoiceService;
import ua.com.tracktor.service.MetricService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    @Autowired
    private MetricService metricService;

    @PostMapping(path="/transaction", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<String> transactionData(@RequestBody String stringData) {
        // (if , try to get body as String to solve this trouble,
        // as servise seng JSON, but provide application/x-www-form-urlencoded header)
        //TODO
        // 1. account hardcoded, need to refactor this
        // 2. add merchant signature check for incomming connections
        // 3. control refund (low priority task)

        // Because Content type 'application/x-www-form-urlencoded;charset=UTF-8' and service send json in body, we will
        //  get body as string and map it to object
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode data = null;
        try {
            stringData = URLDecoder.decode(stringData, StandardCharsets.UTF_8.toString());

            data = objectMapper.readTree(stringData);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }

        Account account = accountService.getAccountById(Long.decode(env.getProperty("wayforpay.account-id")));

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

                Invoice invoice = invoiceService.getInvoiceByNumber(number);
                if (invoice == null) {
                    invoice = new Invoice();
                    invoice.setNumber(number);
                    invoice.setNotified(false);
                }

                invoice.setDate(date);
                invoice.setSum(sum.longValue());
                invoice.setCommissionRate(commissionRate.longValue());
                invoice.setDescription(stringData);
                invoice.setAccount(account);
                invoiceService.save(invoice);

                metricService.recalculateMetrics(account);
            }
        }

        return new ResponseEntity("", HttpStatus.OK);
    }
}