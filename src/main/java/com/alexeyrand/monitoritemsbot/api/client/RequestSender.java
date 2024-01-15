package com.alexeyrand.monitoritemsbot.api.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestSender {

    public void getRequest(String url) {
        System.out.println("in method");
        final RestTemplate restTemplate = new RestTemplate();
        final String stringPosts = restTemplate.getForObject(url, String.class);
        System.out.println(stringPosts);
    }

}
