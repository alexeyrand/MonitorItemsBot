package com.alexeyrand.monitortelegrambot.telegram.methods;

import com.alexeyrand.monitortelegrambot.api.client.RequestSender;
import com.alexeyrand.monitortelegrambot.config.BotConfig;
import com.alexeyrand.monitortelegrambot.telegram.keyboard.HomeKeyboard;
import com.alexeyrand.monitortelegrambot.telegram.keyboard.SettingsKeyboard;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandler {

    private final MessageSender messageSender;

    private final BotConfig config;

    private final RequestSender requestSender;

    String URL = "";

    public void setURL(String URL) {
        this.URL = URL;
    }

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
    public void SettingsCommandReceived(String chatId) {
        String answer = "Для каждого url установите свой адрес ";
        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
        messageSender.sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'settings'");
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

    public void StatusCommandReceived(String chatId, Map<String, String> urlsMap) {
        String answer = "Текущие url:\n"
                + urlsMap.get("url1") + "\n"
                + urlsMap.get("url2") + "\n"
                + urlsMap.get("url3") + "\n"
                + urlsMap.get("url4");
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        messageSender.sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'status'");
    }

}
