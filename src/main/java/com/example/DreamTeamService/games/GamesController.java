package com.example.DreamTeamService.games;

import com.example.DreamTeamService.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/games")
public class GamesController {

    @Autowired
    GamesRepository gamesRepository;

    @GetMapping("")
    List<Game> getGames() {
        return gamesRepository.getGames();
    }

    @GetMapping("/{username}")
    public List<GamesForUser> getUserGames(@PathVariable("username") String username){
        return gamesRepository.getUserGames(username);
    }

    @PostMapping("/{username}")
    public boolean addUserGame(@PathVariable("username") String username, @RequestBody GamesForUser gamesForUser, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(username.equals(tokenUsername)) {
            boolean noConflict = gamesRepository.addUserGames(username, gamesForUser);
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

    @DeleteMapping("/{username}")
    public boolean removeUserGame(@PathVariable("username") String username, @RequestBody String game, HttpServletResponse response){
        Object tokenUsername = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if(username.equals(tokenUsername)) {
            int isRemoved = gamesRepository.removeUserGame(username, game);
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
}
