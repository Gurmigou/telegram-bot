package com.yehorbukh.mathbotv2;

import com.yehorbukh.mathbotv2.bot_controller.Bot;
import com.yehorbukh.mathbotv2.config.ApplicationConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class MathBotV2Application {

    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(context.getBean("bot", Bot.class));

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
