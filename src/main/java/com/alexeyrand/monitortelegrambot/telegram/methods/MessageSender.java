package com.alexeyrand.monitortelegrambot.telegram.methods;

import com.alexeyrand.monitortelegrambot.telegram.TelegramBot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class MessageSender {

    @Lazy
    @Autowired
    private TelegramBot telegramBot;

    @Getter
    @Setter
    private String urlKey = new String();

    boolean waitMessage = false;

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
            telegramBot.execute(message);
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
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }

    public void deleteMessage(String chatId, Integer messageId, String status) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId + 1);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            telegramBot.execute(deleteMessage);
            if (status.equals("start")) {
                sendMessage(chatId, "Монитор активирован. Для остановки - нажмите stop.");
            }
            if (status.equals("stop")) {
                sendMessage(chatId, "Монитор остановлен");
            }
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }

    public void sendItem(String chatId, String textToSend, InputFile image) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(image);
        photo.setProtectContent(true);
        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(textToSend);
        try {
            telegramBot.execute(photo);
            log.info("Message sent");
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }


}
