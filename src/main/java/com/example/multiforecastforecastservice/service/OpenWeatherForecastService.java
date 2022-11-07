package com.example.multiforecastforecastservice.service;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.example.multiforecastforecastservice.dto.User;
import com.example.multiforecastforecastservice.dto.openweather.OpenWeatherForecast;
import com.example.multiforecastforecastservice.enums.Duration;
import com.example.multiforecastforecastservice.enums.Source;
import com.example.multiforecastforecastservice.persistence.entity.ForecastEntity;
import com.example.multiforecastforecastservice.persistence.repository.ForecastRepository;
import com.example.multiforecastforecastservice.rest.OpenWeatherApiClient;
import lombok.extern.slf4j.Slf4j;
import org.bson.json.JsonObject;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.example.multiforecastforecastservice.enums.Source.OPEN_WEATHER;

@Component
@Slf4j
public class OpenWeatherForecastService extends ForecastService{

    private final OpenWeatherApiClient apiClient;

    public OpenWeatherForecastService(final ForecastRepository repo,
                                      final OpenWeatherApiClient apiClient) {
        super(repo);
        this.apiClient = apiClient;
    }

//    @Override
//    public WeatherInfo getWeather(final User user) {
//        return getOpenWeatherForecast(user, CURRENT)
//                .map(WeatherInfo.class::cast)
//                .orElse(NO_WEATHER_INFO);
//    }
//
//    @Override
//    public ForecastInfo getForecast(final User user) {
//        return getOpenWeatherForecast(user, NEAREST)
//                .map(ForecastInfo.class::cast)
//                .orElse(NO_FORECAST_INFO);
//    }

    @Override
    public Source getSource() {
        return OPEN_WEATHER;
    }

    public String getOpenWeatherForecast(User user) {
        ForecastLocation forecastLocation = user.getForecastLocation();
        Optional<String> currentWeather = apiClient.getCurrentWeather(forecastLocation.getLat().toString(), forecastLocation.getLon().toString());
        ForecastEntity forecastEntity = new ForecastEntity(user.getId(), OPEN_WEATHER, Duration.MULTI, Instant.now(), new JsonObject(currentWeather.get()));
        repo.save(forecastEntity);

        return forecastEntity.getForecast().getJson();
    }

//    public Optional<OpenWeatherForecast> getOpenWeatherForecast(final User user, final Duration duration) {
//        ForecastLocation forecastLocation = user.getForecastLocation();
//        Optional<ForecastEntity> lastForecastByLocation =
//                repo.findFirstByForecastLocationAndSourceAndType(mapToForecastLocationEntity(forecastLocation),
//                        OPEN_WEATHER.name(), MIXED.name(), SORT_DESC_BY_CREATED_AT);
//
//        if (lastForecastByLocation.isPresent() && !isExpired(lastForecastByLocation.get(), forecastType)){
//            log.info("Suitable and not expired forecast found in storage, returning it. {}", lastForecastByLocation.get());
//            return Optional.ofNullable(readForecast(lastForecastByLocation.get()));
//        }
//        if (!apiCallCounterService.canApiCallBePerformed(OPEN_WEATHER)) {
//            log.info("Api call limit reached");
//            return Optional.empty();
//        }
//
//        log.info("Getting new forecast from api, saving and returning to bot");
//        Optional<String> jsonResponse = apiClient.getCurrentWeather(forecastLocation.getLat().toString(),
//                forecastLocation.getLon().toString());
//
//        jsonResponse.ifPresent(s -> apiCallCounterService.incrementApiCallCounter(OPEN_WEATHER));
//
//        Optional<ForecastEntity> entity = jsonResponse
//                .map(json -> new ForecastEntity(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), json,
//                        OPEN_WEATHER.name(), MIXED.name(), mapToForecastLocationEntity(forecastLocation)));
//        entity.ifPresent(repo::save);
//
//        return entity.map(OpenWeatherMapper::readForecast);
//    }
}
