package com.example.multiforecastforecastservice.service.searcher;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FieldSearcher implements Searcher {
    private final JsonNode doc;
    private final String name;

    @Override
    public String get() {
        Object value = doc.get(name).asText();
        return name + ": " +  value;
    }
}
