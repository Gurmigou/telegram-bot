package com.yehorbukh.mathbotv2.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "stats")
@Getter
@Setter
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Long chatId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private BotUser user;

    @Column(nullable = false, columnDefinition = "int default '0'")
    private int words;

    @Column(nullable = false, columnDefinition = "int default '0'")
    private int stickers;

    @Column(nullable = false, columnDefinition = "int default '0'")
    private int voices;

    @Column(nullable = false, columnDefinition = "int default '0'")
    private int videoNotes;

    @Column(nullable = false, columnDefinition = "int default '0'")
    private int gifs;

    @Column(nullable = false, columnDefinition = "int default '0'")
    private int swearWords;
}
