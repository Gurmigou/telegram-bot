package com.yehorbukh.mathbotv2.bot_controller;

import com.yehorbukh.mathbotv2.service.CommandSelectorService;
import com.yehorbukh.mathbotv2.service.MessageStatsCounter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "NAME";
    private static final String BOT_TOKEN = "TOKEN";

    @Setter(onMethod=@__({@Autowired}))
    private CommandSelectorService commands;
    @Setter(onMethod=@__({@Autowired}))
    private MessageStatsCounter statsCounter;

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    private boolean isCommand(Update update) {
        return update.getMessage().isCommand();
    }

    private boolean isJustMessage(Update update) {
        return update.getMessage() != null &&
               !update.getMessage().getFrom().getIsBot() &&
               update.getMessage().getForwardFrom() == null;
    }

    private boolean isTag(Update update) {
        return update.getMessage() != null &&
               update.getMessage().hasText() &&
               update.getMessage().getText().startsWith("@");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (isCommand(update))
            commands.selectCommand(update.getMessage().getText(), this,
                                   update.getMessage().getChat().getId(),
                                   update.getMessage().getFrom().getId(),
                                   update.getMessage().getFrom().getFirstName());

        else if (isJustMessage(update))
            statsCounter.processMessage(update.getMessage().getChat().getId(),
                                        update.getMessage().getFrom().getId(),
                                        update.getMessage().getFrom().getFirstName(),
                                        update.getMessage());
    }
}
