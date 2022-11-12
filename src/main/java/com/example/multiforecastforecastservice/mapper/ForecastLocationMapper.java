package com.example.multiforecastforecastservice.mapper;

import com.example.multiforecastforecastservice.dto.ForecastLocation;
import com.multiforecast.userservice.LocationResponse;

public class ForecastLocationMapper {

    public static ForecastLocation fromResponse(LocationResponse response) {
        return new ForecastLocation(response.getLat(), response.getLon());
    }
}
