package ua.com.tracktor.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ua.com.tracktor.dto.SSENotifyDTO;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.service.AccountService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/services")
public class NotificationRestController {
    @Autowired
    private AccountService accountService;

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> accountEmittersMap = new ConcurrentHashMap<>();

    //TODO: change /notificationSource to notification-source
    @GetMapping(path = "/notificationSource")
    public SseEmitter getNewNotification(Principal principal) {
        Account account = accountService.getAccountByPrincipal(principal);
        if (account == null) {
            return null;
        }

        List<SseEmitter> emittersList = accountEmittersMap.computeIfAbsent(account.getId(), k -> new CopyOnWriteArrayList<>());

        SseEmitter emitter = new SseEmitter();
        emittersList.add(emitter);

        emitter.onCompletion(() ->
                emittersList.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            emittersList.remove(emitter);
        });

        return emitter;
    }

    @EventListener
    public void onNotification(SSENotifyDTO notification) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        List<SseEmitter> allEmitters = accountEmittersMap.getOrDefault(notification.getAccountId(), new CopyOnWriteArrayList<>());
        allEmitters.forEach(emitter -> {
            try {
                emitter.send(notification);
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        allEmitters.remove(deadEmitters);
    }
}
