package com.alexeyrand.monitoritemsbot.service;

import com.alexeyrand.monitoritemsbot.config.BotConfig;
import com.alexeyrand.monitoritemsbot.model.Item;
import com.alexeyrand.monitoritemsbot.service.keyboard.HomeKeyboard;
import com.alexeyrand.monitoritemsbot.service.keyboard.SettingsKeyboard;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    boolean waitMessage = false;
    String URL1 = "";

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
        if (update.hasMessage() && update.getMessage().hasText() && !waitMessage) {
            Message message = update.getMessage();
            String messageText = message.getText();
            long chatId = message.getChatId();

            switch (messageText) {
                case "/start" -> StartCommandReceived(chatId, message.getChat().getFirstName());
                case "/help" -> HelpCommandReceived(chatId, message.getChat().getFirstName());
                case "settings" -> SettingsCommandReceived(chatId, message.getChat().getFirstName());
                case "URL1" -> SetUrl1CommandReceived(chatId, message.getChat().getFirstName());
                default -> sendMessage(chatId, "method not allowed");
            }
        }
        ////
        if (update.hasMessage() && update.getMessage().hasText() && waitMessage) {
            Message message = update.getMessage();
            URL1 = message.getText();
            System.out.println(URL1);
            //waitMessage = false;
        }
    }

    public void StartCommandReceived(long chatId, String name) {
        final WebDriver driver;
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get("https://www.avito.ru/moskva/telefony/mobilnye_telefony/apple-ASgBAgICAkS0wA3OqzmwwQ2I_Dc?cd=1&s=104&user=1");
        List<WebElement> selectors = driver.findElements(xpath("//div[@data-marker='item']"));
        for (WebElement e : selectors) {
            Item item = new Item(e);
            sendMessage(chatId, item.getName()
                    + "\n" + item.getPrice()
                    + "\n" + item.getHref());
            try {
                Thread.sleep(3000);
            } catch (Exception ee) {
                System.out.println(ee.getMessage());
            }

        }
        String answer = "hi, " + name + " u chat id is - " + chatId;
        sendMessage(chatId, answer);
        log.info("Replied to user " + name);
    }

    public void HelpCommandReceived(long chatId, String name) {
        String answer = config.getHelpCommand();
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Replied to user " + name);
    }

    public void SettingsCommandReceived(long chatId, String name) {
        String answer = "Введите настройки";
        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Replied to user " + name);
        waitMessage = true;
    }

    public void SetUrl1CommandReceived(long chatId, String name) {

        System.out.println(URL1);
        String answer = "Введите url страницы с товарами";
        sendMessage(chatId, answer);
        log.info("Replied to user " + name);
        waitMessage = true;
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

    public void sendMessageWithKeyboard(long chatId, String textToSend, ReplyKeyboardMarkup keyboardMarkup) {
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
