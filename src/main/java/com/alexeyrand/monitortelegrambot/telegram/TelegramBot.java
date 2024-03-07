package com.alexeyrand.monitortelegrambot.telegram;


import com.alexeyrand.monitortelegrambot.api.client.RequestSender;
import com.alexeyrand.monitortelegrambot.config.BotConfig;
import com.alexeyrand.monitortelegrambot.telegram.methods.MessageSender;
import com.alexeyrand.monitortelegrambot.telegram.methods.EventHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URI;
import java.util.*;

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

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/help", "How to use the bot"));
        listOfCommands.add(new BotCommand("/start", "Start bot"));
        listOfCommands.add(new BotCommand("/status", "Status"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
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
                System.out.println("Функционал ожиданиния");
                if (false) {
                    messageSender.sendMessage(chatId, "Некорректный url.\n");
                } else {
                    messageSender.sendMessage(chatId, "Url изменен на ");
                    waitMessage = false;
                }
            } else {
                switch (messageText) {
                    case "/help" -> eventHandler.HelpCommandReceived(chatId);
                    case "/start" -> eventHandler.StartCommandReceived(chatId, messageId);
                    case "/stop" -> eventHandler.StopCommandReceived(chatId, messageId);
                    case "/status" -> eventHandler.StatusCommandReceived(chatId);
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

    public void sendItem(String chatId, String textToSend, InputFile image) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(image);
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
