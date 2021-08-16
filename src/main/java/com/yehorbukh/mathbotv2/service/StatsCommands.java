package com.yehorbukh.mathbotv2.service;

import com.yehorbukh.mathbotv2.bot_controller.Bot;
import com.yehorbukh.mathbotv2.entity.BotUser;
import com.yehorbukh.mathbotv2.entity.UserStats;
import com.yehorbukh.mathbotv2.repo.StatsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Service
public class StatsCommands {
    private final StatsRepo statsRepo;

    @Autowired
    public StatsCommands(StatsRepo statsRepo) {
        this.statsRepo = statsRepo;
    }

    private void executeCommand(Bot bot, long chatId, String textResponse) {
        SendMessage message = new SendMessage(String.valueOf(chatId), textResponse);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void wordStats(Bot bot, long chatId) {
        List<Object[]> wordsTop = statsRepo.wordsTop(chatId);
        String stats = StatsMessage.createStatsMessage("Рейтинг по словам", "words", wordsTop);
        executeCommand(bot, chatId, stats);
    }

    public void stickerStats(Bot bot, long chatId) {
        List<Object[]> stickersTop = statsRepo.stickerTop(chatId);
        String stats = StatsMessage.createStatsMessage("Рейтинг по стикерам", "stickers", stickersTop);
        executeCommand(bot, chatId, stats);
    }

    public void swearWordStats(Bot bot, long chatId) {
        List<Object[]> swearWordsTop = statsRepo.swearWordsTop(chatId);
        String stats = StatsMessage.createStatsMessage("Рейтинг самых матюкливых", "swear words", swearWordsTop);
        executeCommand(bot, chatId, stats);
    }

    public void gifStats(Bot bot, long chatId) {
        List<Object[]> gifsTop = statsRepo.gifsTop(chatId);
        String stats = StatsMessage.createStatsMessage("Рейтинг по гифкам", "gifs", gifsTop);
        executeCommand(bot, chatId, stats);
    }

    public void videoNoteStats(Bot bot, long chatId) {
        List<Object[]> videoNotesTop = statsRepo.videoNotesTop(chatId);
        String stats = StatsMessage.createStatsMessage("Рейтинг по видеообращениям", "video notes", videoNotesTop);
        executeCommand(bot, chatId, stats);

    }

    public void voiceStats(Bot bot, long chatId) {
        List<Object[]> voicesTop = statsRepo.voicesTop(chatId);
        String stats = StatsMessage.createStatsMessage("Рейтинг по голосовым", "voices", voicesTop);
        executeCommand(bot, chatId, stats);
    }

    public void myStats(Bot bot, long chatId, long userId, String userName) {
        var user = new BotUser(userId, userName);
        var statsOptional = statsRepo.findUserStatsByChatIdAndUser(chatId, user);
        UserStats userStats;

        if (statsOptional.isPresent())
            userStats = statsOptional.get();
        else {
            userStats = new UserStats();
            userStats.setUser(user);
        }
        String statsResponse = StatsMessage.createPersonalStats(userStats);
        executeCommand(bot, chatId, statsResponse);
    }

    private static class StatsMessage {
        private static final String[] statsEmoji = {"\uD83E\uDD47", "\uD83E\uDD48", "\uD83E\uDD49"};

        public static String createStatsMessage(String scoreTitle, String scoreSuffix, List<Object[]> statsList) {
            var sb = new StringBuilder("\uD83D\uDCC8 " + scoreTitle + "\n\n");

            for (int i = 1; i <= statsList.size(); i++) {
                if (i <= 3)
                    sb.append(statsEmoji[i - 1]).append(' ');
                else
                    sb.append("       ");
                sb.append(i)
                        .append(')')
                        .append(' ')
                        .append(statsList.get(i - 1)[0].toString())
                        .append(" - ")
                        .append((int) statsList.get(i - 1)[1])
                        .append(' ')
                        .append(scoreSuffix)
                        .append('\n');
            }
            return sb.toString();
        }

        public static String createPersonalStats(UserStats userStats) {
            return "\uD83D\uDCC8 Статистика " + userStats.getUser().getName() +
                    ":\n\n✍️ Слова: " +
                    userStats.getWords() +
                    "\n\uD83E\uDD25 Маты: " +
                    userStats.getSwearWords() +
                    "\n\uD83D\uDDBC Стикеры: " +
                    userStats.getStickers() +
                    "\n\uD83D\uDE49 Гифки: " +
                    userStats.getGifs() +
                    "\n\uD83C\uDFA4 Голосовые: " +
                    userStats.getVoices() +
                    "\n\uD83D\uDCF7 Видеообращения: " +
                    userStats.getVideoNotes();
        }
    }
}
