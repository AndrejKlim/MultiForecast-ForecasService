package com.example.multiforecastforecastservice.service;

import com.example.multiforecastforecastservice.enums.Duration;
import com.example.multiforecastforecastservice.enums.Source;
import com.example.multiforecastforecastservice.persistence.entity.ForecastEntity;
import com.example.multiforecastforecastservice.persistence.repository.ForecastRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class ForecastService {

    protected final ForecastRepository repo;

    protected ForecastService(final ForecastRepository repo) {
        this.repo = repo;
    }

    public abstract Source getSource();

    protected boolean isExpired(final ForecastEntity forecast, final Duration duration) {

        java.time.Duration forecastActualTimeLimit = switch (duration) {
            case CURRENT -> java.time.Duration.ofHours(2);
            case NEAREST -> java.time.Duration.ofHours(8);
            default -> java.time.Duration.ofHours(12);
        };

        LocalDateTime created = LocalDateTime.ofInstant(forecast.getCreated(), ZoneId.systemDefault());
        return java.time.Duration.between(created, LocalDateTime.now())
                .compareTo(forecastActualTimeLimit) > 0;
    }
}
