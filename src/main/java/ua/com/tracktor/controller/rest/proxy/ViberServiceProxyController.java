package ua.com.tracktor.controller.rest.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.service.AccountService;
import ua.com.tracktor.service.ProxyFilterService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/services/notification")
public class ViberServiceProxyController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ProxyFilterService proxyFilterService;

    @Autowired
    private Environment env;

    private final Map<Account, LocalDateTime> lastAccountQueryTime = new HashMap<>();

    @PostMapping (path="/ping")
    public ResponseEntity<Map<String, String>> ping(Principal principal) {
        Account account = accountService.getAccountByPrincipal(principal);
        Map<String, String> response = new HashMap<>();
        if (account == null) {
            response.put("success", "false");
            response.put("error", "No account found for this user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            response.put("success", "true");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/**")
    public ResponseEntity<String> viberMirror(Principal principal, @RequestBody String body, @RequestHeader HttpHeaders headers, HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        Map<String, LocalDateTime> dates = new HashMap<>();
        dates.put("request", LocalDateTime.now());

        ResponseEntity<String> responseEntity = null;

        int secondsPerQuerylimit = Integer.parseInt(Objects.requireNonNull(env.getProperty("viber-service.server.status-query-limit-time-in-seconds")));
        String server = env.getProperty("viber-service.server.address");
        String basePath = env.getProperty("viber-service.server.path");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("viber-service.server.port")));
        String path = basePath + request.getRequestURI().substring(9);

        Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            responseEntity = new ResponseEntity<>("{\"error\":\"Account not authorized\"}", HttpStatus.UNAUTHORIZED);
        } else {

            // If it is status check query, use limit no more than 1 query per 10 seconds. Another queries we process without limit
            if (path.endsWith("get-status")) {
                LocalDateTime currentTime = LocalDateTime.now();
                LocalDateTime lastTime = lastAccountQueryTime.getOrDefault(account, LocalDateTime.now().plusSeconds(-3600));
                if (ChronoUnit.SECONDS.between(lastTime, currentTime) > secondsPerQuerylimit) {
                    lastAccountQueryTime.put(account, currentTime);
                } else {
                    responseEntity = new ResponseEntity<>("", HttpStatus.TOO_MANY_REQUESTS);
                }
            }

            // Process query
            if (responseEntity == null) {
                URI uri = new URI("https", null, server, port, path, request.getQueryString(), null);

                headers.add("x-user-id", account.getId().toString());
                headers.remove("authorization"); // Authorization on viberService is disabled by default

                HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                try {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters()
                            .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
                    responseEntity = restTemplate.exchange(uri, method, httpEntity, String.class);
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                    String responseBody = e.getResponseBodyAsString();
                    responseEntity = new ResponseEntity<>(responseBody, Objects.requireNonNull(HttpStatus.resolve(e.getRawStatusCode())));
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
            }
        }

        //Exclude "get-status" queries with empty responses from filtering
        boolean filterQuery = true;
        if (path.endsWith("get-status")) {
            if (responseEntity.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                filterQuery = false;
            } else {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode responseBodyNode = mapper.readTree(responseEntity.getBody());
                    JsonNode dataNode = responseBodyNode.get("data");
                    if (dataNode != null && dataNode.isArray() && dataNode.size() == 0) {
                        filterQuery = false;
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

            }
        }

        if (filterQuery) {
            dates.put("response", LocalDateTime.now());
            proxyFilterService.registerQuery(ViberServiceProxyController.class, account, request.getRequestURI(), body,
                    headers, responseEntity.getStatusCodeValue(), responseEntity.getBody(), responseEntity.getHeaders(),
                    dates, request.getRemoteAddr());
        }

        return responseEntity;
    }
}
