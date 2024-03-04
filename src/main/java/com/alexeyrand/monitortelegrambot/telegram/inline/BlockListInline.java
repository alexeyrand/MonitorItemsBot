package com.alexeyrand.monitortelegrambot.telegram.inline;

import com.alexeyrand.monitortelegrambot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class BlockListInline {

    @Autowired
    private BotConfig config;

    public InlineKeyboardMarkup blockListInline(String shop) {

        String[] shopSplit = shop.split("/");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("block");
        inlineKeyboardButton1.setCallbackData(shopSplit[4]);

        //inlineKeyboardButton1.setUrl(config.getBlockEndPoint());
        rowInline1.add(inlineKeyboardButton1);
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

}
