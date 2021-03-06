package ua.com.tracktor.controller.rest.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.service.AccountService;
import ua.com.tracktor.service.ProxyFilterService;
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
    private ProxyFilterService proxyFilterService;

    @Autowired
    private Environment env;

    @RequestMapping(value = "/**")
    public ResponseEntity<String> deliveryMirror(Principal principal, @RequestBody String body, @RequestHeader HttpHeaders headers, HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        Map<String, LocalDateTime> dates = new HashMap<>();
        dates.put("request", LocalDateTime.now());

        String server = env.getProperty("delivery-service.server.address");
        String basePath = env.getProperty("delivery-service.server.path") + "/event";
        String userName = env.getProperty("delivery-service.server.user");
        String password = env.getProperty("delivery-service.server.password");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("delivery-service.server.port")));
        ResponseEntity<String> responseEntity = null;

        Account account = accountService.getAccountByPrincipal(principal);

        URI uri = new URI("https", null, server, port, basePath, request.getQueryString(), null);

        if (account == null) {
            responseEntity = new ResponseEntity<>("{\"error\":\"Account not authorized\"}", HttpStatus.UNAUTHORIZED);
        } else {
            headers.add("x-user-id", account.getId().toString());
            headers.remove("authorization");
            RestUtil.addBasicAuthorizationHeader(headers, userName, password);

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

        dates.put("response", LocalDateTime.now());
        proxyFilterService.registerQuery(DeliveryServiceProxyController.class, account, request.getRequestURI(), body,
                headers, responseEntity.getStatusCodeValue(), responseEntity.getBody(), responseEntity.getHeaders(),
                dates, request.getRemoteAddr());

        return responseEntity;
    }
}
