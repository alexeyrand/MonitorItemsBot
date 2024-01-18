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

}
