package com.example.multiforecastforecastservice.service.translator;

import com.example.multiforecastforecastservice.util.ConvertUtils;

import java.util.function.Function;

public enum Translations {
    TEMPERATURE("temp", "Температура: %s градусов"),
    PRESSURE("pressure", "Давление: %s мм рт. ст.", value -> String.valueOf(ConvertUtils.hPaToMm(Integer.parseInt(value)))),
    HUMIDITY("humidity", "Относительная влажность: %s %%"),
    WIND_SPEED("wind_speed", "Скорость ветра: %s м/с");


    private final String originalName;
    private final String ruValue;
    private final Function<String, String> valueTransformer;

    Translations(final String originalName, final String ruValue) {
        this.originalName = originalName;
        this.ruValue = ruValue;
        this.valueTransformer = value -> value;
    }

    Translations(final String originalName, final String ruValue, final Function<String, String> valueTransformer) {
        this.originalName = originalName;
        this.ruValue = ruValue;
        this.valueTransformer = valueTransformer;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getRuValue() {
        return ruValue;
    }

    public Function<String, String> getValueTransformer() {
        return valueTransformer;
    }
}
