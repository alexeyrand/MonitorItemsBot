package com.alexeyrand.monitoritemsbot.api.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
public class UrlDto {
    private String name;
    private String url;
}
