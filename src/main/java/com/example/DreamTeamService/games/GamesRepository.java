package com.example.DreamTeamService.games;

import com.example.DreamTeamService.users.User;
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

    public List<GamesForUser> getUserGames(String username) {
        return jdbcTemplate.query("SELECT g.id, g.name, gu.is_favourite_game, s.name as server FROM users u INNER JOIN games_users gu ON gu.username = u.username INNER JOIN servers s ON s.id = gu.server_id INNER JOIN games g ON g.id = gu.game_id WHERE u.username=?", BeanPropertyRowMapper.newInstance((GamesForUser.class)), username);
    }
}
