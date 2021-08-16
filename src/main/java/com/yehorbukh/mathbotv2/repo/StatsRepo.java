package com.yehorbukh.mathbotv2.repo;

import com.yehorbukh.mathbotv2.entity.BotUser;
import com.yehorbukh.mathbotv2.entity.UserStats;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StatsRepo extends CrudRepository<UserStats, Integer> {
    @Query(value = "SELECT name, words FROM stats, user WHERE stats.user_id = user.user_id ORDER BY words desc",
           nativeQuery = true)
    List<Object[]> wordsTop();

    @Query(value = "SELECT name, stickers FROM stats, user WHERE stats.user_id = user.user_id ORDER BY stickers desc",
            nativeQuery = true)
    List<Object[]> stickerTop();

    @Query(value = "SELECT name, gifs FROM stats, user WHERE stats.user_id = user.user_id ORDER BY gifs desc",
            nativeQuery = true)
    List<Object[]> gifsTop();

    @Query(value = "SELECT name, swear_words FROM stats, user WHERE stats.user_id = user.user_id ORDER BY swear_words desc",
            nativeQuery = true)
    List<Object[]> swearWordsTop();

    @Query(value = "SELECT name, video_notes FROM stats, user WHERE stats.user_id = user.user_id ORDER BY video_notes desc",
            nativeQuery = true)
    List<Object[]> videoNotesTop();

    @Query(value = "SELECT name, voices FROM stats, user WHERE stats.user_id = user.user_id ORDER BY voices desc",
            nativeQuery = true)
    List<Object[]> voicesTop();

    Optional<UserStats> findUserStatsByChatIdAndUser(long chatId, BotUser user);
}
