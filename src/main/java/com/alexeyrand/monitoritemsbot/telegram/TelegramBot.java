package com.alexeyrand.monitoritemsbot.telegram;


import com.alexeyrand.monitoritemsbot.api.client.RequestSender;
import com.alexeyrand.monitoritemsbot.config.BotConfig;
import com.alexeyrand.monitoritemsbot.telegram.handler.MessageHandler;
import com.alexeyrand.monitoritemsbot.telegram.keyboard.HomeKeyboard;
import com.alexeyrand.monitoritemsbot.telegram.keyboard.SettingsKeyboard;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    @Autowired
    private RequestSender requestSender;
    @Autowired
    private MessageHandler messageHandler;

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
                String[] parseUrl = messageHandler.urlParse(messageText);
                if (parseUrl.length != 2 || parseUrl[1].charAt(7) == '/') {
                    sendMessageWithKeyboard(chatId, "Некорректный url.\n" +
                            "Введите \"название\" и \"url\" через пробел:\n" +
                            "''Nike  http://avito...''" , SettingsKeyboard.setKeyboard());
                    waitMessage = false;
                } else {
                    urlsMap.put(urlKey, parseUrl[1]);

                    try {
                        requestSender.postUrlRequest(URI.create(config.getUrlEndPoint()), chatId, messageId, urlKey, parseUrl[1]);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    sendMessageWithKeyboard(chatId, "Url изменен на " + urlsMap.get(urlKey), SettingsKeyboard.setKeyboard());
                    waitMessage = false;
                }
            } else {
                switch (messageText) {
                    case "/help" -> HelpCommandReceived(chatId);
                    case "/start" -> StartCommandReceived(chatId, messageId);
                    case "/stop" -> StopCommandReceived(chatId, messageId);
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
    }

    public void HelpCommandReceived(String chatId) {
        String answer = config.getHelpCommand();
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'help'");
    }

    public void SettingsCommandReceived(String chatId) {
        String answer = "Для каждого url установите свой адрес ";
        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
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

    public void HomeCommandReceived(String chatId) {
        String answer = "Menu:";
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'home'");
    }

    public void StatusCommandReceived(String chatId) {
        String answer = "Текущие url:\n"
                + urlsMap.get("url1") + "\n"
                + urlsMap.get("url2") + "\n"
                + urlsMap.get("url3") + "\n"
                + urlsMap.get("url4");
        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'status'");
    }


    public void SetUrl1CommandReceived(String chatId) {
        String answer = "Введите url:";
        String urlKey = "url1";
        sendMessageWait(chatId, answer, urlKey);
        log.info("Setting button 'URL1'");
    }

    public void SetUrl2CommandReceived(String chatId) {
        String answer = "Введите url:";
        String url = new String("url2");
        sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL2'");
    }

    public void SetUrl3CommandReceived(String chatId) {
        String answer = "Введите url:";
        String url = new String("url3");
        sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL3'");
    }

    public void SetUrl4CommandReceived(String chatId) {
        String answer = "Введите url:";
        String url = new String("url4");
        sendMessageWait(chatId, answer, url);
        log.info("Setting button 'URL4'");
    }

    public void sendMessageWait(String chatId, String textToSend, String url) {
        setUrlKey("" + url);
        System.out.println(getUrlKey());
        sendMessage(chatId, textToSend);
        waitMessage = true;
    }

    public void sendMessage(String chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        System.out.println(textToSend);
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

    public void deleteMessage(String chatId, Integer messageId, String status) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId+1);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            execute(deleteMessage);
            if (status.equals("start")) {
                sendMessage(chatId, "Монитор активирован. Для остановки - нажмите stop."); }
            if (status.equals("stop")) {
                sendMessage(chatId, "Монитор остановлен"); }
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }

    public Integer sendMessageDeleted(String chatId, String textToSend, Integer messageId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
        return messageId;
    }
}
