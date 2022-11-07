package com.example.multiforecastforecastservice;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.example.multiforecastforecastservice.dto.User;
import com.example.multiforecastforecastservice.enums.Duration;
import com.example.multiforecastforecastservice.enums.Source;
import com.example.multiforecastforecastservice.grpc.LocationClient;
import com.example.multiforecastforecastservice.persistence.entity.ForecastEntity;
import com.example.multiforecastforecastservice.persistence.repository.ForecastRepository;
import com.example.multiforecastforecastservice.service.OpenWeatherForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.json.JsonObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ForecastRunner implements CommandLineRunner {

    private final LocationClient client;
    private final ForecastRepository repository;
    private final OpenWeatherForecastService service;

    @Override
    public void run(final String... args) throws Exception {
//        log.info(client.getLocation(1L).orElse("empty"));
//        repository.save(new ForecastEntity(1L, Source.ALL, Duration.CURRENT, Instant.now(), new JsonObject("{\"sas\":123")));
        ForecastLocation forecastLocation = new ForecastLocation();
        forecastLocation.setLat(53.6884f);
        forecastLocation.setLon(23.8258f);
        User user = new User();
        user.setForecastLocation(forecastLocation);
        user.setId(1L);
        service.getOpenWeatherForecast(user);
    }
}
