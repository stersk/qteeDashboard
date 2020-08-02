package ua.com.tracktor.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.service.AccountService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/services/notification/**")
public class ViberServiceProxyController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private Environment env;

    private final Map<Account, LocalDateTime> lastAccountQueryTime = new HashMap<>();

    @RequestMapping(value = "/**")
    public ResponseEntity<String> viberMirror(Principal principal, @RequestBody String body, @RequestHeader MultiValueMap<String, String> headers, HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        int secondsPerQuerylimit = Integer.parseInt(Objects.requireNonNull(env.getProperty("viber-service.server.status-query-limit-time-in-seconds")));
        String server = env.getProperty("viber-service.server.address");
        String basePath = env.getProperty("viber-service.server.path");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("viber-service.server.port")));

        Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            return new ResponseEntity<>("{\"error\":\"Account not authorized\"}", HttpStatus.UNAUTHORIZED);
        }

        String path = basePath + request.getRequestURI().substring(9);

        // If it is status check query, use limit no more than 1 query per 10 seconds. Another queries we process without limit
        if (path.endsWith("get-status")) {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime lastTime = lastAccountQueryTime.getOrDefault(account, LocalDateTime.now().plusSeconds(-3600));
            if (ChronoUnit.SECONDS.between(lastTime, currentTime) > secondsPerQuerylimit) {
                lastAccountQueryTime.put(account, currentTime);
            } else {
                return new ResponseEntity<>("", HttpStatus.TOO_MANY_REQUESTS);
            }
        }

        URI uri = new URI("https", null, server, port, path, request.getQueryString(), null);

        headers.add("x-user-id", account.getId().toString());
        headers.remove("authorization"); // Authorization on viberService is disabled by default

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity;

        try {
            RestTemplate restTemplate = new RestTemplate();
            responseEntity = restTemplate.exchange(uri, method, httpEntity, String.class);

        } catch (Exception e) {
            Map<String, String> data = new HashMap<>();
            data.put("error", "Query send error");
            data.put("reason", e.getLocalizedMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String stringData = "";
            try {
                stringData = objectMapper.writeValueAsString(data);
            } catch (JsonProcessingException jsonProcessingException) {
                stringData = e.getLocalizedMessage();
            }

            responseEntity = new ResponseEntity<>(stringData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}
