package com.alexeyrand.monitortelegrambot.api.factories;

import com.alexeyrand.monitortelegrambot.api.dto.MessageDto;
import org.springframework.stereotype.Component;

@Component
public class MessageDtoFactory {
    public MessageDto makeMessageDto(String chatId, Integer messageId) {
        return MessageDto.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }
}
