package com.example.DreamTeamService.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    private String userRequest;
    private String userAccept;
    private String status;

}