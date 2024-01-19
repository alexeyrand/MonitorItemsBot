package com.alexeyrand.monitoritemsbot.api.client;

import com.alexeyrand.monitoritemsbot.api.dto.MessageDto;
import com.alexeyrand.monitoritemsbot.api.dto.UrlDto;
import com.alexeyrand.monitoritemsbot.api.factories.MessageDtoFactory;
import com.alexeyrand.monitoritemsbot.telegram.TelegramBot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component

public class RequestSender {

    @Lazy
    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private MessageDtoFactory messageDtoFactory;
    private final ObjectMapper mapper = new ObjectMapper();

    public void postStartRequest(URI url, String chatId, Integer messageId) {
        MessageDto messageDto = messageDtoFactory.makeMessageDto(chatId, messageId);
        String jsonMessageDto;
        try {
            jsonMessageDto = mapper.writeValueAsString(messageDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.of(5, SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(jsonMessageDto))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            String[] response = responseFuture.get().toString().split( "\\) ");
            //System.out.println(response[1].equals("200"));
            if (response[1].equals("200")) {
                telegramBot.sendMessage(chatId, "Монитор запускается ...\nЭто займет несколько секунд");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void postUrlRequest(URI url, String chatId, UrlDto urlDto) throws JsonProcessingException {

        String jsonUrlDto = mapper.writeValueAsString(urlDto);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.of(5, SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(jsonUrlDto))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            String response = responseFuture.get().toString();
            if (response.charAt(0) == '2') {
                telegramBot.sendMessage(chatId, "Новый Url установлен");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }



    }

}
