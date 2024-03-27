package com.alexeyrand.monitortelegrambot.telegram.methods;

import com.alexeyrand.monitortelegrambot.api.client.RequestSender;
import com.alexeyrand.monitortelegrambot.config.BotConfig;
import com.alexeyrand.monitortelegrambot.telegram.keyboard.HomeKeyboard;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final MessageSender messageSender;

    private final BotConfig config;

    private final RequestSender requestSender;

    public void HomeCommandReceived(String chatId) {
        String answer = "Menu:";
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        messageSender.sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'home'");
    }

    public void HelpCommandReceived(String chatId) {
        String answer = config.getHelpCommand();
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        messageSender.sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'help'");
    }

    public void StartCommandReceived(String chatId, Integer messageId) {
        try {
            requestSender.postStartRequest(URI.create(config.getStartEndpoint()), chatId, messageId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Monitor is running");
    }

    public void StopCommandReceived(String chatId, Integer messageId) {
        try {
            requestSender.getStopRequest(URI.create(config.getStopEndPoint()), chatId, messageId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Monitor is stopped");
    }

    public void StatusCommandReceived(String chatId) {
        String answer = "Монитор активен.";
        System.out.println(MessageSender.class.getName());
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        messageSender.sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'status'");
    }

}
