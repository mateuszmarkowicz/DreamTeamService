package com.example.DreamTeamService.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private int id;
    private String reviewer;
    private String reviewed;
    private float rating;
    private String comment;
    private LocalDateTime publicationDate;

    private List<String> attributes;
}
