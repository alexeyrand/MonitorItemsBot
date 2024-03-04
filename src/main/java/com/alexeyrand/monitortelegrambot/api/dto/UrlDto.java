package com.alexeyrand.monitortelegrambot.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    public String name;
    public String url;
}
