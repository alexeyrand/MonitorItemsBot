package com.alexeyrand.monitoritemsbot.api.controller;

import com.alexeyrand.monitoritemsbot.api.dto.ItemDto;
import com.alexeyrand.monitoritemsbot.api.dto.MessageDto;
import com.alexeyrand.monitoritemsbot.telegram.TelegramBot;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class MonitorItemsController {

    TelegramBot telegramBot;

    private static final String GET_ITEMS = "/items";
    private static final String GET_STATUS = "/status";

    @PostMapping(value = GET_ITEMS, consumes = {"application/json"})
    public void getItems(@RequestBody ItemDto itemDto) {
        String chatId = itemDto.getChatId();
        String name = itemDto.getName();
        String price = itemDto.getPrice();
        String href = itemDto.getHref();
        String descr = itemDto.getDescription().substring(0, 210);
        String image = itemDto.getImage();
        System.out.println(itemDto);
        telegramBot.sendItem(chatId, "[" + name + "]" + "(" + href +")" + "\nЦена: " + price + "\n\n" +
                descr + "\n" + image);
    }

    @PostMapping(value = GET_STATUS, consumes = {"application/json"})
    public MessageDto getStatus(@RequestBody MessageDto messageDto, @RequestParam String status) {
        telegramBot.deleteMessage(messageDto.getChatId(), messageDto.getMessageId(), status);
        return messageDto;
    }


}
