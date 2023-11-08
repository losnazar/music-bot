package com.losnazar.musicbot.model.db;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    private Long chatId;
    private String firstName;
    private String lastName;
    private String userName;
    private LocalDateTime registerAt;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Audio> audioList;
}
