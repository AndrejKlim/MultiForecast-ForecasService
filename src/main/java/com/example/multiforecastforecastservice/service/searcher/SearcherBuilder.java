package com.example.multiforecastforecastservice.service.searcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearcherBuilder {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static Searcher build(final String jsonWeatherResponse, final String userSearchPath) {
        JsonNode searchPathJson;
        JsonNode weatherDoc;
        try {
            searchPathJson = objectMapper.readTree(userSearchPath);
            weatherDoc = objectMapper.readTree(jsonWeatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return buildSearchNode(weatherDoc, null, searchPathJson);
    }

    private static Searcher buildSearchNode(final JsonNode doc, final String name,  final JsonNode jsonSearchNode) {
        if (jsonSearchNode.isContainerNode()) {
            if (jsonSearchNode.isArray()) {
                return null;
            }
            if (jsonSearchNode.isObject()) {
                JsonNode nested;
                if (name != null) {
                    nested = doc.get(name);
                } else {
                    nested = doc;
                }
                List<Searcher> searchers = new ArrayList<>();
                Iterator<Map.Entry<String, JsonNode>> entryIterator = jsonSearchNode.fields();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, JsonNode> next = entryIterator.next();
                    searchers.add(buildSearchNode(nested, next.getKey(), next.getValue()));
                }
                return new ObjectSearcher(doc, searchers);
            }
            return null;
        } else {
            return new FieldSearcher(doc, name, jsonSearchNode.asText());
        }
    }
}
