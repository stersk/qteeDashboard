package ua.com.tracktor.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ua.com.tracktor.dto.SSENotifyDTO;

@Service
public class SSENotificationService {
    public final ApplicationEventPublisher eventPublisher;

    public SSENotificationService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void sendUpdateNotification(Long accountId){
        SSENotifyDTO updateNotification = new SSENotifyDTO("", accountId, "dataUpdated");
        this.eventPublisher.publishEvent(updateNotification);
    }
}
