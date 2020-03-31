package com.online.game.api.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CharacterLevelConfiguration {

    private final Map<Integer, Integer> characterLevelConfigurationMap;

    public Integer getRequiredLevelUpExperience(int currentLevel) {
        return characterLevelConfigurationMap.get(currentLevel);
    }
}
