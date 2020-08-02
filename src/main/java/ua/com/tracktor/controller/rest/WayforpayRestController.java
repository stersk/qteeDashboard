package ua.com.tracktor.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.dto.WayForPayCreateInvoiceQueryDTO;
import ua.com.tracktor.dto.WayForPayCreateInvoiceResponseDTO;
import ua.com.tracktor.dto.WayForPayResponseDTO;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Invoice;
import ua.com.tracktor.entity.User;
import ua.com.tracktor.service.AccountService;
import ua.com.tracktor.service.InvoiceService;
import ua.com.tracktor.service.MetricService;
import ua.com.tracktor.service.UserServiceWithDetails;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

@RestController
public class WayforpayRestController {
    @Autowired
    private Environment env;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MetricService metricService;

    @Autowired
    private UserServiceWithDetails userService;

    @PostMapping(path="/wayforpay/transaction", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<WayForPayResponseDTO> transactionStatus(@RequestBody String stringData) {
        WayForPayResponseDTO responseBody = new WayForPayResponseDTO();

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
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        JsonNode metricNode = data.get("merchantAccount");
        String merchantAccount = metricNode.asText("merchantAccount");

        if (metricNode != null && merchantAccount.equalsIgnoreCase(env.getProperty("wayforpay.account"))) {
            JsonNode transactionStatusNode = data.get("transactionStatus");
            String transactionStatus = transactionStatusNode.asText();
            UUID number = UUID.fromString(data.get("orderReference").asText());

            if (transactionStatus != null && transactionStatus.equalsIgnoreCase("Approved")) {
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

                Invoice invoice = invoiceService.getInvoice(number);
                if (invoice == null) {
                    invoice = new Invoice();
                    invoice.setNumber(number.toString());
                    invoice.setAccount(accountService.getAccountById(Long.decode(env.getProperty("wayforpay.account-id-for-unknown-invoices"))));
                }

                invoice.setDate(date);
                invoice.setSum(sum.longValue());
                invoice.setCommissionRate(commissionRate.longValue());
                invoice.setDescription(stringData);

                invoiceService.save(invoice);

                metricService.recalculateMetrics(invoice.getAccount());

                // Make a valid response with signature
                return new ResponseEntity<>(WayForPayResponseDTO.createResponseDto(invoice, env), HttpStatus.OK);
            } else
                // Make a valid response with signature
                responseBody.setOrderReference(number.toString());
                responseBody.setTime(System.currentTimeMillis());
                responseBody.signResponse(env);
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }

        return new ResponseEntity<>(responseBody, HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping(path="/services/get-new-invoice-link")
    public ResponseEntity<String> createInvoice(Principal principal) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        final Account account = user.getAccount();
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        Invoice newInvoice = new Invoice();
        newInvoice.setAccount(account);
        newInvoice.setNotified(false);
        newInvoice.setDate(LocalDateTime.now());
        newInvoice = invoiceService.save(newInvoice);

        WayForPayCreateInvoiceQueryDTO query = WayForPayCreateInvoiceQueryDTO.createQuery(newInvoice, env);

        HttpEntity<WayForPayCreateInvoiceQueryDTO> httpEntity = new HttpEntity<>(query);
        ResponseEntity<String> responseEntity;
        ResponseEntity<String> resultResponseEntity = null;

        try {
            //for debug
            ObjectMapper objectMapper = new ObjectMapper();
            String testData = objectMapper.writeValueAsString(query);
            //for debug

            RestTemplate restTemplate = new RestTemplate();
            responseEntity = restTemplate.exchange("https://api.wayforpay.com/api", HttpMethod.POST, httpEntity, String.class);

            //Jackson used for body deserialization because we cannot get response object as content type [text/html;charset=UTF-8] returned by WayForPay REST service
            ObjectMapper mapper = new ObjectMapper();
            WayForPayCreateInvoiceResponseDTO queryResult = mapper.readValue(responseEntity.getBody(), WayForPayCreateInvoiceResponseDTO.class);

            if (queryResult.getReasonCode() == 1100) {
                resultResponseEntity = new ResponseEntity<>(queryResult.getInvoiceUrl(), HttpStatus.OK);
            } else {
                resultResponseEntity = new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            invoiceService.deleteInvoice(newInvoice.getId());
            resultResponseEntity = new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return resultResponseEntity;
    }
}