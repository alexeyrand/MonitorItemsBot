package com.alexeyrand.monitoritemsbot.service;

import com.alexeyrand.monitoritemsbot.config.BotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    @Override
    public String getBotUsername() {
        return config.getName();
    }


    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            long chatId = message.getChatId();

            switch (messageText) {
                case "/start" -> StartCommandReceived(chatId, message.getChat().getFirstName());
                case "/help" -> sendMessage(chatId, "i help u");
                default -> sendMessage(chatId, "method not allowed");
            }
        }
    }

    public void StartCommandReceived(long chatId, String name) {
        String answer = "hi, " + name + " u chat id is - " + chatId;
        sendMessage(chatId, answer);
        log.info("Replied to user " + name);
    }

    public void HelpCommandReceived(long chatId, String name) {
        String answer = "i help u";
        sendMessage(chatId, answer);
        log.info("Replied to user " + name);
    }

    public void sendMessage(long chatId, String textToSend) {
    SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }

}
