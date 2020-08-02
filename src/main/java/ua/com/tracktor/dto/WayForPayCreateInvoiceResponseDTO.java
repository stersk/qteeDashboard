package ua.com.tracktor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WayForPayCreateInvoiceResponseDTO {
    private String reason;
    private Integer reasonCode;
    private String invoiceUrl;
    private String qrCode;
}
