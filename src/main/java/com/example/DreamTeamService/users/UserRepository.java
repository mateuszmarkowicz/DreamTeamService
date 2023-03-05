package com.example.DreamTeamService.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

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

    public List<String> getUsers() {
        return jdbcTemplate.queryForList("SELECT username FROM users", (String.class));
    }

    public UserData getUserData(String username) {
        UserData userData =  jdbcTemplate.queryForObject("SELECT username, email, description, date_of_birth FROM users WHERE username=?", BeanPropertyRowMapper.newInstance((UserData.class)), username);
        userData.setLanguages(jdbcTemplate.queryForList("SELECT l.name FROM users u INNER JOIN languages_users lu ON lu.username = u.username  INNER JOIN languages l ON lu.language_id=l.id WHERE u.username=?",String.class, username));
        userData.setSocials(jdbcTemplate.query("SELECT s.name, su.link FROM users u INNER JOIN socials_users su ON su.username = u.username  INNER JOIN socials s ON su.social_id=s.id WHERE u.username=?", BeanPropertyRowMapper.newInstance((Social.class)), username));
        if(userData.getSocials().isEmpty()) userData.setSocials(null);
        if(userData.getLanguages().isEmpty()) userData.setLanguages(null);
//        try {
//            BufferedImage bImage = ImageIO.read(new File("src/main/resources/static/usersProfilePictures/"+username+".jpg"));
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ImageIO.write(bImage, "jpg", bos);
//            byte[] data = bos.toByteArray();
//            userData.setProfilePicture(Base64.getEncoder().encodeToString(data));
//        }catch (IIOException e){
//            try {
//                BufferedImage bImage = ImageIO.read(new File("src/main/resources/static/usersProfilePictures/default.jpg"));
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                ImageIO.write(bImage, "jpg", bos);
//                byte[] data = bos.toByteArray();
//                userData.setProfilePicture(Base64.getEncoder().encodeToString(data));
//            }catch (Exception ex){
//                System.out.println(ex);
//            }
//        } catch (Exception e){
//            System.out.println(e);
//        }
        return  userData;
    }

    public List<Review> getReviews(String username){
       return jdbcTemplate.query("SELECT * FROM reviews WHERE reviewed=?", BeanPropertyRowMapper.newInstance((Review.class)), username);
    }
    public boolean addReview(Review review, String reviewer) {
        try {
            jdbcTemplate.update("INSERT INTO reviews(reviewer, reviewed, rating, comment) VALUES (?,?,?,?)", reviewer,review.getReviewed(),review.getRating(), review.getComment());
            return true;
        }catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    public boolean updateUserEmail(String username, String email){
        try {
            jdbcTemplate.update("UPDATE users SET email=? WHERE username=?",email, username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateUserDescription(String username, String description) {
        try {
            jdbcTemplate.update("UPDATE users SET description=? WHERE username=?",description, username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateUserDateOfBirth(String username, String dateOfBirth) {
        try {
            jdbcTemplate.update("UPDATE users SET date_of_birth=? WHERE username=?", dateOfBirth, username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addUserSocial(String username, Social social) {
        try {
            jdbcTemplate.update("INSERT INTO socials_users(username, social_id, link) VALUES(?,(SELECT id FROM socials WHERE name=?),?)", username, social.getName(), social.getLink());
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean addUserLanguage(String username, String language) {
        try {
            jdbcTemplate.update("INSERT INTO languages_users(username, language_id) VALUES(?,(SELECT id FROM languages WHERE name=?))", username, language);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int removeUserSocial(String username, String socialName) {
        try {
            return jdbcTemplate.update("DELETE FROM socials_users WHERE username=? AND social_id=(select id from socials WHERE name=?)", username, socialName);
        } catch (Exception e) {
            return 0;
        }
    }

    public int removeUserLanguage(String username, String language) {
        try {
            return jdbcTemplate.update("DELETE FROM languages_users WHERE username=? AND language_id=(select id from languages WHERE name=?)", username,language);
        } catch (Exception e) {
            return 0;
        }
    }

    public int removeReview(String username, String reviewer) {
        try {
            return jdbcTemplate.update("DELETE FROM reviews WHERE reviewer=? AND  reviewed=?", reviewer, username);
        } catch (Exception e) {
            return 0;
        }
    }
}
