package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.club.Game;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Short> {
    @Query(
            nativeQuery = true,
            value = """
                    SELECT * FROM games.game g
                    JOIN info.game_club gc ON g.id = gc.game_id
                    JOIN info.club c ON gc.club_id = c.id
                    WHERE c.id = :clubId AND g.is_active = TRUE
                    ORDER BY c.name
                    """
    )
    List<Game> findAllActiveGamesInClub(int clubId);

    List<Game> findAllByIsActiveTrue();
}
