package com.alexeyrand.monitortelegrambot.api.client;

import com.alexeyrand.monitortelegrambot.api.dto.MessageDto;
import com.alexeyrand.monitortelegrambot.api.dto.UrlDto;
import com.alexeyrand.monitortelegrambot.api.factories.MessageDtoFactory;
import com.alexeyrand.monitortelegrambot.api.factories.UrlDtoFactory;
import com.alexeyrand.monitortelegrambot.telegram.TelegramBot;
import com.alexeyrand.monitortelegrambot.telegram.methods.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class RequestSender {

    @Autowired
    private UrlDtoFactory urlDtoFactory;
    @Autowired
    private MessageDtoFactory messageDtoFactory;
    @Autowired
    private MessageSender messageSender;

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
            if (responseFuture.get().statusCode() == 200) {
                messageSender.sendMessage(chatId, "Монитор запускается ...\nЭто займет несколько секунд");
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
            if (responseFuture.get().statusCode() == 200) {
                messageSender.sendMessage(chatId, "Новый Url установлен");
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
            if (responseFuture.get().statusCode() == 200) {
                messageSender.sendMessage(chatId, "Монитор остановлен.");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }


}
