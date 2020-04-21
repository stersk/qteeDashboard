package org.qtee.dashboard.controller.rest;

import org.qtee.dashboard.data.projection.ShipmentDayStat;
import org.qtee.dashboard.dto.ShipmentForBootstrapTableDTO;
import org.qtee.dashboard.dto.ShipmentFrom1CDTO;
import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.Shipment;
import org.qtee.dashboard.entity.User;
import org.qtee.dashboard.service.AccountService;
import org.qtee.dashboard.service.ShipmentService;
import org.qtee.dashboard.service.UserServiceWithDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
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
    private UserServiceWithDetails userService;

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
    public ResponseEntity<List<ShipmentForBootstrapTableDTO>> getAll(Principal principal, @RequestParam String from, @RequestParam String to) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        Account account = user.getAccount();
        if (account == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));

        LocalDateTime startDate = LocalDateTime.parse(from, formatter);
        LocalDateTime endDate = LocalDateTime.parse(to, formatter);

        List<Shipment> shipmentList = shipmentService.getAllInRange(startDate, endDate, account);
        List<ShipmentForBootstrapTableDTO> response = shipmentList.stream().map(ShipmentForBootstrapTableDTO::new).collect(Collectors.toList());

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping(path="/get-day-stats")
    public ResponseEntity<List<ShipmentDayStat>> getDayStats(Principal principal, @RequestParam String from, @RequestParam String to) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        Account account = user.getAccount();
        if (account == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.of("UTC"));

        LocalDateTime startDate = LocalDateTime.parse(from, formatter);
        LocalDateTime endDate = LocalDateTime.parse(to, formatter);

        List<ShipmentDayStat> response = shipmentService.getDayStats(startDate, endDate, account);

        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping(path="/save-all")
    public ResponseEntity<String> saveShipments(Principal principal, @RequestBody List<ShipmentFrom1CDTO> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) principal;
        User userWithoutAccount = (User) authenticationToken.getPrincipal();
        User user = userService.findById(userWithoutAccount.getId());

        Account account = user.getAccount();
        if (account == null) {
            return new ResponseEntity<>("No account found for this user", HttpStatus.BAD_REQUEST);
        }

        for (ShipmentFrom1CDTO shipmentDto: data) {
            Shipment shipment = shipmentDto.toShipment();
            shipment.setAccount(account);

            shipmentService.save(shipment);
        }

        return new ResponseEntity("{}", HttpStatus.OK);
    }
}
