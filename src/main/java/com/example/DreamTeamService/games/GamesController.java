package com.example.DreamTeamService.games;

import com.example.DreamTeamService.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
