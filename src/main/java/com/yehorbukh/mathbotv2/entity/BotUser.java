package com.yehorbukh.mathbotv2.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@NotNull
public class BotUser {
    @Id
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    private String telegramTag;

    @OneToMany(mappedBy = "user")
    private List<UserStats> statsList;

    public BotUser(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }
}