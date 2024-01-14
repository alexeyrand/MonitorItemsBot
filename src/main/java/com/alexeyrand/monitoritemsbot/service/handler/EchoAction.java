package com.alexeyrand.monitoritemsbot.service.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class EchoAction implements Action {
    private final String action;

    public EchoAction(String action) {
        this.action = action;
    }

    @Override
    public SendMessage handle(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        String text = "Введите url:";

        return new SendMessage(chatId, text);
    }

    @Override
    public SendMessage callback(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        var text = "Action: " + action + ", data: " + msg.getText();
        return new SendMessage(chatId, text);
    }


}