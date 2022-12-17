package com.example.DreamTeamService.users;

import com.example.DreamTeamService.jwt.JwtConfig;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin
public class UserController {

    UserRepository userRepository;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    @Autowired
    public UserController(SecretKey secretKey, JwtConfig jwtConfig) {
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
