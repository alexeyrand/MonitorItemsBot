package com.alexeyrand.monitoritemsbot.telegram.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public interface Action {
    SendMessage handle(Update update);
    SendMessage callback(Update update);

}
