package com.example.DreamTeamService.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean validateLogData(User user){
            return true;
    }

    public boolean register(User user) throws DataIntegrityViolationException{
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            int insertUser =  jdbcTemplate.update("INSERT INTO users(username, password, enabled, email) VALUES(?,?,?,?)", user.getUsername(), user.getPassword(), 1, user.getEmail());
            int insertAuth = jdbcTemplate.update("INSERT INTO authorities(username, authority) VALUES(?,?)", user.getUsername(),"ROLE_USER");


        }catch (Exception e){
            return false;
        }
        return true;
    }

    public UserData getUserData(String username) {
        UserData userData =  jdbcTemplate.queryForObject("SELECT username, email, description, date_of_birth FROM users WHERE username=?", BeanPropertyRowMapper.newInstance((UserData.class)), username);
        userData.setLanguages(jdbcTemplate.queryForList("SELECT l.name FROM users u INNER JOIN languages_users lu ON lu.username = u.username  INNER JOIN languages l ON lu.language_id=l.id WHERE u.username=?",String.class, username));
        userData.setSocials(jdbcTemplate.query("SELECT s.name, su.link FROM users u INNER JOIN socials_users su ON su.username = u.username  INNER JOIN socials s ON su.social_id=s.id WHERE u.username=?", BeanPropertyRowMapper.newInstance((Socials.class)), username));
        if(userData.getSocials().isEmpty()) userData.setSocials(null);
        if(userData.getLanguages().isEmpty()) userData.setLanguages(null);
        return  userData;
    }
}
