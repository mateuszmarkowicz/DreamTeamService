package com.example.DreamTeamService.users;

import com.example.DreamTeamService.games.GamesForUser;
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

    public boolean validateLogData(User user) {
        return true;
    }

    public boolean register(User user) throws DataIntegrityViolationException {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            int insertUser = jdbcTemplate.update("INSERT INTO users(username, password, enabled, email) VALUES(?,?,?,?)", user.getUsername(), user.getPassword(), 1, user.getEmail());
            int insertAuth = jdbcTemplate.update("INSERT INTO authorities(username, authority) VALUES(?,?)", user.getUsername(), "ROLE_USER");


        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<String> getUsers() {
        return jdbcTemplate.queryForList("SELECT username FROM users", (String.class));
    }

    public UserData getUserData(String username) {
        UserData userData = jdbcTemplate.queryForObject("SELECT username, email, description, date_of_birth FROM users WHERE username=?", BeanPropertyRowMapper.newInstance((UserData.class)), username);
        userData.setLanguages(jdbcTemplate.queryForList("SELECT l.name FROM users u INNER JOIN languages_users lu ON lu.username = u.username  INNER JOIN languages l ON lu.language_id=l.id WHERE u.username=?", String.class, username));
        userData.setSocials(jdbcTemplate.query("SELECT s.name, su.link FROM users u INNER JOIN socials_users su ON su.username = u.username  INNER JOIN socials s ON su.social_id=s.id WHERE u.username=?", BeanPropertyRowMapper.newInstance((Social.class)), username));
        if (userData.getSocials().isEmpty()) userData.setSocials(null);
        if (userData.getLanguages().isEmpty()) userData.setLanguages(null);
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
        return userData;
    }

    public List<Review> getReviews(String username) {
        List<Review> reviews =  jdbcTemplate.query("SELECT * FROM reviews WHERE reviewed=?", BeanPropertyRowMapper.newInstance((Review.class)), username);
        for(Review review: reviews){
            review.setAttributes(jdbcTemplate.queryForList("SELECT a.name FROM attributes a INNER JOIN reviews_attributes ra ON a.id=ra.attribute_id INNER JOIN reviews r ON ra.review_id=r.id WHERE r.reviewed=?", String.class,username));
        }
        return reviews;
    }

    public boolean addReview(Review review, String reviewer) {
        try {
            jdbcTemplate.update("INSERT INTO reviews(reviewer, reviewed, rating, comment) VALUES (?,?,?,?)", reviewer, review.getReviewed(), review.getRating(), review.getComment());
            if(review.getAttributes()!=null&& !review.getAttributes().isEmpty()){
                List<String> attributes = review.getAttributes();
                for(String attrib: attributes)
                jdbcTemplate.update("INSERT INTO reviews_attributes(review_id, attribute_id) values ((select id from reviews where reviewer=? AND reviewed=?)," +
                        "(select id from attributes where name=?))",reviewer, review.getReviewed(), attrib);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean updateUserEmail(String username, String email) {
        try {
            jdbcTemplate.update("UPDATE users SET email=? WHERE username=?", email, username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateUserDescription(String username, String description) {
        try {
            jdbcTemplate.update("UPDATE users SET description=? WHERE username=?", description, username);
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
            return jdbcTemplate.update("DELETE FROM languages_users WHERE username=? AND language_id=(select id from languages WHERE name=?)", username, language);
        } catch (Exception e) {
            return 0;
        }
    }

    public int removeReview(String username, String reviewer) {
        try {
            List<Integer> attr_ids = jdbcTemplate.queryForList("SELECT a.id from attributes a INNER JOIN reviews_attributes ra ON a.id=ra.attribute_id INNER JOIN reviews r ON ra.review_id=r.id WHERE r.reviewer=? ", Integer.class, reviewer);
            for(Integer id: attr_ids)jdbcTemplate.update("DELETE FROM reviews_attributes WHERE review_id IN (select id from reviews WHERE reviewer=?) AND attribute_id=?", reviewer, id);
            jdbcTemplate.update("DELETE FROM reviews WHERE reviewer=? AND  reviewed=?", reviewer, username);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean inviteFriend(String userRequest, String userAccept) {
        try {

            jdbcTemplate.update("INSERT INTO friendships(user_request, user_accept, status) VALUES(?,?,'0')", userRequest, userAccept);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateFriendship(String userRequest, String userAccept, String status) {
        try {
            Friendship currentFriendship = jdbcTemplate.queryForObject(
                   "SELECT user_request, user_accept, status FROM friendships WHERE user_request LIKE ? AND user_accept LIKE ? OR user_accept LIKE ? AND user_request LIKE ?",
                    BeanPropertyRowMapper.newInstance((Friendship.class)), userRequest, userAccept, userRequest, userAccept);
            if(currentFriendship.getStatus().equals("2") && !currentFriendship.getUserRequest().equals(userRequest)) return false;
            if(currentFriendship.getStatus().equals("0") && currentFriendship.getUserRequest().equals(userRequest)) return false;
            if(status.equals("2"))jdbcTemplate.update("UPDATE friendships SET user_request=?, user_accept=?, status=? WHERE user_request LIKE ? AND user_accept LIKE ? OR user_accept LIKE ? AND user_request LIKE ?",
                    userRequest, userAccept, status, userRequest, userAccept, userRequest, userAccept);
            else jdbcTemplate.update("UPDATE friendships SET status=? WHERE user_request LIKE ? AND user_accept LIKE ? OR user_accept LIKE ? AND user_request LIKE ?",
                    status, userRequest, userAccept, userRequest, userAccept);
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public List<String> getFriendList(String username, String status) {
        if(status.equals("0")){
            List<String> friendsList = jdbcTemplate.queryForList("SELECT user_request FROM friendships WHERE user_accept LIKE ? AND status LIKE ?", String.class, username, status);
            return friendsList;
        }else if(status.equals("1")){
            List<String> friendsList = jdbcTemplate.queryForList("SELECT user_request FROM friendships WHERE user_accept LIKE ? AND status LIKE ? UNION SELECT user_accept FROM friendships WHERE user_request LIKE ? AND status LIKE ?", String.class, username, status, username, status);
            return friendsList;
        }else if(status.equals("2")){
            List<String> friendsList = jdbcTemplate.queryForList("SELECT user_accept FROM friendships WHERE user_request LIKE ? AND status LIKE ?", String.class, username, status);
            return friendsList;
        }
        return null;
    }
}
