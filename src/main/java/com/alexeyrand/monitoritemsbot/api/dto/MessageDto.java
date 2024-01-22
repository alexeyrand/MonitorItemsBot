package com.alexeyrand.monitoritemsbot.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class MessageDto {
    private String chatId;
    private Integer messageId;
}
