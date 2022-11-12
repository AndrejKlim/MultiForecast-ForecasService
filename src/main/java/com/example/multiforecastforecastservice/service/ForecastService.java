package com.example.multiforecastforecastservice.service;

import com.example.multiforecastforecastservice.enums.Duration;
import com.example.multiforecastforecastservice.enums.Source;
import com.example.multiforecastforecastservice.persistence.entity.ForecastEntity;
import com.example.multiforecastforecastservice.persistence.repository.ForecastRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public abstract class ForecastService {

    protected final ForecastRepository repo;

    protected ForecastService(final ForecastRepository repo) {
        this.repo = repo;
    }

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
