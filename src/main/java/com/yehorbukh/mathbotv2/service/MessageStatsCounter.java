package com.yehorbukh.mathbotv2.service;

import com.yehorbukh.mathbotv2.entity.BotUser;
import com.yehorbukh.mathbotv2.entity.UserStats;
import com.yehorbukh.mathbotv2.repo.StatsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MessageStatsCounter {

    private final StatsRepo statsRepo;

    @Autowired
    public MessageStatsCounter(StatsRepo statsRepo) {
        this.statsRepo = statsRepo;
    }

    private enum StatsType {
        WORD,
        STICKER,
        VOICE,
        SWEAR_WORD,
        VIDEO_NOTE,
        GIF
    }

    private boolean checkIsSpam(String[] words) {
        if (words.length < 100) {
            var set = Arrays.stream(words).
                    map(String::toLowerCase).
                    collect(Collectors.toSet());

            double uniqueness = calculateUniqueWordsCoefficient(words.length, set.size());
            return uniqueness < 0.6;
        }
        return true;
    }

    private double calculateUniqueWordsCoefficient(int allWords, int uniqueWords) {
        return (double) uniqueWords / allWords;
    }

    public void processMessage(long chatId, long userId, String userName, Message message) {
        var stats = getOrCreateUserStats(chatId, userId, userName);

        if (message.hasText()) {
            String[] words = message.getText().split("\\PL+");

            if (!checkIsSpam(words)) {
                // increase number of words in the stats table
                updateUserStats(stats, StatsType.WORD, words.length);

                // check presence of swear words
                int numOfSwearWords = SwearWordsDetector.getNumberOfSwearWords(words);

                if (numOfSwearWords != 0)
                    updateUserStats(stats, StatsType.SWEAR_WORD, numOfSwearWords);
            }
        }

        else if (message.hasSticker())
            updateUserStats(stats, StatsType.STICKER, 1);

        else if (message.hasVideoNote())
            updateUserStats(stats, StatsType.VIDEO_NOTE, 1);

        else if (message.hasVoice())
            updateUserStats(stats, StatsType.VOICE, 1);

        else if (message.hasAnimation())
            updateUserStats(stats, StatsType.GIF, 1);

        statsRepo.save(stats);
    }

    private UserStats getOrCreateUserStats(long chatId, long userId, String userName) {
        var botUser = new BotUser(userId, userName);
        Optional<UserStats> statsOptional = statsRepo.findUserStatsByChatIdAndUser(chatId, botUser);

        UserStats stats;

        if (statsOptional.isEmpty()) {
            var userStats = new UserStats();
            userStats.setChatId(chatId);
            userStats.setUser(botUser);
            stats = userStats;
        } else
            stats = statsOptional.get();

        return stats;
    }

    private void updateUserStats(UserStats stats, StatsType statsTypeUpdate, int increaseBy) {
        switch (statsTypeUpdate) {
            case WORD:
                stats.setWords(stats.getWords() + increaseBy);
                break;
            case STICKER:
                stats.setStickers(stats.getStickers() + increaseBy);
                break;
            case SWEAR_WORD:
                stats.setSwearWords(stats.getSwearWords() + increaseBy);
                break;
            case GIF:
                stats.setGifs(stats.getGifs() + increaseBy);
                break;
            case VOICE:
                stats.setVoices(stats.getVoices() + increaseBy);
                break;
            case VIDEO_NOTE:
                stats.setVideoNotes(stats.getVideoNotes() + increaseBy);
                break;
        }
    }

    static class SwearWordsDetector {
        // Х*й
        private static final Pattern pattern1 = Pattern.compile("^([Рр][ОоOo][Зз]|[ВвB]|[Зз][Аа]|[ОоOo][ТтT]|[ДдПп][ОоOo]|[Нн][АаЕеЄє])?([XxХх])([YyУу])(([Лл][Ии])$|([ЙйЕеЁёИиІіIiЇїЯяЮюЭэЄє]))([Вв][ОоOo]|[Нн][Яя]|[Чч]|[Рр])?[аА-яЯ]{0,4}([СсCc]([Яя]|[Ьь]))?$");
        // Бл* / Бл*дь
        private static final Pattern pattern2 = Pattern.compile("^([Дд][Оо]|[Зз][Аа]|[Нн][Аа]|[Нн][ЕеЄє][Дд][Оо]|[Пп][ЕеЄє][Рр][ЕеЄє]|[Пп][Оо]|[Вв][ЫыИи])?(([Бб][Лл][Яя])|[Бб][Лл][Яя][Дд])([Сс][Кк]|[Сс][Тт][Вв]|[Ии][Нн]|[Кк]|[Хх]|[Оо][Вв][Аа][Лл])?([Аа][Яя]|[Оо][ЕеЄє]|[Аа]|[ЫыИи]|[ИиІі]|[Ьь]|[Оо])?([аА-яЯ]{0,2})$");
        // П*изд
        private static final Pattern pattern3 = Pattern.compile("^([Дд][Оо]|[Зз][Аа]|[Нн][Аа]|[Нн][ЕеЄє][Дд][Оо]|[Пп][ЕеЄє][Рр][ЕеЄє]|[Пп][Оо]|[Вв][ЫыИи]|[Вв]|[Сс])?([Пп][Ии][Зз][Дд])([аА-яЯ]|[ЁёІіЇї]){0,8}([Аа]|[Яя]|[Уу]|[Юю]|[Ьь]|[Йй]|[ЫыИи]|[ИиІіЇї]|[ЕеЄє]|([Тт]|[Ьь])|([Сс]|[ЯяЬь])){0,2}$");
        // Еб*ть
        private static final Pattern pattern4 = Pattern.compile("^([ВвУу]|[Рр][Аа][Зз]|[Пп][Оо][Дд]|[Дд][Оо]|[Зз][Аа]|[Нн][Аа]|[Нн][ЕеЄє][Дд][Оо]|[Пп][ЕеЄє][Рр][ЕеЄє]|[Пп][Оо]|[Вв][ЫыИи]|[Вв]|[Сс])?([ЕеЄє][Бб]([Аа]|[Тт][Ьь]|[ЕеЄє]|[Уу]|[ИиІі]))([аА-яЯ]|[ЁёІіЇї]){0,8}([Аа]|[Яя]|[Уу]|[Юю]|[Ьь]|[Йй]|[ЫыИи]|[ИиІіЇї]|[ЕеЄє]|([Тт]|[Ьь])|([Сс]|[ЯяЬь])){0,2}$");
        // Сук*
        private static final Pattern pattern5 = Pattern.compile("^[Сс][Уу][Кк]([АаИиІіОо])*$");
        // Пид*р
        private static final Pattern pattern6 = Pattern.compile("^([Пп][ИиІі][Дд][Оо][Рр])([ЫыИиІіАаУуЕеЄє]|[Аа][СсМм]|[ИиІі][Нн]|[Щщ][ЕеЄє]){0,3}$");
        // Г*ндон
        private static final Pattern pattern7 = Pattern.compile("^([Гг][Аа][Нн][Дд][Оо][Нн])([ЫыИиІіАаУуЕеЄє]|[Аа][СсМм]|[ИиІі][Нн]|[Щщ][ЕеЄє]){0,3}$");

        private static final List<Pattern> swearWordPatterns =
                List.of(pattern1, pattern2, pattern3, pattern4, pattern5, pattern6, pattern7);

        public static int getNumberOfSwearWords(String[] words) {
            int numOfSwear = 0;

            for (String word : words) {
                for (Pattern pattern : swearWordPatterns) {
                    if (pattern.matcher(word).matches()) {
                        numOfSwear++;
                        break;
                    }
                }
            }
            return numOfSwear;
        }
    }
}
