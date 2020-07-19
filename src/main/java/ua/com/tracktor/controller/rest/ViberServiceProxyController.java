package ua.com.tracktor.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.User;
import ua.com.tracktor.service.UserServiceWithDetails;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/services/notification/**")
public class ViberServiceProxyController {
    @Autowired
    private UserServiceWithDetails userService;

    @Autowired
    private Environment env;

    @RequestMapping(value = "/**")
    @ResponseBody
    public String viberMirror(Principal principal, @RequestBody String body, @RequestHeader Map<String, String> headers, HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        String server = env.getProperty("viber-service.server.address");
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("viber-service.server.port")));

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        Account account = user.getAccount();
        if (account == null) {
            return "{\"error\":\"Account not authorized\"}";
        }

        URI uri = new URI("https", null, server, port, request.getRequestURI(), request.getQueryString(), null);

        HttpEntity<String> httpEntity = new HttpEntity<>(body);
        httpEntity.getHeaders().add("1c-user", account.getId().toString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(uri, method, httpEntity, String.class);

        return responseEntity.getBody();
    }
}
