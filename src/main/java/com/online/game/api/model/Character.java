package com.online.game.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Character {

    private final int id;
    private final CharacterLevelInfo levelInfo;

    @JsonCreator
    public Character(@JsonProperty("id") int id, @JsonProperty("levelInfo") CharacterLevelInfo levelInfo) {
        this.id = id;
        this.levelInfo = levelInfo;
    }
}
