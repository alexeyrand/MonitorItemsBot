package com.alexeyrand.monitoritemsbot.telegram;


import com.alexeyrand.monitoritemsbot.api.client.RequestSender;
import com.alexeyrand.monitoritemsbot.config.BotConfig;
import com.alexeyrand.monitoritemsbot.telegram.keyboard.HomeKeyboard;
import com.alexeyrand.monitoritemsbot.telegram.keyboard.SettingsKeyboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    @Autowired
    RequestSender requestSender;
    boolean waitMessage = false;
    Map<String, StringBuilder> urlMap = new ConcurrentHashMap<>();

    private String URL = "";

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    StringBuilder URL1 = new StringBuilder();
    StringBuilder URL2 = new StringBuilder();
    StringBuilder URL3 = new StringBuilder();
    StringBuilder URL4 = new StringBuilder();

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/help", "How to use the bot"));
        listOfCommands.add(new BotCommand("/start", "Start bot"));
        listOfCommands.add(new BotCommand("/settings", "Settings bot"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
        urlMap.put("url1", URL1);
        urlMap.put("url2", URL2);
        urlMap.put("url3", URL3);
        urlMap.put("url4", URL4);
    }

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
        if (update.hasMessage() && update.getMessage().hasText() && waitMessage) {

            Message message = update.getMessage();
            String chatId = message.getChatId().toString();

            var newurl = urlMap.get(URL);
            newurl = new StringBuilder(message.getText());
            urlMap.put(URL, newurl);
            URL = message.getText();
            sendMessage(chatId, "Url изменён");
            waitMessage = false;

        } else if (update.hasMessage() && update.getMessage().hasText() && !waitMessage) {
            String chatId = update.getMessage().getChatId().toString();
            Message message = update.getMessage();
            String messageText = message.getText();

            switch (messageText) {
                case "/start" -> StartCommandReceived(chatId);
                case "/help" -> HelpCommandReceived(chatId);
                case "/settings" -> SettingsCommandReceived(chatId);
                case "/status" -> StatusCommandReceived(chatId);
                case "<---   back" -> HomeCommandReceived(chatId);
                case "url1" -> SetUrl1CommandReceived(chatId);
                case "url2" -> SetUrl2CommandReceived(chatId);
                case "url3" -> SetUrl3CommandReceived(chatId);
                case "url4" -> SetUrl4CommandReceived(chatId);
                default -> sendMessage(chatId, "method not allowed");
            }
        }
    }

    public void HelpCommandReceived(String chatId) {
        String answer = config.getHelpCommand();
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'help'");
    }

    public void StartCommandReceived(String chatId) {
        System.out.println("tut");
        requestSender.getRequest("http://localhost:9090/start");
        System.out.println();
        String answer = "Монитор запущен";
        sendMessage(chatId, answer);
        log.info("Monitor is running");
    }

        public void SettingsCommandReceived(String chatId) {
        String answer = "Для каждого url установите соответствующий адрес";
        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'settings'");
    }

    public void HomeCommandReceived(String chatId) {
        String answer = "Menu:";
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'home'");
    }
    public void  StatusCommandReceived(String chatId) {
        String answer = "Текущие url:\n";
        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'status'");
    }


    public void SetUrl1CommandReceived(String chatId) {
        String answer = "Введите url:";
        StringBuilder url = new StringBuilder("url1");
        sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL1'");
    }

    public void SetUrl2CommandReceived(String chatId) {
        String answer = "Введите url:";
        StringBuilder url = new StringBuilder("url2");
        sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL2'");
    }

    public void SetUrl3CommandReceived(String chatId) {
        String answer = "Введите url:";
        StringBuilder url = new StringBuilder("url3");
        sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL3'");
    }

    public void SetUrl4CommandReceived(String chatId) {
        String answer = "Введите url:";
        StringBuilder url = new StringBuilder("url4");
        sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL4'");
    }

    public void sendMessageWait(String chatId, String textToSend, StringBuilder url) {
        setURL("" + url);
        sendMessage(chatId, textToSend);
        waitMessage = true;
    }

    public void sendMessage(String chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
            log.info("Message sent");
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }

    public void sendMessageWithKeyboard(String chatId, String textToSend, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(keyboardMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }
}