package ua.com.tracktor.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.tracktor.data.projection.ShipmentDayStat;
import ua.com.tracktor.dto.Shipment1CDTO;
import ua.com.tracktor.dto.ShipmentBootstrapTableDTO;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Shipment;
import ua.com.tracktor.service.AccountService;
import ua.com.tracktor.service.MetricService;
import ua.com.tracktor.service.ShipmentService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services/shipment")
public class ShipmentRestController {
    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MetricService metricService;

    @GetMapping(path="/ping")
    public ResponseEntity<String> ping(Principal principal) {
        String response = "{}";
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(path="/get-all")
    public ResponseEntity<List<ShipmentBootstrapTableDTO>> getAll(Principal principal, @RequestParam String from, @RequestParam String to) {
        Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));

        LocalDateTime startDate = LocalDateTime.parse(from, formatter);
        LocalDateTime endDate = LocalDateTime.parse(to, formatter).toLocalDate().atTime(LocalTime.MAX);

        List<Shipment> shipmentList = shipmentService.getAllInRange(startDate, endDate, account);
        List<ShipmentBootstrapTableDTO> response = shipmentList.stream().map(ShipmentBootstrapTableDTO::new).collect(Collectors.toList());

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(path="/get-day-stats")
    public ResponseEntity<List<ShipmentDayStat>> getDayStats(Principal principal, @RequestParam String from, @RequestParam String to) {
        Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));

        LocalDateTime startDate = LocalDateTime.parse(from, formatter).toLocalDate().atStartOfDay();
        LocalDateTime endDate = LocalDateTime.parse(to, formatter).toLocalDate().atTime(LocalTime.MAX);

        List<ShipmentDayStat> response = shipmentService.getDayStats(startDate, endDate, account);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(path="/save-all")
    public ResponseEntity<String> saveShipments(Principal principal, @RequestBody List<Shipment1CDTO> data) {
        Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        for (Shipment1CDTO shipmentDto: data) {
            Shipment shipment = shipmentDto.toShipment();
            shipment.setAccount(account);

            shipmentService.save(shipment);
        }

        if (!data.isEmpty()) {
            metricService.recalculateMetrics(account);
        }

        return new ResponseEntity("{}", HttpStatus.OK);
    }
}
