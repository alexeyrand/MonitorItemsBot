package com.alexeyrand.monitoritemsbot.api.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Future;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class RequestSender {


    public void getRequest(URI url) {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.of(5, SECONDS))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .timeout(Duration.of(5, SECONDS))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println);


//        final RestTemplate restTemplate = new RestTemplate();
//        final String stringPosts = restTemplate.getForObject(url, String.class);
//        System.out.println(stringPosts);
    }

}
