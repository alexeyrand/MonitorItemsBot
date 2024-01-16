package com.alexeyrand.monitoritemsbot.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UrlDto {
    private String name;
    private String url;
}
