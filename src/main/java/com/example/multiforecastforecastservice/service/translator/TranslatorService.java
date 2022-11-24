package com.example.multiforecastforecastservice.service.translator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslatorService {

    private final Map<String, Translations> translationsMap =
            Arrays.stream(Translations.values()).collect(Collectors.toMap(Translations::getOriginalName, tr -> tr));

    public String translate(final String forecast) {
        String nameValueSeparator = ": ";
        return forecast.lines()
                        .map(line -> line.split(nameValueSeparator))
                .map(nameValueArr -> {
                    var name = nameValueArr[0];
                    var value = nameValueArr[1];
                    if (translationsMap.containsKey(name)){
                        Translations translation = translationsMap.get(name);
                        return translation.getRuValue().formatted(translation.getValueTransformer().apply(value));
                    } else {
                        return name + nameValueSeparator + value;
                    }
                }).collect(Collectors.joining("\n"));
    }
}
