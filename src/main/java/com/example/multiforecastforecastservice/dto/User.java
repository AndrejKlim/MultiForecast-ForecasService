package com.example.multiforecastforecastservice.dto;

import com.example.multiforecastforecastservice.enums.Source;
import lombok.Data;

import java.util.List;

@Data
public class User {
    private Long id;
    private String timeZone;
    private ForecastLocation forecastLocation;
    private List<Source> forecastSourceList;
}
