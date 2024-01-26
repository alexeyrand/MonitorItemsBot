package com.alexeyrand.monitoritemsbot.api.controller;

import com.alexeyrand.monitoritemsbot.api.dto.ItemDto;
import com.alexeyrand.monitoritemsbot.api.dto.MessageDto;
import com.alexeyrand.monitoritemsbot.telegram.TelegramBot;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.Objects;

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
        String image = itemDto.getImage();

        String descriptionDTO = itemDto.getDescription();
        String description;
        try {
            description = descriptionDTO.substring(0, 80);
        } catch (StringIndexOutOfBoundsException SIOBE) {
            description = descriptionDTO;
        }
        //System.out.println(image);
        InputFile file = new InputFile(image);
        if (!Objects.equals(image, "")) {
            telegramBot.sendItem(chatId,
                    "[" + name + "]"
                            + "(" + href + "/)"
                            + "\nЦена: " + price
                            + description
                            + "\n"
                    //+ image
                    , file);
        } else {
            telegramBot.sendMessage(chatId, "[" + name + "]"
                    + "(" + href + "/)"
                    + "\nЦена " + price
                    + description);
        }
    }

    @PostMapping(value = GET_STATUS, consumes = {"application/json"})
    public MessageDto getStatus(@RequestBody MessageDto messageDto, @RequestParam String status) {
        telegramBot.deleteMessage(messageDto.getChatId(), messageDto.getMessageId(), status);
        return messageDto;
    }


}
