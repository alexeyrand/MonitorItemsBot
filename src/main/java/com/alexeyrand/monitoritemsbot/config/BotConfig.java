package com.alexeyrand.monitoritemsbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@Data
@PropertySource("application.yaml")
public class BotConfig {

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Value("${answer.command.help}")
    private String helpCommand;

    //////////////////////////////////////////////////////////////////////////////////////////////// monitor endpoints

    @Value("${monitor.endpoint.start}")
    private String startEndpoint;

    @Value("${monitor.endpoint.stop}")
    private String stopEndPoint;

    @Value("${monitor.endpoint.url}")
    private String urlEndPoint;

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////// ulr

    @Value("${monitor.urls.file.name}")
    private String urlsFileName;

}
