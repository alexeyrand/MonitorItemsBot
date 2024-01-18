package com.alexeyrand.monitoritemsbot.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UrlDto urlDto = new UrlDto("hhellloo", "url");
        String jsonString = mapper.writeValueAsString(urlDto);
        System.out.println(jsonString);
    }
}


class UrlDto {

    public String name;
    public String url;

    public UrlDto(String name, String url) {
        this.name = name;
        this.url = url;
    }

}