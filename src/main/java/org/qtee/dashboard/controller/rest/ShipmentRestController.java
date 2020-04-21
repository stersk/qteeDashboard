package org.qtee.dashboard.controller.rest;

import org.qtee.dashboard.data.projection.ShipmentDayStat;
import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.Shipment;
import org.qtee.dashboard.service.AccountService;
import org.qtee.dashboard.service.ShipmentService;
import org.qtee.dashboard.tao.ShipmentForBootstrapTableTAO;
import org.qtee.dashboard.tao.ShipmentFrom1CTAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services/shipment")
public class ShipmentRestController {
    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private AccountService accountService;

    @GetMapping(path="/ping")
    public ResponseEntity<String> ping(Principal principal) {
        String response = "That's all Ok:";
        if (principal == null) {
            response += "null";
        } else {
            response += principal.getName();
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(path="/get-all")
    public ResponseEntity<List<ShipmentForBootstrapTableTAO>> getAll(Principal principal, @RequestParam String from, @RequestParam String to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));

        LocalDateTime startDate = LocalDateTime.parse(from, formatter);
        LocalDateTime endDate = LocalDateTime.parse(to, formatter);

        List<Shipment> shipmentList = shipmentService.getAllInRange(startDate, endDate);
        List<ShipmentForBootstrapTableTAO> response = shipmentList.stream().map(ShipmentForBootstrapTableTAO::new).collect(Collectors.toList());

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(path="/get-day-stats")
    public ResponseEntity<List<ShipmentDayStat>> getDayStats(Principal principal, @RequestParam String from, @RequestParam String to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));

        LocalDateTime startDate = LocalDateTime.parse(from, formatter);
        LocalDateTime endDate = LocalDateTime.parse(to, formatter);

        List<ShipmentDayStat> response = shipmentService.getDayStats(startDate, endDate);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(path="/saveShipments")
    public ResponseEntity<String> saveShipments(@RequestBody List<ShipmentFrom1CTAO> data) {
        // TODO make appropriate account setting after REST authorization
        // temporary hardcode account
        Account account = accountService.getAccountById(1l);

        for (ShipmentFrom1CTAO shipmentTao: data) {
            Shipment shipment = shipmentTao.toShipment();
            shipment.setAccount(account);

            shipmentService.save(shipment);
        }

        return new ResponseEntity("{}", HttpStatus.OK);
    }
}
