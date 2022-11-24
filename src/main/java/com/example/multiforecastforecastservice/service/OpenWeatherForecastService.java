package com.example.multiforecastforecastservice.service;

import com.example.multiforecastforecastservice.dto.User;
import com.example.multiforecastforecastservice.enums.Duration;
import com.example.multiforecastforecastservice.enums.Source;
import com.example.multiforecastforecastservice.exception.NoForecastException;
import com.example.multiforecastforecastservice.grpc.UserServiceClient;
import com.example.multiforecastforecastservice.persistence.entity.ForecastEntity;
import com.example.multiforecastforecastservice.persistence.repository.ForecastRepository;
import com.example.multiforecastforecastservice.rest.OpenWeatherApiClient;
import com.example.multiforecastforecastservice.service.searcher.Searcher;
import com.example.multiforecastforecastservice.service.searcher.SearcherBuilder;
import com.mongodb.BasicDBObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

import static com.example.multiforecastforecastservice.enums.Source.OPEN_WEATHER;

@Component
@Slf4j
public class OpenWeatherForecastService extends ForecastService{

    private final OpenWeatherApiClient apiClient;
    private final UserServiceClient userServiceClient;

    public OpenWeatherForecastService(final ForecastRepository repo,
                                      final OpenWeatherApiClient apiClient,
                                      final UserServiceClient userServiceClient) {
        super(repo);
        this.apiClient = apiClient;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Source getSource() {
        return OPEN_WEATHER;
    }

    public String getOpenWeatherForecast(User user) {

        Optional<ForecastEntity> lastForecast = repo.findFirstByUserIdAndSourceOrderByCreatedDesc(user.getId(), OPEN_WEATHER);

        var currentWeather = getCurrentWeather(user, lastForecast);

        String userSearchPath = userServiceClient.getUserSearchPath(user.getId());
        Searcher searcher = null;
        if (!userSearchPath.isEmpty()) {
            searcher = SearcherBuilder.build(currentWeather, userSearchPath);
        } else {
            log.warn("No search path for user {} retrieved", user.getId());
        }

        return searcher != null ? searcher.get() : Strings.EMPTY;
    }

    private String getCurrentWeather(final User user, final Optional<ForecastEntity> lastForecast) {
        var currentWeather = "";
        try {
            if (lastForecast.isPresent() && !isExpired(lastForecast.get(), Duration.CURRENT)) {
                currentWeather = lastForecast.get().getForecast().toString();
            } else {
                log.info("No actual forecast for user {} found, requesting new", user.getId());
                var lat = user.getForecastLocation().getLat().toString();
                var lon = user.getForecastLocation().getLon().toString();
                Optional<String> currentWeatherOpt = apiClient.getCurrentWeather(lat, lon);
                if (currentWeatherOpt.isPresent()) {
                    ForecastEntity forecastEntity = new ForecastEntity(user.getId(), OPEN_WEATHER, Duration.MULTI, Instant.now(), BasicDBObject.parse(currentWeatherOpt.get()));
                    repo.save(forecastEntity);
                    currentWeather = currentWeatherOpt.get();
                } else {
                    log.warn("No forecast from API! User {}. Returning expired", user.getId());
                    currentWeather = lastForecast
                            .map(forecastEntity -> forecastEntity.getForecast().toString())
                            .orElseThrow(NoForecastException::new);
                }
            }
        } catch (NoForecastException e) {
            log.error("No forecast found for user {}, Check user info", user.getId());
        }
        return currentWeather;
    }
}
