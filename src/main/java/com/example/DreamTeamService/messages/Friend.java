package com.example.DreamTeamService.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    //klasa odzwierciedlajaca dane znajomego
    //czyli uzytkownika z ktorym mamy chociaz jedna wiadomosc
    private String friend;
    private int is_online;
    private int is_all_read;
}
