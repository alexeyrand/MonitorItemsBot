package com.alexeyrand.monitortelegrambot.api.controllers;

import com.alexeyrand.monitortelegrambot.api.dto.ItemDto;
import com.alexeyrand.monitortelegrambot.api.dto.MessageDto;
import com.alexeyrand.monitortelegrambot.telegram.TelegramBot;
import com.alexeyrand.monitortelegrambot.telegram.methods.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TelegramBotController {

    private final MessageSender messageSender;

    private static final String GET_ITEMS = "/items";
    private static final String GET_STATUS = "/status";

    @PostMapping(value = GET_ITEMS, consumes = {"application/json"})
    public void getItems(@RequestBody ItemDto itemDto) {
        String descriptionDTO = itemDto.getDescription();
        String description;
        try {
            description = descriptionDTO.substring(0, 80);
        } catch (StringIndexOutOfBoundsException SIOBE) {
            description = descriptionDTO;
        }

        if (!Objects.equals(itemDto.getImage(), "")) {
            messageSender.sendItem(itemDto.getChatId(),
                    "[" + itemDto.getName() + "]"
                            + "(" + itemDto.getHref() + "/)"
                            + "\nЦена: " + itemDto.getPrice() + " RUB"
                            + "\n" + description
                    , new InputFile(itemDto.getImage()));
        } else {
            messageSender.sendMessage(itemDto.getChatId(), "[" + itemDto.getName() + "]"
                    + "(" + itemDto.getHref() + "/)"
                    + "\nЦена " + itemDto.getPrice() + " RUB"
                    + "\n" + description);
        }
    }

    @PostMapping(value = GET_STATUS, consumes = {"application/json"})
    public MessageDto getStatus(@RequestBody MessageDto messageDto, @RequestParam String status) {
        messageSender.deleteMessage(messageDto.getChatId(), messageDto.getMessageId(), status);
        return messageDto;
    }


}
