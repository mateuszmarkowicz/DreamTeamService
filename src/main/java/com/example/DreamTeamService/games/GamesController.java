package com.example.DreamTeamService.games;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
