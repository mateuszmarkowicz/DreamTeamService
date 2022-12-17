package com.example.DreamTeamService.games;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GamesRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public List<Game> getGames() {
        return jdbcTemplate.query("SELECT id, name from games", BeanPropertyRowMapper.newInstance(Game.class));
    }
}
