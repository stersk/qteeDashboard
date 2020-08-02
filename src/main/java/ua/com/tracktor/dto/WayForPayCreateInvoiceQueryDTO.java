package ua.com.tracktor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.env.Environment;
import ua.com.tracktor.entity.Invoice;
import ua.com.tracktor.util.Hmac;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class WayForPayCreateInvoiceQueryDTO {
    private static final String transactionType = "CREATE_INVOICE";
    private String merchantAccount;
    private static final String merchantAuthType = "SimpleSignature";
    private String merchantDomainName;
    private String merchantSignature;
    private static final int apiVersion = 1;
    private static final String language = "ua";
    private String serviceUrl;
    private String orderReference;
    private long orderDate;
    private static final int amount = 0;
    private static final String currency = "UAH";
    private static final int orderTimeout = 86400;
    private List<String> productName;
    private static final List<Integer> productPrice = getNullPriceForOneProduct();
    private static final List<Integer> productCount = getNullProductsCountForOneProduct();
    private String paymentSystems;

    public String getTransactionType() {
        return transactionType;
    }

    public String getMerchantAuthType() {
        return merchantAuthType;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public String getLanguage() {
        return language;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getOrderTimeout() {
        return orderTimeout;
    }

    public List<Integer> getProductPrice() {
        return productPrice;
    }

    public List<Integer> getProductCount() {
        return productCount;
    }

    public static WayForPayCreateInvoiceQueryDTO createQuery(Invoice invoice, Environment env) {
        ZoneOffset zoneOffSet= ZoneOffset.of("+03:00");

        List<String> productsNames = new ArrayList<>();
        productsNames.add(env.getProperty("wayforpay.shipment-item-name"));

        WayForPayCreateInvoiceQueryDTO query = new WayForPayCreateInvoiceQueryDTO();
        query.setMerchantAccount(env.getProperty("wayforpay.account"));
        query.setMerchantDomainName(env.getProperty("wayforpay.merchant-domain-name"));
        query.setServiceUrl(env.getProperty("wayforpay.service-url"));
        query.setOrderReference(invoice.getId().toString());
        query.setOrderDate(invoice.getDate().toEpochSecond(zoneOffSet));
        query.setProductName(productsNames);
        query.setPaymentSystems(env.getProperty("wayforpay.payment-systems"));

        query.signQuery(env.getProperty("wayforpay.secret-key"));

        return query;
    }

    private void signQuery(String key){
        //setting signature
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(merchantAccount).append(";");
        stringBuilder.append(merchantDomainName).append(";");
        stringBuilder.append(orderReference).append(";");
        stringBuilder.append(orderDate).append(";");
        stringBuilder.append(amount).append(";");
        stringBuilder.append(currency).append(";");

        productName.forEach(item -> stringBuilder.append(item).append(";"));
        productCount.forEach(item -> stringBuilder.append(item).append(";"));
        productPrice.forEach(item -> stringBuilder.append(item).append(";"));

        stringBuilder.setLength(stringBuilder.length() - 1); //trim last character
        merchantSignature = Hmac.hmacDigest(stringBuilder.toString(), key, "HmacMD5");
    }

    private static List<Integer> getNullPriceForOneProduct() {
        List<Integer> prices = new ArrayList<>();
        prices.add(0);

        return prices;
    }

    private static List<Integer> getNullProductsCountForOneProduct() {
        List<Integer> counts = new ArrayList<>();
        counts.add(1);

        return counts;
    }
}
