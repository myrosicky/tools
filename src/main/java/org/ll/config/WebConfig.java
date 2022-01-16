package org.ll.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;

@Configuration @Slf4j
public class WebConfig {

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplateBuilder().build();
    }

    @Bean
    Yaml yaml(){
        return new Yaml();
    }

    @Bean
    Robot robot(){
        try {
            return new Robot();
        } catch (AWTException e) {
            log.error(null, e);
        }
        return null;
    }
}
