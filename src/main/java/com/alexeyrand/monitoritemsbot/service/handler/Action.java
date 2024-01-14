package com.alexeyrand.monitoritemsbot.service.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public interface Action {
    SendMessage handle(Update update);
    SendMessage callback(Update update);

}
