package ua.com.tracktor.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.tracktor.entity.Invoice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice1CDTO {
    private UUID id;
    private LocalDateTime date;
    private String number;
    private Float sum;
    private Float commissionRate;
    private String description;

    public Invoice1CDTO(Invoice invoice){
        this.id = invoice.getId();
        this.date = invoice.getDate();
        this.number = invoice.getNumber();
        this.sum = invoice.getSum().floatValue()/100;
        this.commissionRate = invoice.getCommissionRate().floatValue()/100;
        this.description = invoice.getDescription();
    }

    public Invoice toInvoice() {
        Float tempSum = sum * 100;
        Long sumLong = tempSum.longValue();

        tempSum = commissionRate * 100;
        Long commissionRateLong = tempSum.longValue();

        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setDate(date);
        invoice.setNumber(number);
        invoice.setSum(sumLong);
        invoice.setCommissionRate(commissionRateLong);
        invoice.setDescription(description);

        return invoice;
    }

    @JsonGetter("date")
    public String getDateForJson(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of("UTC"));

        return date.format(formatter);
    }
}
