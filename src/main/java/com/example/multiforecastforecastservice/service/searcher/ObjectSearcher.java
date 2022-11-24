package com.example.multiforecastforecastservice.service.searcher;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ObjectSearcher implements Searcher {
    private final List<Searcher> searchers;

    @Override
    public String get() {
        return searchers.stream().map(Searcher::get).collect(Collectors.joining("\n"));
    }
}
