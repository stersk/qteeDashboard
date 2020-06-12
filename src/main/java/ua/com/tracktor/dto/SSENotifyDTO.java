package ua.com.tracktor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SSENotifyDTO {
    private String message;
    private LocalDateTime time;
    private static Long notifyId = 0L;
    private Long accountId;
    private String notificationType;

    public SSENotifyDTO(String message, Long accountId, String notificationType) {
        this.message = message;
        this.accountId = accountId;
        this.notificationType = notificationType;
        this.time = LocalDateTime.now();

        ++notifyId;
    }
}
