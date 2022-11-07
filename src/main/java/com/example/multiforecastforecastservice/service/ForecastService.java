package com.example.multiforecastforecastservice.service;

import com.example.multiforecastforecastservice.dto.ForecastInfo;
import com.example.multiforecastforecastservice.dto.User;
import com.example.multiforecastforecastservice.dto.WeatherInfo;
import com.example.multiforecastforecastservice.enums.Duration;
import com.example.multiforecastforecastservice.enums.Source;
import com.example.multiforecastforecastservice.persistence.entity.ForecastEntity;
import com.example.multiforecastforecastservice.persistence.repository.ForecastRepository;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public abstract class ForecastService {

    public static final Sort SORT_DESC_BY_CREATED_AT = Sort.by(Sort.Direction.DESC, "createdAt");
    public static final WeatherInfo NO_WEATHER_INFO = () -> "No info";
    public static final ForecastInfo NO_FORECAST_INFO = () -> List.of("No info");

    protected final ForecastRepository repo;

    protected ForecastService(final ForecastRepository repo) {
        this.repo = repo;
    }

//    public abstract WeatherInfo getWeather(final User user);
//
//    public abstract ForecastInfo getForecast(final User user);

    public abstract Source getSource();

    protected boolean isExpired(final ForecastEntity forecast, final Duration duration) {
        long expiredTime = 0;
        if (duration == Duration.CURRENT) expiredTime = 7200; // 2 hours
        if (duration == Duration.NEAREST) expiredTime = 28800; // 8 hours

        final var forecastCreatedAt = forecast.getCreated().getEpochSecond();
        final var now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        return now - forecastCreatedAt > expiredTime;
    }
}
