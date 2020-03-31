package com.online.game.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CharacterLevelInfo {

    private final int level;
    private final int exp;

    @JsonCreator
    public CharacterLevelInfo(@JsonProperty("level") int level, @JsonProperty("exp") int exp) {
        this.level = level;
        this.exp = exp;
    }
}
