package ua.com.tracktor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.env.Environment;
import ua.com.tracktor.entity.Invoice;
import ua.com.tracktor.util.HmacUtil;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WayForPayResponseDTO {
    private String orderReference;
    private static final String status = "accept";
    private Long time;
    private String signature;

    public static WayForPayResponseDTO createResponseDto(Invoice invoice, Environment env) {
        WayForPayResponseDTO responseDTO = new WayForPayResponseDTO();
        responseDTO.orderReference = invoice.getId().toString();
        responseDTO.time = System.currentTimeMillis();

        responseDTO.signResponse(env);

        return responseDTO;
    }

    public void signResponse(Environment env){
        //setting signature
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(orderReference).append(";");
        stringBuilder.append(status).append(";");
        stringBuilder.append(time.toString());

        signature = HmacUtil.hmacDigest(stringBuilder.toString(), env.getProperty("wayforpay.secret-key"), "HmacMD5");
    }
}
