package ua.com.tracktor.controller.rest.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.service.AccountService;
import ua.com.tracktor.util.RestUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/services/delivery")
public class DeliveryServiceProxyController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private Environment env;

    private final Map<Account, LocalDateTime> lastAccountQueryTime = new HashMap<>();

    @RequestMapping(value = "/**")
    public ResponseEntity<String> viberMirror(Principal principal, @RequestBody String body, @RequestHeader MultiValueMap<String, String> headers, HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        String server = env.getProperty("delivery-service.server.address");
        String basePath = env.getProperty("delivery-service.server.path");
        String userName = env.getProperty("delivery-service.server.user");
        String password = env.getProperty("delivery-service.server.password");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("delivery-service.server.port")));

        Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            return new ResponseEntity<>("{\"error\":\"Account not authorized\"}", HttpStatus.UNAUTHORIZED);
        }

        URI uri = new URI("https", null, server, port, basePath, request.getQueryString(), null);

        headers.add("x-user-id", account.getId().toString());
        headers.remove("authorization"); // Authorization on viberService is disabled by default
        RestUtil.addBasicAuthorizationHeader(headers, userName, password);

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity;

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
            responseEntity = restTemplate.exchange(uri, method, httpEntity, String.class);
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            responseEntity = new ResponseEntity<>(responseBody, Objects.requireNonNull(HttpStatus.resolve(e.getRawStatusCode())));
        } catch (HttpServerErrorException e) {
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

        return responseEntity;
    }
}
