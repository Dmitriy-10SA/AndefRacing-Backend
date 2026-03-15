package ru.andef.andefracing.backend.data.repositories.club;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.andef.andefracing.backend.data.entities.club.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Integer> {
    /**
     * Получение списка избранных клубов клиента
     */
    @Query(
            nativeQuery = true,
            value = """
                    SELECT * FROM info.club c
                    JOIN favorite.client_favorite_club cfc ON c.id = cfc.club_id
                    WHERE cfc.client_id = :clientId
                    ORDER BY c.name
                    """,
            countQuery = """
                    SELECT COUNT(*) FROM info.club c
                    JOIN favorite.client_favorite_club cfc ON c.id = cfc.club_id
                    WHERE cfc.client_id = :clientId
                    """
    )
    Page<Club> getClientFavoriteClubs(@Param(value = "clientId") long clientId, Pageable pageable);

    Page<Club> findAllByCity_IdAndIsOpenTrue(short cityId, Pageable pageable);
}