package com.example.ChatDatabaseApp.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("")
    public boolean register(@RequestBody User user, HttpServletResponse response){
        boolean isRegisterDone = userRepository.register(user);
        if(isRegisterDone){
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

//    @RequestMapping(value = "/name", method = RequestMethod.GET)
//    public String getName(Authentication authentication, Principal principal) {
//        System.out.println(authentication.getName());
//        System.out.println("-----------------");
//        System.out.println(principal.getName());
//        return "";
//    }
}
