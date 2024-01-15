//package com.alexeyrand.monitoritemsbot.telegram.methods;
//
//import com.alexeyrand.monitoritemsbot.api.client.RequestSender;
//import com.alexeyrand.monitoritemsbot.config.BotConfig;
//import com.alexeyrand.monitoritemsbot.telegram.TelegramBot;
//import com.alexeyrand.monitoritemsbot.telegram.keyboard.HomeKeyboard;
//import com.alexeyrand.monitoritemsbot.telegram.keyboard.SettingsKeyboard;
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ReceivedMethod {
//
//    final TelegramBot telegramBot;
//    BotConfig config;
//    RequestSender requestSender;
//    String URL = "";
//
//    public void setURL(String URL) {
//        this.URL = URL;
//    }
//    boolean waitMessage = false;
//
//    public void StartCommandReceived(String chatId) {
//
//        requestSender.getRequest("http://localhost:8080/start");
//        String answer = "Монитор запущен";
//        telegramBot.sendMessage(chatId, answer);
//        log.info("Monitor is running");
//    }
//
//    public void HelpCommandReceived(String chatId) {
//        String answer = "config.getHelpCommand()";
//        System.out.println("тут");
//        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
//        System.out.println("тут");
//        telegramBot.sendMessageWithKeyboard(chatId, answer, keyboard);
//        log.info("Setting button 'help'");
//    }
//
////    public static void SettingsCommandReceived(String chatId) {
////        String answer = "Для каждого url установите соответствующий адрес";
////        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
////        telegramBot.sendMessageWithKeyboard(chatId, answer, keyboard);
////        log.info("Setting button 'settings'");
////    }
////
////    public static void HomeCommandReceived(String chatId) {
////        String answer = "Menu:";
////        ReplyKeyboardMarkup keyboard = HomeKeyboard.setKeyboard();
////        telegramBot.sendMessageWithKeyboard(chatId, answer, keyboard);
////        log.info("Setting button 'home'");
////    }
////    public static void  StatusCommandReceived(String chatId) {
////        String answer = "Текущие url:\n";
////        ReplyKeyboardMarkup keyboard = SettingsKeyboard.setKeyboard();
////        telegramBot.sendMessageWithKeyboard(chatId, answer, keyboard);
////        log.info("Setting button 'status'");
////    }
////
////
////    public static void SetUrl1CommandReceived(String chatId) {
////        String answer = "Введите url:";
////        StringBuilder url = new StringBuilder("url1");
////        telegramBot.sendMessageWait(chatId, answer, url);
////        log.info("Setting button 'URL1'");
////    }
////
////    public static void SetUrl2CommandReceived(String chatId) {
////        String answer = "Введите url:";
////        StringBuilder url = new StringBuilder("url2");
////        telegramBot.sendMessageWait(chatId, answer, url);
////        log.info("Setting button 'URL2'");
////    }
////
////    public static void SetUrl3CommandReceived(String chatId) {
////        String answer = "Введите url:";
////        StringBuilder url = new StringBuilder("url3");
////        telegramBot.sendMessageWait(chatId, answer, url);
////        log.info("Setting button 'URL3'");
////    }
////
////    public static void SetUrl4CommandReceived(String chatId) {
////        String answer = "Введите url:";
////        StringBuilder url = new StringBuilder("url4");
////        telegramBot.sendMessageWait(chatId, answer, url);
////        log.info("Setting button 'URL4'");
////    }
//}
