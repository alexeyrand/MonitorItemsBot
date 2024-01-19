package com.alexeyrand.monitoritemsbot.api.controller;

import com.alexeyrand.monitoritemsbot.api.dto.MessageDto;
import com.alexeyrand.monitoritemsbot.telegram.TelegramBot;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class MonitorItemsController {

    TelegramBot telegramBot;

    private static final String GET_ITEMS = "/items";
    private static final String GET_STATUS = "/status";

    @PostMapping(GET_ITEMS)
    public void getItems(@RequestBody String url) {
        String[] split = url.split(" ");
        telegramBot.sendMessage(split[1], split[0]);
    }

    @PostMapping(value = GET_STATUS, produces = APPLICATION_JSON_VALUE)
    public void getStatus(@RequestBody MessageDto messageDto) {
        System.out.println("Пришло");
        System.out.println(messageDto.getClass().getName());
        //telegramBot.deleteMessage(messageDto.getChatId(), messageDto.getMessageId());
    }

}
