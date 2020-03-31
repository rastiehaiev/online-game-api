package com.online.game.api.service;

import com.online.game.api.configuration.CharacterLevelConfiguration;
import com.online.game.api.model.CharacterLevelInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CharacterLevelManagementService {

    private final CharacterLevelConfiguration characterLevelConfiguration;

    public CharacterLevelInfo upgrade(CharacterLevelInfo currentCharacterLevelInfo, int exp) {
        int currentLevel = currentCharacterLevelInfo.getLevel();
        int currentExperience = currentCharacterLevelInfo.getExp() + exp;
        return upgrade(currentLevel, currentExperience);
    }

    private CharacterLevelInfo upgrade(int currentLevel, int currentExperience) {
        Integer requiredLevelUpExperience = characterLevelConfiguration.getRequiredLevelUpExperience(currentLevel);
        if (requiredLevelUpExperience == null || requiredLevelUpExperience > currentExperience) {
            return new CharacterLevelInfo(currentLevel, currentExperience);
        }
        return upgrade(currentLevel + 1, currentExperience - requiredLevelUpExperience);
    }
}
