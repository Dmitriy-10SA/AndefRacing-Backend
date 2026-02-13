package ru.andef.andefracing.backend.data.entities.club.photo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Фотография в клубе
 */
@Entity
@Table(name = "photo", schema = "info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "sequence_number", nullable = false)
    @Setter
    private short sequenceNumber;

    public Photo(String url, short sequenceNumber) {
        this.url = url;
        this.sequenceNumber = sequenceNumber;
    }
}