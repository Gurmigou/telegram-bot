package com.yehorbukh.mathbotv2.service;

import com.yehorbukh.mathbotv2.bot_controller.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandSelectorService {
    private final StatsCommands statsCommands;

    @Autowired
    public CommandSelectorService(StatsCommands statsCommands) {
        this.statsCommands = statsCommands;
    }

    public void selectCommand(String command, Bot bot, long chatId, long userId, String userName) {
        switch (command) {
            case "/wordstats@mathematicianUnivBot":
                statsCommands.wordStats(bot, chatId);
                break;
            case "/stickerstats@mathematicianUnivBot":
                statsCommands.stickerStats(bot, chatId);
                break;
            case "/swearingstats@mathematicianUnivBot":
                statsCommands.swearWordStats(bot, chatId);
                break;
            case "/voicestats@mathematicianUnivBot":
                statsCommands.voiceStats(bot, chatId);
                break;
            case "/videostats@mathematicianUnivBot":
                statsCommands.videoNoteStats(bot, chatId);
                break;
            case "/gifstats@mathematicianUnivBot":
                statsCommands.gifStats(bot, chatId);
                break;
            case "/mystats@mathematicianUnivBot":
                statsCommands.myStats(bot, chatId, userId, userName);
                break;
        }
    }
}
