package com.alexeyrand.monitortelegrambot.telegram.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class UrlsKeyboard {
    public static InlineKeyboardMarkup setKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("help");
        inlineKeyboardButton1.setCallbackData("fg");

        rowInline1.add(inlineKeyboardButton1);


        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("«Аврезе»");
        inlineKeyboardButton3.setCallbackData("«Аврезе»");
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("«Аврезе»");
        inlineKeyboardButton4.setCallbackData("«Аврезе»");
        rowInline2.add(inlineKeyboardButton3);
        rowInline2.add(inlineKeyboardButton4);



        List<InlineKeyboardButton> rowInline11 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton21 = new InlineKeyboardButton();
        inlineKeyboardButton21.setText("«Переход на внешний сайт»");
        inlineKeyboardButton21.setUrl("https://collections.hermitagemuseum.org");
        inlineKeyboardButton21.setCallbackData("«ПЕРЕХОД НА ВНЕШНИЙ САЙТ»");
        rowInline11.add(inlineKeyboardButton21);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline11);

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
