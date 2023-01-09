package com.example.DreamTeamService.games;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamesForUser {
    private int id;
    private String name;
    private String server;
    private boolean isFavouriteGame;

}

