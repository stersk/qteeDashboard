package ua.com.tracktor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.com.tracktor.controller.rest.proxy.DeliveryServiceProxyController;
import ua.com.tracktor.controller.rest.proxy.ViberServiceProxyController;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.QueryRecord;
import ua.com.tracktor.entity.Shipment;
import ua.com.tracktor.entity.enums.DeliveryService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

//TODO: Refactor: write Jackson util class for getting nodes and nodes value by path (using recursive)

@Service
public class ProxyFilterService {
    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private MetricService metricService;

    @Autowired
    private QueryRecordService queryRecordService;

    @Async
    public void registerQuery(Class controller, Account account, String uri, String requestBody, HttpHeaders requestHeaders,
                              int responseStatusCode, String responseBody, HttpHeaders responseHeaders,
                              Map<String, LocalDateTime> dates, String remoteAddr) {

        ObjectMapper objectMapper = new ObjectMapper();
        String requestHeadersString = "{\"error\": \"parsing error\"}";
        String responseHeadersString = "{\"error\": \"parsing error\"}";
        try {
            requestHeadersString = objectMapper.writeValueAsString(requestHeaders);
            responseHeadersString = objectMapper.writeValueAsString(responseHeaders);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        QueryRecord queryRecord = new QueryRecord(null, account, uri, false, dates.get("request"), dates.get("response"),
                requestBody, requestHeadersString, responseStatusCode, responseBody, responseHeadersString, remoteAddr);
        Long newId = queryRecordService.save(queryRecord);

        boolean success = filterRequest(controller, account, requestBody, responseBody, responseStatusCode);

        queryRecordService.updateFilteredFlag(newId, success);
    }

    private Boolean filterRequest(Class controller, Account account, String requestBody, String responseBody, Integer responseStatusCode) {
        boolean success = true;

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode requestBodyNode = mapper.readTree(requestBody);
            JsonNode responseBodyNode = mapper.readTree(responseBody);

            if (DeliveryServiceProxyController.class.equals(controller)) {
                success = filterDeliveryRequest(account, requestBodyNode, responseBodyNode, responseStatusCode);
            } else if (ViberServiceProxyController.class.equals(controller)) {
                success = filterViberRequest(requestBodyNode, responseBodyNode);
            } else {
                success = false;
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    private Boolean filterViberRequest(JsonNode requestBodyNode, JsonNode responseBodyNode) {
        Boolean success = true;

        return true;
    }

    private boolean filterDeliveryRequest(Account account, JsonNode requestBodyNode, JsonNode responseBodyNode, Integer responseStatusCode) {
        boolean success = true;
        String requestType = "";

        JsonNode requestTypeNode = requestBodyNode.get("ИмяФункции");
        if (requestTypeNode != null && requestTypeNode.isTextual()) {
            requestType = requestTypeNode.textValue();
        }

        switch (requestType) {
            case "ОбновитьДокумент":
                success = processShipmentCreateUpdateRequest(account, requestBodyNode, responseBodyNode, responseStatusCode, false);
                if (!success) {
                    processShipmentCreateRequestFallbackMode(account, requestBodyNode, responseStatusCode, false);
                }
                metricService.recalculateMetrics(account);
                break;

            case "СохранитьДокумент" :
                success = processShipmentCreateUpdateRequest(account, requestBodyNode, responseBodyNode, responseStatusCode, true);
                if (!success) {
                    processShipmentCreateRequestFallbackMode(account, requestBodyNode, responseStatusCode, true);
                }
                metricService.recalculateMetrics(account);
                break;

            case "":
                success = false;
                break;
        }

        return success;
    }

    private boolean processShipmentCreateUpdateRequest(Account account, JsonNode requestBodyNode, JsonNode responseBodyNode,
                                                       Integer responseStatusCode, boolean thisIsCreateQuery) {

        Shipment shipment = null;

        JsonNode parametersNode = requestBodyNode.get("ПараметрыФункции");
        if (parametersNode == null) {
            return false;
        }

        JsonNode documentNode = parametersNode.get("Документ");
        if (documentNode == null) {
            return false;
        }

        JsonNode deliveryDataNode = documentNode.get("ДанныеДоставки");
        if (deliveryDataNode == null) {
            return false;
        }

        JsonNode documentIdNode = documentNode.get("УникальныйИдентификатор");
        UUID shipmentId;
        if (documentIdNode != null && documentIdNode.isTextual()) {
            shipmentId = UUID.fromString(documentIdNode.textValue());
        } else {
            return false;
        }

        JsonNode mainDocumentIdNode = documentNode.get("ВедущаяТранспортнаяНакладная");
        UUID mainShipmentId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        Long price = 0l;

        if (mainDocumentIdNode != null && mainDocumentIdNode.get("УникальныйИдентификатор") != null && mainDocumentIdNode.get("УникальныйИдентификатор").isTextual()) {
            mainShipmentId = UUID.fromString(mainDocumentIdNode.get("УникальныйИдентификатор").textValue());
        }

        if (mainShipmentId.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            price = account.getPrice();
        }

        if (!thisIsCreateQuery) {
            shipment = shipmentService.getShipmentById(shipmentId);
        }

        if (shipment == null) {
            shipment = new Shipment();
            shipment.setId(shipmentId);
            shipment.setAccount(account);
          }


        if (responseStatusCode == 200) {
            shipment.setDate(LocalDateTime.now());
            shipment.setDeclarationPrice(price);
            shipment.setMainShipmentId(mainShipmentId);
        }

        JsonNode documentSumNode = documentNode.get("ОбъявленнаяСтоимость");
        if (documentSumNode != null && documentSumNode.isNumber()) {
            shipment.setSum(documentSumNode.longValue() * 100L);
        } else {
            return false;
        }

        JsonNode documentPartnerNode = deliveryDataNode.get("Получатель");
        if (documentPartnerNode != null && documentPartnerNode.has("Наименование")) {
            JsonNode partnerNameNode = documentPartnerNode.get("Наименование");
            if (partnerNameNode.isTextual()) {
                shipment.setCustomer(partnerNameNode.textValue());
            } else {
                return false;
            }
        } else {
            return false;
        }

        JsonNode documentPartnerPhoneNode = deliveryDataNode.get("Телефон");
        if (documentPartnerPhoneNode != null && documentPartnerPhoneNode.isTextual()) {
            shipment.setPhone(documentPartnerPhoneNode.textValue());
        } else {
            return false;
        }

        JsonNode documentPartnerAddressNode = deliveryDataNode.get("АдресПредставление");
        if (documentPartnerAddressNode != null && documentPartnerAddressNode.isTextual()) {
            shipment.setAddress(documentPartnerAddressNode.textValue());
        } else {
            return false;
        }

        JsonNode responseDataNode = responseBodyNode.get("Данные");
        if (responseDataNode == null) {
            return false;
        }

        JsonNode documentDeclarationNode = responseDataNode.get("НомерДекларации");
        if (documentDeclarationNode != null && documentDeclarationNode.isTextual()) {
            shipment.setDeclarationNumber(documentDeclarationNode.textValue());
        } else {
            return false;
        }

        JsonNode documentDeliveryServiceNode = documentNode.get("СлужбаДоставки");
        if (documentDeliveryServiceNode != null && documentDeliveryServiceNode.has("Код")) {
            JsonNode deliveryServiceCodeNode = documentDeliveryServiceNode.get("Код");
            if (deliveryServiceCodeNode.isTextual()) {
                switch (deliveryServiceCodeNode.textValue()) {
                    case "НоваяПочта":
                        shipment.setDeliveryService(DeliveryService.NOWA_POSHTA);
                        break;
                    case "Укрпочта" :
                        shipment.setDeliveryService(DeliveryService.UKRPOSHTA);
                        break;
                    default:
                        return false;
                }

            } else {
                return false;
            }

        } else {
            return false;
        }

        try {
            shipmentService.save(shipment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void processShipmentCreateRequestFallbackMode(Account account, JsonNode requestBodyNode,
                                                          Integer responseStatusCode, boolean thisIsCreateQuery) {
        Boolean success = true;

        Shipment shipment = null;

        JsonNode parametersNode = requestBodyNode.get("ПараметрыФункции");
        if (parametersNode == null) {
            throw new RuntimeException("Proxy query body data wrong: ПараметрыФункции");
        }

        JsonNode documentNode = parametersNode.get("Документ");
        if (documentNode == null) {
            throw new RuntimeException("Proxy query body data wrong: ПараметрыФункции.Документ");
        }

        JsonNode documentIdNode = documentNode.get("УникальныйИдентификатор");
        UUID shipmentId;
        if (documentIdNode != null && documentIdNode.isTextual()) {
            shipmentId = UUID.fromString(documentIdNode.textValue());
        } else {
            throw new RuntimeException("Proxy query body data wrong: ПараметрыФункции.Документ.УникальныйИдентификатор");
        }

        if (!thisIsCreateQuery) {
            shipment = shipmentService.getShipmentById(shipmentId);
        }

        if (shipment == null) {
            shipment = new Shipment();
            shipment.setId(shipmentId);
            shipment.setAccount(account);
            shipment.setDeclarationPrice(account.getPrice());
        }

        if (responseStatusCode == 200) {
            shipment.setDate(LocalDateTime.now());
        }

        try {
            shipmentService.save(shipment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
