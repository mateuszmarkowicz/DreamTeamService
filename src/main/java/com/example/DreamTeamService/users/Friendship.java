package com.example.DreamTeamService.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    private int userRequest;
    private int userAccept;
    private String status;

}