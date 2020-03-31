package com.online.game.api.controller;

import com.online.game.api.model.Character;
import com.online.game.api.model.CharacterLevelInfo;
import com.online.game.api.model.ExperienceParam;
import com.online.game.api.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OnlineGameApiController {

    private final CharacterService characterService;

    @PostMapping("/character")
    public Character create() {
        return characterService.createCharacter();
    }

    @GetMapping("/character/{id}")
    public CharacterLevelInfo getCurrentCharacterInfo(@PathVariable("id") int characterId) {
        return characterService.getCharacterInfo(characterId);
    }

    @PostMapping("/character/{id}/exp")
    public ResponseEntity<CharacterLevelInfo> upgradeExperience(@PathVariable("id") int characterId, @RequestBody ExperienceParam experience) {
        if (experience.getExp() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        CharacterLevelInfo characterLevelInfo = characterService.updateAndGetLevelInfo(characterId, experience.getExp());
        if (characterLevelInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(characterLevelInfo);
    }
}
