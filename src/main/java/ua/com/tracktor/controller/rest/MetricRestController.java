package ua.com.tracktor.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ua.com.tracktor.dto.NotifyDTO;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Metric;
import ua.com.tracktor.entity.User;
import ua.com.tracktor.entity.enums.MetricType;
import ua.com.tracktor.service.MetricService;
import ua.com.tracktor.service.UserServiceWithDetails;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/services/metric")
public class MetricRestController {
    @Autowired
    private MetricService metricService;

    @Autowired
    private UserServiceWithDetails userService;

    @GetMapping(path="/update")
    public ResponseEntity<String> ping(Principal principal) {
        String response = "{}";

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        Account account = user.getAccount();
        if (account == null) {
            response = "No account found for this user";
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        metricService.recalculateMetrics(account);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(path="/get-metric/{type}")
    public ResponseEntity<Map<String, Object>> getMetric(Principal principal, @PathVariable(value = "type") String metricTypeName) {
        Map<String, Object> response = new HashMap<>();

        //TODO Refactor code for getting account from principal to separate method and remove similar duplicate code
        //  from rest controllers
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        Account account = user.getAccount();
        if (account == null) {
            response.put("error", "No account found for this user");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        MetricType metricType = MetricType.getByName(metricTypeName);
        if (metricType == null) {
            response.put("error", "Metric type unknown");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Metric metric = metricService.getMetric(account, metricType);
        NotifyDTO notifyData = metricService.getNotify(metric);

        response.put("date", (metric == null) ? LocalDateTime.ofInstant(Instant.ofEpochMilli(1l), ZoneId.of("UTC")): metric.getDate());
        response.put("value", (metric == null) ? 0: metric.getValue());
        response.put("showNotify", (notifyData != null));
        response.put("notifyData", (metric == null) ? false: notifyData);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path="/save-all")
    public ResponseEntity<String> saveMetrics(Principal principal, @RequestBody JsonNode data) {
        if (!data.getNodeType().equals(JsonNodeType.ARRAY)) {
            return new ResponseEntity<>("It should be array of metrics data in the body, but it isn't", HttpStatus.BAD_REQUEST);
        }

        //TODO Refactor code for getting account from principal to separate method and remove similar duplicate code
        //  from rest controllers
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        Account account = user.getAccount();
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        List<Metric> metricsToSave = new ArrayList<>();
        String errorString = "";
        for (JsonNode metricData : data) {
            JsonNode metricNode = metricData.get("metric");
            JsonNode valueNode  = metricData.get("value");
            if (metricNode == null) {
                errorString = "Missing required parameter 'metric'";
                break;
            } else if (valueNode == null) {
                errorString = "Missing required parameter 'value'";
                break;
            } else if (!valueNode.isNumber()) {
                errorString = "Parameter 'value' should be number, but it isn't";
            }

            MetricType metricType = MetricType.getByName(metricNode.asText());
            if (metricType == null) {
                errorString = "Unknown parameter value 'metric':'" + metricNode.asText() + "'";
                break;
            }

            double value = valueNode.asDouble();

            Metric metric = new Metric();
            metric.setAccount(account);
            metric.setMetricType(metricType);
            metric.setDate(LocalDateTime.now());
            metric.setValue(value);

            metricsToSave.add(metric);
        }

        if (errorString.isBlank()) {
            metricService.saveAll(metricsToSave);
        } else {
            return new ResponseEntity<>(errorString, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity("{}", HttpStatus.OK);
    }
}
