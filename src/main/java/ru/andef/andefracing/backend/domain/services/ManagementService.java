package ru.andef.andefracing.backend.domain.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.repositories.club.ClubRepository;
import ru.andef.andefracing.backend.data.repositories.club.GameRepository;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.management.DuplicateGameInClubException;
import ru.andef.andefracing.backend.domain.mappers.club.GameMapper;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementService {
    private final ClubRepository clubRepository;
    private final GameRepository gameRepository;

    private final GameMapper gameMapper;

    /**
     * Получение клуба по id или выброс исключения
     */
    private Club findClubByIdOrThrow(int clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("Клуб с id " + clubId + " не найден"));
    }

    /**
     * Получение игры по id или выброс исключения
     */
    private Game findGameByIdOrThrow(short gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Игра с id " + gameId + " не найдена"));
    }

    /**
     * Добавить активную игру в клуб (из справочника)
     */
    @Transactional
    public void addGameToClub(int clubId, short gameId) {
        Club club = findClubByIdOrThrow(clubId);
        Game game = findGameByIdOrThrow(gameId);
        List<Game> gamesInClub = gameRepository.findAllActiveGamesInClub(club.getId());
        if (gamesInClub.contains(game)) {
            throw new DuplicateGameInClubException(game.getId());
        }
        club.addGame(game);
        clubRepository.save(club);
    }

    /**
     * Получение справочника игр (только активных)
     */
    @Transactional(readOnly = true)
    public List<GameDto> getAllActiveGames() {
        List<Game> games = gameRepository.findAllByIsActiveTrue();
        return gameMapper.toDto(games);
    }

    /**
     * Удалить игру из клуба
     */
    @Transactional
    public void deleteGameInClub(int clubId, short gameId) {
        Club club = findClubByIdOrThrow(clubId);
        Game game = findGameByIdOrThrow(gameId);
        List<Game> gamesInClub = gameRepository.findAllActiveGamesInClub(club.getId());
        if (!gamesInClub.contains(game)) {
            throw new EntityNotFoundException("Игра с id " + gameId + " не найдена в клубе");
        }
        club.deleteGame(game);
        clubRepository.save(club);
    }
}
