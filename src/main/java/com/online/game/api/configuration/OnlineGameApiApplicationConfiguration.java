package com.online.game.api.configuration;

import com.online.game.api.configuration.parser.CharacterLevelConfigurationParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class OnlineGameApiApplicationConfiguration {

    @Bean
    public CharacterLevelConfiguration characterLevelConfiguration(CharacterLevelConfigurationParser parser,
                                                                   @Value("classpath:${online-game.configuration-path}") Resource configurationFile) {
        return new CharacterLevelConfiguration(parser.parse(configurationFile));
    }
}
