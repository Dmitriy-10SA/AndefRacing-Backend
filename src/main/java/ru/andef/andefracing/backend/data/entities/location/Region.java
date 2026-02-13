package ru.andef.andefracing.backend.data.entities.location;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Регион
 */
@Entity
@Table(name = "region", schema = "location")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy(value = "name ASC")
    private List<City> cities = new ArrayList<>();
}