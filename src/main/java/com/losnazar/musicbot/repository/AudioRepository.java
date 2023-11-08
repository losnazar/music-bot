package com.losnazar.musicbot.repository;

import com.losnazar.musicbot.model.db.Audio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {
    List<Audio> findAllByArtist(String artist);
}
