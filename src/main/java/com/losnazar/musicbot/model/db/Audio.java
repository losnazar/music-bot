package com.losnazar.musicbot.model.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "audios")
public class Audio {
    @Id
    @GeneratedValue(generator = "audios_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "audios_id_seq",
            sequenceName = "audios_id_seq",
            allocationSize = 1)
    private Long id;
    private String artist;
    private String title;
    private int duration;
    private String path;
    private String url;

    @Override
    public String toString() {
        return artist + " - " + title;
    }
}
