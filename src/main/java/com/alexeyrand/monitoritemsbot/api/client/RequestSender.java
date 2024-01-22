package com.alexeyrand.monitoritemsbot.api.client;

import com.alexeyrand.monitoritemsbot.api.dto.MessageDto;
import com.alexeyrand.monitoritemsbot.api.dto.UrlDto;
import com.alexeyrand.monitoritemsbot.api.factories.MessageDtoFactory;
import com.alexeyrand.monitoritemsbot.api.factories.UrlDtoFactory;
import com.alexeyrand.monitoritemsbot.telegram.TelegramBot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
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
    private UrlDtoFactory urlDtoFactory;
    @Autowired
    private MessageDtoFactory messageDtoFactory;
    private final ObjectMapper mapper = new ObjectMapper();

    public void postStartRequest(URI url, String chatId, Integer messageId) throws JsonProcessingException {

        MessageDto messageDto = messageDtoFactory.makeMessageDto(chatId, messageId);
        String jsonMessageDto = mapper.writeValueAsString(messageDto);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .timeout(Duration.of(5, SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(jsonMessageDto))
                .build();
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            String[] response = responseFuture.get().toString().split("\\) ");

            if (response[1].equals("200")) {
                telegramBot.sendMessage(chatId, "Монитор запускается ...\nЭто займет несколько секунд");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void postUrlRequest(URI url, String chatId, Integer messageId, String nameUrl, String hrefUrl) throws JsonProcessingException {
        UrlDto urlDto = urlDtoFactory.makeUrlDto(nameUrl, hrefUrl);
        String jsonUrlDto = mapper.writeValueAsString(urlDto);
        System.out.println(jsonUrlDto);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .timeout(Duration.of(5, SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(jsonUrlDto))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            String response = responseFuture.get().toString();
            System.out.println(response);
            if (response.charAt(0) == '2') {
                telegramBot.sendMessage(chatId, "Новый Url установлен");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void getStopRequest(URI url, String chatId, Integer messageId) throws JsonProcessingException {

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.of(5, SECONDS))
                .GET()
                .build();
        CompletableFuture<HttpResponse<String>> responseFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            String response = responseFuture.get().toString();
            System.out.println(response);
            if (response.charAt(0) == '2') {
                telegramBot.sendMessage(chatId, "Монитор остановлен.");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }


}
