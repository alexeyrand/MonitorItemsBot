package com.alexeyrand.monitoritemsbot.api.controller;

import com.alexeyrand.monitoritemsbot.telegram.TelegramBot;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MonitorItemsController {

    TelegramBot telegramBot;

    @PostMapping("/item")
    public void getItem(@RequestBody String url) {
        String[] split = url.split(" ");
        telegramBot.sendMessage(split[1], split[0]);
    }

    @PostMapping("/status")
    public void getStatus(@RequestBody String message) {
        System.out.println(message);
        String[] split = message.split(" ");
        Integer messageId = Integer.parseInt(split[1]);
        telegramBot.deleteMessage(split[0], messageId);
    }

}
