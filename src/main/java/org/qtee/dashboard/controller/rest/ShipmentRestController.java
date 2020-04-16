package org.qtee.dashboard.controller.rest;

import org.qtee.dashboard.entity.Shipment;
import org.qtee.dashboard.service.ShipmentService;
import org.qtee.dashboard.tao.ShipmentForBootstrapTableTAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services/shipment")
public class ShipmentRestController {
    @Autowired
    private ShipmentService shipmentService;

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
    public ResponseEntity<List<ShipmentForBootstrapTableTAO>> getAll(Principal principal) {
        List<Shipment> shipmentList = shipmentService.getAll();
        List<ShipmentForBootstrapTableTAO> response = shipmentList.stream().map(ShipmentForBootstrapTableTAO::new).collect(Collectors.toList());

        return new ResponseEntity(response, HttpStatus.OK);
    }
}
