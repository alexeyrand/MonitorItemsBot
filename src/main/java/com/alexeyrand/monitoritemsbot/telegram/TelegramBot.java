package com.alexeyrand.monitoritemsbot.telegram;


import com.alexeyrand.monitoritemsbot.api.client.RequestSender;
import com.alexeyrand.monitoritemsbot.api.dto.UrlDto;
import com.alexeyrand.monitoritemsbot.api.factories.UrlDtoFactory;
import com.alexeyrand.monitoritemsbot.config.BotConfig;
import com.alexeyrand.monitoritemsbot.telegram.handler.MessageHandler;
import com.alexeyrand.monitoritemsbot.telegram.keyboard.HomeKeyboard;
import com.alexeyrand.monitoritemsbot.telegram.keyboard.SettingsKeyboard;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    @Autowired
    private RequestSender requestSender;
    @Autowired
    private MessageHandler messageHandler;
    @Autowired
    private UrlDtoFactory urlDtoFactory;
    boolean waitMessage = false;
    Map<String, String> urlMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    private String URL = new String();


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
        urlMap.put("url1", "");
        urlMap.put("url2", "");
        urlMap.put("url3", "");
        urlMap.put("url4", "");
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
            String chatId = message.getChatId().toString();
            String messageText = message.getText();

            if (waitMessage) {
                String[] parseUrl = messageHandler.urlParse(messageText);
                urlMap.put(URL, parseUrl[1]);
                UrlDto urlDto = urlDtoFactory.makeUrlDto(parseUrl[0], parseUrl[1]);

                sendMessageWithKeyboard(chatId, "Url изменен на " + urlMap.get(URL), SettingsKeyboard.setKeyboard());
                waitMessage = false;

            } else {

                switch (messageText) {
                    case "/help" -> HelpCommandReceived(chatId);
                    case "/start" -> StartCommandReceived(chatId);
                    case "/stop" -> StopCommandReceived(chatId);
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
        String answer = "Для каждого url установите соответствующий адрес";
        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'settings'");
    }

    public void StartCommandReceived(String chatId) {

        requestSender.getRequest(URI.create("http://localhost:9090/start"));
        String answer = "Монитор запущен";
        sendMessage(chatId, answer);
        log.info("Monitor is running");
    }

    public void StopCommandReceived(String chatId) {
        requestSender.getRequest(URI.create("http://localhost:9090/stop"));
        String answer = "Монитор остановлен";
        sendMessage(chatId, answer);
        log.info("Monitor is stopped");
    }

    public void HomeCommandReceived(String chatId) {
        String answer = "Menu:";
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Setting button 'home'");
    }
    public void  StatusCommandReceived(String chatId) {
        String answer = "Текущие url:\n"
                + urlMap.get("url1") + "\n"
                + urlMap.get("url2") + "\n"
                + urlMap.get("url3") + "\n"
                + urlMap.get("url4");
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
        setURL("" + url);
        System.out.println(getURL());
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
