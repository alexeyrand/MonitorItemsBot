package com.alexeyrand.monitoritemsbot.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class InfoAction implements Action {
    private final List<String> actions;

    public InfoAction(List<String> actions) {
        this.actions = actions;
    }

    @Override
    public SendMessage handle(Update update) {
        Message msg = update.getMessage();
        String chatId = msg.getChatId().toString();
        StringBuilder out = new StringBuilder();
        out.append("Выберите действие:").append("\n");
        for (String action : actions) {
            out.append(action).append("\n");
        }
        return new SendMessage(chatId, out.toString());
    }

    @Override
    public SendMessage callback(Update update) {
        return handle(update);
    }
}
