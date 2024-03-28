package com.alexeyrand.monitortelegrambot.api.controllers;

import com.alexeyrand.monitortelegrambot.api.dto.ItemDto;
import com.alexeyrand.monitortelegrambot.api.dto.MessageDto;
import com.alexeyrand.monitortelegrambot.telegram.TelegramBot;
import com.alexeyrand.monitortelegrambot.telegram.inline.BlockListInline;
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
    private final TelegramBot telegramBot;
    private final BlockListInline inline;
    private static final String GET_ITEM = "/items";
    private static final String GET_STATUS = "/status";


    @PostMapping(value = GET_ITEM, consumes = {"application/json"})
    public void getItem(@RequestBody ItemDto itemDto) {
        String descriptionDTO = itemDto.getDescription();
        String description;
        try {
            description = descriptionDTO.substring(0, 80);
        } catch (StringIndexOutOfBoundsException SIOBE) {
            description = descriptionDTO;
        }
        if (!Objects.equals(itemDto.getImage(), "")) {
            if(Objects.equals(itemDto.getShop(), "")) {
                telegramBot.sendItem(itemDto.getChatId(),
                        "[" + itemDto.getName() + "]"
                                + "(" + itemDto.getHref() + "/)"
                                + "\nЦена: " + itemDto.getPrice() + " RUB"
                                + "\n" + description
                        , new InputFile(itemDto.getImage()));
            } else {
                telegramBot.sendItemWithInLineBlock(itemDto.getChatId(),
                        "[" + itemDto.getName() + "]"
                                + "(" + itemDto.getHref() + "/)"
                                + "\nЦена: " + itemDto.getPrice() + " RUB"
                                + "\n" + description
                        , new InputFile(itemDto.getImage()), inline.blockListInline(itemDto.getShop()));
            }
        } else {
            messageSender.sendMessage(itemDto.getChatId(), "[" + itemDto.getName() + "]"
                    + "(" + itemDto.getHref() + "/)"
                    + "\nЦена " + itemDto.getPrice() + " RUB"
                    + "\n" + description);
        }
    }

    @PostMapping(value = GET_STATUS, consumes = {"application/json"})
    public MessageDto getStatus(@RequestBody MessageDto messageDto, @RequestParam String status) {
        if (status.equals("start")) {
            messageSender.deleteMessage(messageDto.getChatId(), messageDto.getMessageId(), status);
        } else if (status.equals("state")) {
            messageSender.sendMessage(messageDto.getChatId(), "Монитор активен");
        }

        return messageDto;
    }


}
