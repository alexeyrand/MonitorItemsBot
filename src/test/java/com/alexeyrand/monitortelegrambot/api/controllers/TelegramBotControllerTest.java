package com.alexeyrand.monitortelegrambot.api.controllers;

import com.alexeyrand.monitortelegrambot.api.dto.MessageDto;
import com.alexeyrand.monitortelegrambot.api.factories.MessageDtoFactory;
import com.alexeyrand.monitortelegrambot.telegram.TelegramBot;
import com.alexeyrand.monitortelegrambot.telegram.inline.BlockListInline;
import com.alexeyrand.monitortelegrambot.telegram.methods.MessageSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TelegramBotControllerTest {
    @Mock
    MessageSender messageSender;
    @Mock
    TelegramBot telegramBot;
    @Mock
    BlockListInline inline;

    @Mock
    MessageDtoFactory dtoFactory;

    @InjectMocks
    TelegramBotController controller;

    @Test
    void getItem() {
        // given
        //MessageDto messageDto = dtoFactory.makeMessageDto("12", 12);

        var responseEntity = this.controller.getStatus(messageDto, "status");

    }

}
