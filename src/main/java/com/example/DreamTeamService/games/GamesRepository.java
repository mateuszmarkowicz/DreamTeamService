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

    public List<String> getGameUsers(int id) {
        List<String> list = jdbcTemplate.queryForList("SELECT u.username FROM users u INNER JOIN games_users gu ON u.username=gu.username INNER JOIN games g ON gu.game_id=g.id WHERE g.id=?", String.class, id);
        if(list.isEmpty())list=null;
        return list;
    }

    public boolean addUserGames(String username, GamesForUser gamesForUser) {
        try {
            jdbcTemplate.update("INSERT INTO games_users(username, game_id, is_favourite_game, server_id) VALUES(?,(SELECT id FROM games WHERE name=? ), ?, (SELECT id from servers WHERE name=? AND game_id=(SELECT id FROM games WHERE name=? )))", username, gamesForUser.getName(), gamesForUser.isFavouriteGame(), gamesForUser.getServer(), gamesForUser.getName());
            return true;
        }catch (Exception e){
            return false;
        }
    }


    public int removeUserGame(String username, String game) {
        try {
            return jdbcTemplate.update("DELETE FROM games_users WHERE username=? AND game_id=(select id from games WHERE name=?)", username,game);
        } catch (Exception e) {
            return 0;
        }
    }

}
