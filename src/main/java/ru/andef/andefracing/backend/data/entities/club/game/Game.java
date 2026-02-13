package ru.andef.andefracing.backend.data.entities.club.game;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Игра
 */
@Entity
@Table(name = "game", schema = "games")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "photo_url", unique = true, nullable = false)
    private String photoUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}