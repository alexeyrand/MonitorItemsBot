package com.alexeyrand.monitoritemsbot.service.keyboard;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class SettingsKeyboard {
    public static ReplyKeyboardMarkup setKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("URL1");
        row1.add("URL2");
        KeyboardRow row2 = new KeyboardRow();
        row2.add("URL3");
        row2.add("URL4");
        KeyboardRow row3 = new KeyboardRow();
        row3.add("<---   Back");
        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        return keyboardMarkup;
    }
}
