package com.example.DreamTeamService.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    //funkcja zwracajaca liste wiadomosci przeslanych pomiedzy dwoma uzytkownikami
    public List<Message> getMessages(String username, String username2) {
        return jdbcTemplate.query("SELECT id, sender, receiver, post_date, content from messages WHERE " +
                "(sender LIKE ? AND receiver LIKE ?) OR (receiver LIKE ? AND sender LIKE ?) ORDER BY post_date ASC", BeanPropertyRowMapper.newInstance(Message.class), username, username2, username, username2);
    }
    //funkcja dodajaca wiadomosc do bazy
    public boolean sendMessage(Message message) {
        try {
            int insertUser =  jdbcTemplate.update("INSERT INTO messages(sender, receiver, content) VALUES(?,?,?)", message.getSender(), message.getReceiver(), message.getContent());
        }catch (Exception e){
            return false;
        }
        return true;
    }
    //funckja zwracajaca znajomych uzytkownikow
    public List<Friend> getFriends(String username) {
        //pobranie nazwy oraz statusu znajowmych z bazy
        List<Friend> friends =  jdbcTemplate.query("SELECT DISTINCT m.receiver as friend, u.is_online FROM messages m INNER JOIN users u ON u.username = m.receiver WHERE m.sender=?" +
                "UNION select distinct m.sender as friend, u.is_online from messages m INNER JOIN users u ON u.username = m.sender WHERE m.receiver=?;", BeanPropertyRowMapper.newInstance(Friend.class), username, username);
        for (Friend f: friends) {
            try {
                //dla kazdego znajomego pobieramy informacje czy wszystkie wiadomosci od niego sa przeczytane
                f.setIs_all_read(jdbcTemplate.queryForObject("SELECT is_read from messages where sender=? and receiver=? order by post_date DESC LIMIT 1;", Integer.class, f.getFriend(), username));
            }catch (Exception e){
                f.setIs_all_read(1);
            }
        }
        return friends;
    }

    //funkcja zmienajaca status nieprzeczytanych wiadomosci z danym uzytkownikem na przeczytane
    public boolean changeStatus(String receiverUsername, String senderUsername) {
        try {
            jdbcTemplate.update("UPDATE messages SET is_read=1 WHERE sender=? AND receiver=? AND is_read=0", senderUsername, receiverUsername);
        } catch (Exception e) {
            return false;
        }
        return true;

    }
}