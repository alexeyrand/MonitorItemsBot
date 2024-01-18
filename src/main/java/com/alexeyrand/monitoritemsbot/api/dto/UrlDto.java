package com.alexeyrand.monitoritemsbot.api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
