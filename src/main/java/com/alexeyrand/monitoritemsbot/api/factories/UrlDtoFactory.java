package com.alexeyrand.monitoritemsbot.api.factories;

import com.alexeyrand.monitoritemsbot.api.dto.UrlDto;
import lombok.Builder;
import lombok.Data;
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
