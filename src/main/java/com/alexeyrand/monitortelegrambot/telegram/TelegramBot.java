package com.alexeyrand.monitortelegrambot.telegram;


import com.alexeyrand.monitortelegrambot.api.client.RequestSender;
import com.alexeyrand.monitortelegrambot.config.BotConfig;
import com.alexeyrand.monitortelegrambot.telegram.methods.MessageSender;
import com.alexeyrand.monitortelegrambot.telegram.keyboard.SettingsKeyboard;
import com.alexeyrand.monitortelegrambot.telegram.methods.EventHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URI;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    @Autowired
    private EventHandler eventHandler;

    @Autowired
    private RequestSender requestSender;

    @Autowired
    private MessageSender messageSender;

    boolean waitMessage = false;
    Map<String, String> urlsMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private String urlKey = new String();


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
        urlsMap.put("url1", "");
        urlsMap.put("url2", "");
        urlsMap.put("url3", "");
        urlsMap.put("url4", "");
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
        if (update.hasMessage() && update.getMessage().hasText()) {

            Message message = update.getMessage();
            Integer messageId = message.getMessageId();
            String chatId = message.getChatId().toString();
            String messageText = message.getText();


            if (waitMessage) {
                String[] parseUrl = messageText.split(" ");
                if (parseUrl.length != 2 || parseUrl[1].charAt(6) != '/') {
                    messageSender.sendMessageWithKeyboard(chatId, "Некорректный url.\n" +
                            "Введите \"название\" и \"url\" через пробел:\n" +
                            "''Nike  http://avito...''", SettingsKeyboard.setKeyboard());
                } else {
                    urlsMap.put(urlKey, parseUrl[1]);
                    try {
                        requestSender.postUrlRequest(URI.create(config.getUrlEndPoint()), chatId, messageId, urlKey, parseUrl[1]);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    messageSender.sendMessageWithKeyboard(chatId, "Url изменен на " + urlsMap.get(urlKey), SettingsKeyboard.setKeyboard());
                    waitMessage = false;
                }
            } else {
                switch (messageText) {
                    case "/help" -> eventHandler.HelpCommandReceived(chatId);
                    case "/start" -> eventHandler.StartCommandReceived(chatId, messageId);
                    case "/stop" -> eventHandler.StopCommandReceived(chatId, messageId);
                    case "/settings" -> eventHandler.SettingsCommandReceived(chatId);
                    case "/status" -> eventHandler.StatusCommandReceived(chatId, urlsMap);
                    case "<---   back" -> eventHandler.HomeCommandReceived(chatId);
                    case "url1" -> SetUrl1CommandReceived(chatId);
                    case "url2" -> SetUrl2CommandReceived(chatId);
                    case "url3" -> SetUrl3CommandReceived(chatId);
                    case "url4" -> SetUrl4CommandReceived(chatId);
                    default -> messageSender.sendMessage(chatId, "method not allowed");
                }

            }
        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            Integer messageId = message.getMessageId();
            String chatId = message.getChatId().toString();
            String shop = update.getCallbackQuery().getData();

            try {
                requestSender.getAddBlockList(URI.create(config.getBlockEndPoint() + shop), chatId, messageId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void SetUrl1CommandReceived(String chatId) {
        String answer = "Введите url:";
        String urlKey = "url1";
        messageSender.sendMessageWait(chatId, answer, urlKey);
        log.info("Setting button 'URL1'");
    }

    public void SetUrl2CommandReceived(String chatId) {
        String answer = "Введите url:";
        String url = new String("url2");
        messageSender.sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL2'");
    }

    public void SetUrl3CommandReceived(String chatId) {
        String answer = "Введите url:";
        String url = new String("url3");
        messageSender.sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL3'");
    }

    public void SetUrl4CommandReceived(String chatId) {
        String answer = "Введите url:";
        String url = new String("url4");
        messageSender.sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL4'");
    }

    public void sendItem(String chatId, String textToSend, InputFile image) {

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(image);
        photo.setProtectContent(true);
        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(textToSend);
        try {
            execute(photo);
            log.info("Message sent");
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }

    public void sendItemWithInLineBlock(String chatId, String textToSend, InputFile image, InlineKeyboardMarkup inline) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(image);
        photo.setProtectContent(true);
        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(textToSend);
        photo.setReplyMarkup(inline);
        try {
            execute(photo);
            log.info("Message sent");
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }

}
