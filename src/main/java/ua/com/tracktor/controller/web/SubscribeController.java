package ua.com.tracktor.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/subscribe")
public class SubscribeController {
    @GetMapping
    public String subscribePage(@RequestParam String bot) {
        if (bot.equalsIgnoreCase("egastronom")){
            return "subscribe";
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
    }
}
