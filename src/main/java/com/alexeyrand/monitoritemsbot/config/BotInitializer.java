package com.alexeyrand.monitoritemsbot.config;

import com.alexeyrand.monitoritemsbot.service.TelegramBot;
import com.alexeyrand.monitoritemsbot.service.handler.EchoAction;
import com.alexeyrand.monitoritemsbot.service.handler.InfoAction;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer {
    @Autowired
    BotConfig config;
    TelegramBot bot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        var actions = Map.of(
                "/start", new InfoAction(
                        List.of(
                                "/start - Команды бота",
                                "/URL1 - Ввод данных для команды",
                                "/new - Регистрация пользователя")
                ),
                "URL1", new EchoAction("/echo"),
                "URL2", new EchoAction("/echo"),
                "URL3", new EchoAction("/echo"),
                "URL4", new EchoAction("/echo"));

        bot = new TelegramBot(config, actions);
        try {
            telegramBotsApi.registerBot(bot);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred:" + e.getMessage() + "/// in class: " + this.getClass().getName());
        }
    }
}