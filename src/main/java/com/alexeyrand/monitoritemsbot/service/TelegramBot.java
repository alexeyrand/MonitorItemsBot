package com.alexeyrand.monitoritemsbot.service;

import com.alexeyrand.monitoritemsbot.config.BotConfig;
import com.alexeyrand.monitoritemsbot.model.Item;
import com.alexeyrand.monitoritemsbot.service.handler.Action;
import com.alexeyrand.monitoritemsbot.service.keyboard.HomeKeyboard;
import com.alexeyrand.monitoritemsbot.service.keyboard.SettingsKeyboard;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

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


    public TelegramBot(BotConfig config, Map<String, Action> actions) {
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
        System.out.println("я тут " + waitMessage);
        System.out.println(URL1);
//        System.out.println("Есть ли сообщение? " + update.hasMessage());
//        System.out.println("А текст? " + update.getMessage().hasText());
//        System.out.println(update.hasMessage() && update.getMessage().hasText() && waitMessage == true);
//        System.out.println(k);

        if (update.hasMessage() && update.getMessage().hasText() && waitMessage == true) {
            System.out.println(URL);
            Message message1 = update.getMessage();
            var newurl = urlMap.get(URL);
            newurl = new StringBuilder(message1.getText());
            urlMap.put(URL, newurl);
            URL = message1.getText();
            System.out.println("new url: " + URL1);
            System.out.println("Меняю с тру на фолс!");
            waitMessage = false;

        } else if (update.hasMessage() && update.getMessage().hasText() && waitMessage == false) {
            String chatId = update.getMessage().getChatId().toString();
            Message message = update.getMessage();
            String messageText = message.getText();

            switch (messageText) {
                case "/start" -> StartCommandReceived(chatId, message.getChat().getFirstName());
                case "/help" -> HelpCommandReceived(chatId, message.getChat().getFirstName());
                case "settings" -> SettingsCommandReceived(chatId, message.getChat().getFirstName());
                case "URL1" -> SetUrl1CommandReceived(chatId, message.getChat().getFirstName());
                case "URL2" -> SetUrl2CommandReceived(chatId, message.getChat().getFirstName());
                case "URL3" -> SetUrl3CommandReceived(chatId, message.getChat().getFirstName());
                case "URL4" -> SetUrl4CommandReceived(chatId, message.getChat().getFirstName());
                default -> sendMessage(chatId, "method not allowed");
            }
        }
    }


    public void StartCommandReceived(String chatId, String name) {
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

    public void HelpCommandReceived(String chatId, String name) {
        String answer = config.getHelpCommand();
        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Replied to user " + name);
    }

    public void SettingsCommandReceived(String chatId, String name) {
        String answer = "Введите настройки";
        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
        sendMessageWithKeyboard(chatId, answer, keyboard);
        log.info("Replied to user " + name);

    }

    public void SetUrl1CommandReceived(String chatId, String name) {
        String answer = "Введите url страницы с товарами";
        StringBuilder url = new StringBuilder("url1");
        sendMessageWait(chatId, answer, url);
        log.info("Replied to user " + name);
    }
    public void SetUrl2CommandReceived(String chatId, String name) {
        String answer = "Введите url страницы с товарами";
        StringBuilder url = new StringBuilder("url2");
        sendMessageWait(chatId, answer, url);
        log.info("Replied to user " + name);
    }
    public void SetUrl3CommandReceived(String chatId, String name) {
        String answer = "Введите url страницы с товарами";
        StringBuilder url = new StringBuilder("url3");
        sendMessageWait(chatId, answer, url);
        log.info("Replied to user " + name);
    }
    public void SetUrl4CommandReceived(String chatId, String name) {
        String answer = "Введите url страницы с товарами";
        StringBuilder url = new StringBuilder("url4");
        sendMessageWait(chatId, answer, url);
        log.info("Replied to user " + name);
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
