package com.yehorbukh.mathbotv2.config;

import com.yehorbukh.mathbotv2.bot_controller.Bot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.yehorbukh.mathbotv2")
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
public class ApplicationConfig {
    @Bean
    public Bot bot() {
        return new Bot();
    }
}
