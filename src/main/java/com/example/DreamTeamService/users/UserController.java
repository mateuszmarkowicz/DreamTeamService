package com.example.DreamTeamService.users;

import com.example.DreamTeamService.jwt.JwtConfig;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin
public class UserController {

    UserRepository userRepository;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    @Autowired
    public UserController(SecretKey secretKey, JwtConfig jwtConfig, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/register")
    public boolean register(@RequestBody User user, HttpServletResponse response){
        boolean isRegisterDone = userRepository.register(user);
        if(isRegisterDone){
            HashMap<String, String> auth = new HashMap<String, String>();
             auth.put("authority", "ROLE_USER");
            List<HashMap<String, String>> authList = new ArrayList<HashMap<String , String >>();
            authList.add(auth);

            String token = Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("authorities", authList)
                    .setIssuedAt(new Date())
                    .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                    .signWith(secretKey)
                    .compact();

            response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() +token);
            return true;
        }
        else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return false;
        }
    }

    @GetMapping("/users/{username}")
    public UserData getUserData(@PathVariable("username") String username){
        return userRepository.getUserData(username);
    }

    @GetMapping("/users/reviews/{username}")
    public List<Review> getReviews(@PathVariable("username") String username){
        return userRepository.getReviews(username);
    }
    @PatchMapping("/users/emails/{username}")
    public boolean updateUserEmail(@PathVariable("username") String username, @RequestBody String email, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(username.equals(tokenUsername)) {
            return userRepository.updateUserEmail(username,email);
        }else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }

    @PatchMapping("/users/descriptions/{username}")
    public boolean updateUserDescription(@PathVariable("username") String username, @RequestBody String description, HttpServletResponse response){
            Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            if(username.equals(tokenUsername)) {
                return userRepository.updateUserDescription(username,description);
            }else{
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
    }

    @PatchMapping("/users/birthDates/{username}")
    public boolean updateUserDateOfBirth(@PathVariable("username") String username, @RequestBody String dateOfBirth, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(username.equals(tokenUsername)) {
            return userRepository.updateUserDateOfBirth(username,dateOfBirth);
        }else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }
    @PostMapping("/users/reviews")
    public boolean addReview(@RequestBody Review review, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String reviewer = tokenUsername.toString();
        if(review.getReviewed().equals(reviewer)){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return false;
        }
        boolean isReviewAdded =  userRepository.addReview(review, reviewer);
        if(isReviewAdded) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            return true;
        }
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        return false;
    }

    @PostMapping("/users/socials/{username}")
    public boolean addUserSocial(@PathVariable("username") String username, @RequestBody Social social, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(username.equals(tokenUsername)) {
            boolean noConflict = userRepository.addUserSocial(username, social);
            if(noConflict) return noConflict;
            else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return noConflict;
            }
        }else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }

    @PostMapping("/users/languages/{username}")
    public boolean addUserLanguage(@PathVariable("username") String username, @RequestBody String language, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(username.equals(tokenUsername)) {
            boolean noConflict = userRepository.addUserLanguage(username, language);
            if(noConflict) return noConflict;
            else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return noConflict;
            }
        }else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }

    @PutMapping("/users/profilePictures/{username}")
    public boolean addUserProfilePicture(@PathVariable("username") String username, @RequestBody MultipartFile file, HttpServletResponse response){
        try {
            Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            if(username.equals(tokenUsername)) {
                BufferedImage bi = ImageIO.read(file.getInputStream());
                File outPutFile = new File("src/main/resources/static/usersProfilePictures/" + username + ".jpg");
                ImageIO.write(bi, "jpg", outPutFile);
            }else{
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    @DeleteMapping("/users/socials/{username}")
    public boolean removeUserSocial(@PathVariable("username") String username, @RequestBody String socialName, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(username.equals(tokenUsername)) {
            int isRemoved = userRepository.removeUserSocial(username, socialName);
            if(isRemoved==1) return true;
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return false;
            }
        }else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }

    @DeleteMapping("/users/languages/{username}")
    public boolean removeUserLanguage(@PathVariable("username") String username, @RequestBody String language, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(username.equals(tokenUsername)) {
            int isRemoved = userRepository.removeUserLanguage(username, language);
            if(isRemoved==1) return true;
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return false;
            }
        }else{
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
    }

    @DeleteMapping("/users/profilePictures/{username}")
    public boolean removeUserProfilePicture(@PathVariable("username") String username, HttpServletResponse response){
            Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            if(username.equals(tokenUsername)) {
                File fileToDelete = new File("src/main/resources/static/usersProfilePictures/" + username + ".jpg");
                if(fileToDelete.delete()) return true;
                else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return false;
                }
            }else{
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }

    }
    @DeleteMapping("users/reviews/{username}")
    public boolean removeReview(@PathVariable String username, HttpServletResponse response)
    {
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String reviewer = tokenUsername.toString();
        if(username.equals(reviewer)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
            int isRemoved = userRepository.removeReview(username, reviewer);
            if(isRemoved==1) return true;
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return false;
            }
    }

    @GetMapping("test")
    public String userTest() {
        return "Test";
    }

    @GetMapping("test2")
    public String userTest2() {
        //WAZNE - pobieranie nazwy uzytkownika
        Object user2 = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        System.out.println(user2);
        return "Test2";
    }

}
