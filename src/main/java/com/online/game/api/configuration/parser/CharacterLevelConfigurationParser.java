package com.online.game.api.configuration.parser;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Component
public class CharacterLevelConfigurationParser {

    private final CharacterLevelConfigurationInnerParser innerParser = new CharacterLevelConfigurationInnerParser();

    public Map<Integer, Integer> parse(Resource resource) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readLine(); // skip the first header line
            return innerParser.parse(reader.lines());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load resource " + resource.getFilename(), e);
        }
    }
}
