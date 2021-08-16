package com.yehorbukh.mathbotv2.repo;

import com.yehorbukh.mathbotv2.entity.BotUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<BotUser, Integer> {
}
