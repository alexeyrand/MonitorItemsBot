package com.alexeyrand.monitoritemsbot.telegram.handler;

import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class MessageHandler {
    public String[] urlParse(String url) {
        return url.split(" ");
    }
}
