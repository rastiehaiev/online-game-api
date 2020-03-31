package com.online.game.api.service;

import com.online.game.api.model.Character;
import com.online.game.api.model.CharacterLevelInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final AtomicInteger characterIdGenerator = new AtomicInteger(0);
    private final Map<Integer, CharacterLevelInfo> charactersMap = new ConcurrentHashMap<>();

    private final CharacterLevelManagementService characterLevelManagementService;

    public Character createCharacter() {
        int id = characterIdGenerator.incrementAndGet();
        CharacterLevelInfo characterLevelInfo = new CharacterLevelInfo(1, 0);
        charactersMap.put(id, characterLevelInfo);
        return new Character(id, characterLevelInfo);
    }

    public CharacterLevelInfo updateAndGetLevelInfo(int characterId, int exp) {
        return charactersMap.computeIfPresent(characterId,
                (id, levelInfo) -> characterLevelManagementService.upgrade(levelInfo, exp));
    }

    public CharacterLevelInfo getCharacterInfo(int characterId) {
        return charactersMap.get(characterId);
    }
}
