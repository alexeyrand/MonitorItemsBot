package com.alexeyrand.monitortelegrambot.api.factories;

import com.alexeyrand.monitortelegrambot.api.dto.UrlDto;
import org.springframework.stereotype.Component;

@Component
public class UrlDtoFactory {
    public UrlDto makeUrlDto(String name, String url) {
        return UrlDto.builder()
                .name(name)
                .url(url)
                .build();
    }
}
