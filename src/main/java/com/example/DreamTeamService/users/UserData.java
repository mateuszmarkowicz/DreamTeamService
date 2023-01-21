package com.example.DreamTeamService.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private String username;
    private String email;
    public String description;
    public String dateOfBirth;
    private List<String> languages;
    private List<Social> socials;

}
