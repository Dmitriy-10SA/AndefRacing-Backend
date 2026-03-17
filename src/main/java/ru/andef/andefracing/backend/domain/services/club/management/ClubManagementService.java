package ru.andef.andefracing.backend.domain.services.club.management;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.andef.andefracing.backend.CacheConfig;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.club.Game;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.data.entities.club.Price;
import ru.andef.andefracing.backend.data.entities.club.work.schedule.WorkScheduleException;
import ru.andef.andefracing.backend.data.repositories.club.*;
import ru.andef.andefracing.backend.domain.exceptions.DuplicateException;
import ru.andef.andefracing.backend.domain.exceptions.EntityNotFoundException;
import ru.andef.andefracing.backend.domain.exceptions.InvalidDateRangeException;
import ru.andef.andefracing.backend.domain.exceptions.management.CannotAddExceptionDayDueToExistingBookingsException;
import ru.andef.andefracing.backend.domain.exceptions.management.ClubCloseConditionsNotMetException;
import ru.andef.andefracing.backend.domain.exceptions.management.ClubOpenConditionsNotMetException;
import ru.andef.andefracing.backend.domain.exceptions.management.InvalidWorkScheduleException;
import ru.andef.andefracing.backend.domain.mappers.club.GameMapper;
import ru.andef.andefracing.backend.domain.mappers.club.PriceMapper;
import ru.andef.andefracing.backend.domain.mappers.club.WorkScheduleExceptionMapper;
import ru.andef.andefracing.backend.domain.services.search.ClubSearchService;
import ru.andef.andefracing.backend.network.dtos.common.GameDto;
import ru.andef.andefracing.backend.network.dtos.management.AddPriceDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.AddWorkScheduleExceptionDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.UpdateWorkScheduleDto;
import ru.andef.andefracing.backend.network.dtos.management.work.schedule.WorkScheduleExceptionDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubManagementService {
    private final ClubSearchService clubSearchService;

    private final ClubRepository clubRepository;
    private final GameRepository gameRepository;
    private final BookingRepository bookingRepository;
    private final PriceRepository priceRepository;
    private final WorkScheduleExceptionRepository workScheduleExceptionRepository;

    private final GameMapper gameMapper;
    private final PriceMapper priceMapper;
    private final WorkScheduleExceptionMapper workScheduleExceptionMapper;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Валидация графика работы
     */
    private void validateWorkSchedule(boolean isWorkDay, LocalTime openTime, LocalTime closeTime) {
        boolean isValidWorkDay = isWorkDay && openTime != null && closeTime != null;
        boolean isValidWeekend = !isWorkDay && openTime == null && closeTime == null;
        boolean isValidWorkSchedule = isValidWorkDay || isValidWeekend;
        if (!isValidWorkSchedule) {
            throw new InvalidWorkScheduleException(
                    "Неверные данные, если это рабочий день, то нужно передать время открытия и закрытия," +
                            " а если выходной - то время открытия и закрытия null"
            );
        }
        if (isWorkDay && !openTime.isBefore(closeTime)) {
            throw new InvalidWorkScheduleException("Неверные данные, время открытия позже времени закрытия");
        }
    }

    /**
     * Добавить активную игру в клуб (из справочника)
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void addGameToClub(int clubId, short gameId) {
        Club club = clubSearchService.findClubById(clubId);
        Game game = clubSearchService.findGameById(gameId);
        List<Game> gamesInClub = gameRepository.findAllActiveGamesInClub(club.getId());
        if (gamesInClub.contains(game)) {
            throw new DuplicateException("Игра с id " + gameId + " уже есть в клубе");
        }
        club.addGame(game);
        clubRepository.save(club);
    }

    /**
     * Получение справочника игр (только активных)
     */
    @Cacheable(value = CacheConfig.CacheNames.GAMES, key = "'all'")
    @Transactional(readOnly = true)
    public List<GameDto> getAllActiveGames() {
        List<Game> games = gameRepository.findAllByIsActiveTrue();
        return gameMapper.toDto(games);
    }

    /**
     * Удалить игру из клуба
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void deleteGameInClub(int clubId, short gameId) {
        Club club = clubSearchService.findClubById(clubId);
        Game game = clubSearchService.findGameById(gameId);
        List<Game> gamesInClub = gameRepository.findAllActiveGamesInClub(club.getId());
        if (!gamesInClub.contains(game)) {
            throw new EntityNotFoundException("Игра с id " + gameId + " не найдена в клубе");
        }
        club.deleteGame(game);
        clubRepository.save(club);
    }

    /**
     * Изменение количества симуляторов в выбранном текущим клубе
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void updateCntEquipmentInClub(int clubId, short cntEquipment) {
        Club club = clubSearchService.findClubById(clubId);
        club.setCntEquipment(cntEquipment);
        clubRepository.save(club);
    }

    /**
     * Открыть клуб
     */
    @CacheEvict(
            value = {
                    CacheConfig.CacheNames.CLUB_FULL_INFO,
                    CacheConfig.CacheNames.CLUBS_IN_CITY,
                    CacheConfig.CacheNames.REGIONS,
                    CacheConfig.CacheNames.CITIES
            },
            allEntries = true
    )
    @Transactional
    public void openClub(int clubId) {
        Club club = clubSearchService.findClubById(clubId);
        int photosCnt = club.getPhotos().size();
        int pricesCnt = club.getPrices().size();
        int workSchedulesCnt = club.getWorkSchedules().size();
        int activeGamesCnt = gameRepository.findAllActiveGamesInClub(club.getId()).size();
        if (photosCnt >= 1 && pricesCnt >= 1 && workSchedulesCnt == 7 && activeGamesCnt >= 1) {
            club.setOpen(true);
            clubRepository.save(club);
        } else {
            throw new ClubOpenConditionsNotMetException(clubId);
        }
    }

    /**
     * Закрыть клуб
     */
    @CacheEvict(
            value = {
                    CacheConfig.CacheNames.CLUB_FULL_INFO,
                    CacheConfig.CacheNames.CLUBS_IN_CITY,
                    CacheConfig.CacheNames.REGIONS,
                    CacheConfig.CacheNames.CITIES
            },
            allEntries = true
    )
    @Transactional
    public void closeClub(int clubId) {
        Club club = clubSearchService.findClubById(clubId);
        long countUpcomingPaidOrPendingBookings = bookingRepository.countUpcomingPaidOrPendingBookings(club.getId());
        if (countUpcomingPaidOrPendingBookings <= 0) {
            club.setOpen(false);
            clubRepository.save(club);
        } else {
            throw new ClubCloseConditionsNotMetException(clubId);
        }
    }

    /**
     * Управление фотографиями в клубе
     */
    @CacheEvict(
            value = {
                    CacheConfig.CacheNames.CLUB_FULL_INFO,
                    CacheConfig.CacheNames.CLUBS_IN_CITY
            },
            allEntries = true
    )
    @Transactional
    public void managePhotosInClub(int clubId, List<MultipartFile> files) throws IOException {
        Set<String> fileNames = files.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.toSet());
        if (files.size() != fileNames.size()) {
            throw new DuplicateException("В списке фотографий есть дубликаты url");
        }
        Club club = clubSearchService.findClubById(clubId);
        if (club.isOpen() && files.isEmpty()) {
            throw new IllegalArgumentException("Для открытого клуба список фотографий не может быть пустым");
        } else if (!club.isOpen() && files.isEmpty()) {
            club.getPhotos().clear();
            clubRepository.save(club);
            return;
        }
        club.getPhotos().clear();
        clubRepository.flush();
        Path uploadDirPath = Paths.get(uploadPath, "clubs", String.valueOf(clubId));
        Files.createDirectories(uploadDirPath);
        short sequenceNumber = 1;
        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID() + ".jpg";
            Path filePath = uploadDirPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            String url = "/files/clubs/" + clubId + "/" + fileName;
            club.addPhoto(new Photo(url, sequenceNumber++));
        }
        clubRepository.save(club);
    }

    /**
     * Добавление цены за кол-во минут игры в клубе
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void addPriceForMinutesInClub(int clubId, AddPriceDto addPriceDto) {
        Club club = clubSearchService.findClubById(clubId);
        List<Price> pricesInClub = club.getPrices();
        List<Short> durationMinutes = pricesInClub.stream().map(Price::getDurationMinutes).toList();
        if (durationMinutes.contains(addPriceDto.durationMinutes())) {
            throw new DuplicateException(
                    "В клубе с id " + clubId + " уже есть стоимость за " + addPriceDto.durationMinutes() + " минут"
            );
        }
        Price price = priceMapper.toEntity(addPriceDto);
        price.setValue(price.getValue().setScale(2, RoundingMode.HALF_EVEN));
        club.addPrice(price);
        clubRepository.save(club);
    }

    /**
     * Изменение цены за кол-во минут игры в клубе
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void updatePriceForMinutesInClub(int clubId, long priceId, BigDecimal value) {
        Club club = clubSearchService.findClubById(clubId);
        for (Price price : club.getPrices()) {
            if (price.getId() == priceId) {
                price.setValue(value.setScale(2, RoundingMode.HALF_EVEN));
                priceRepository.save(price);
                return;
            }
        }
        throw new EntityNotFoundException("Цена с id " + priceId + " не найдена в клубе");
    }

    /**
     * Удаление цены за кол-во минут игры в клубе
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void deletePriceForMinutesInClub(int clubId, long priceId) {
        Club club = clubSearchService.findClubById(clubId);
        Price price = clubSearchService.findPriceById(priceId);
        boolean isDeleted = club.deletePrice(price);
        if (isDeleted) {
            clubRepository.save(club);
        } else {
            throw new EntityNotFoundException("Цена с id " + priceId + " не найдена в клубе");
        }
    }

    /**
     * Добавление «дня-исключения» в график работы в выбранном текущим клубе
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void addWorkScheduleExceptionInClub(int clubId, AddWorkScheduleExceptionDto addWorkScheduleExceptionDto) {
        boolean isWorkDay = addWorkScheduleExceptionDto.isWorkDay();
        LocalDate date = addWorkScheduleExceptionDto.date();
        LocalTime openTime = addWorkScheduleExceptionDto.openTime();
        LocalTime closeTime = addWorkScheduleExceptionDto.closeTime();
        String description = addWorkScheduleExceptionDto.description();
        validateWorkSchedule(isWorkDay, openTime, closeTime);
        Club club = clubSearchService.findClubById(clubId);
        if (workScheduleExceptionRepository.findByClubIdAndDate(club.getId(), date).isPresent()) {
            throw new DuplicateException("День-исключение с датой " + date + " уже есть в клубе");
        }
        OffsetDateTime start = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        if (bookingRepository.existsByDateRangeAndClubId(clubId, start, end)) {
            throw new CannotAddExceptionDayDueToExistingBookingsException();
        }
        WorkScheduleException workScheduleException = isWorkDay
                ? new WorkScheduleException(date, openTime, closeTime, description)
                : new WorkScheduleException(date, description);
        club.addWorkScheduleException(workScheduleException);
        clubRepository.save(club);
    }

    /**
     * Получение исключений в расписании на диапазон дат в клубе
     */
    @Transactional(readOnly = true)
    public List<WorkScheduleExceptionDto> getAllWorkSchedulesExceptionsInClub(
            int clubId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException();
        }
        Club club = clubSearchService.findClubById(clubId);
        List<WorkScheduleException> workScheduleExceptions = workScheduleExceptionRepository.
                findAllByRangeOfDatesBetweenStartAndEnd(club.getId(), startDate, endDate);
        return workScheduleExceptionMapper.toDto(workScheduleExceptions);
    }

    /**
     * Удаление «дня-исключения» в графике работы в выбранном текущим клубе
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void deleteWorkScheduleExceptionInClub(int clubId, long workScheduleExceptionId) {
        Club club = clubSearchService.findClubById(clubId);
        WorkScheduleException workScheduleException = workScheduleExceptionRepository
                .findByIdAndClubId(workScheduleExceptionId, club.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "День-исключение с id " + workScheduleExceptionId + " не найден в клубе"
                        )
                );
        club.deleteWorkScheduleException(workScheduleException);
        clubRepository.save(club);
    }

    /**
     * Изменение графика работы, а точнее времени открытия и/или закрытия в конкретный день недели
     */
    @CacheEvict(value = CacheConfig.CacheNames.CLUB_FULL_INFO, key = "#clubId")
    @Transactional
    public void updateWorkScheduleInClub(int clubId, UpdateWorkScheduleDto updateWorkScheduleDto) {
        boolean isWorkDay = updateWorkScheduleDto.isWorkDay();
        DayOfWeek dayOfWeek = updateWorkScheduleDto.dayOfWeek();
        LocalTime openTime = updateWorkScheduleDto.openTime();
        LocalTime closeTime = updateWorkScheduleDto.closeTime();
        validateWorkSchedule(isWorkDay, openTime, closeTime);
        Club club = clubSearchService.findClubById(clubId);
        if (isWorkDay) {
            club.updateDayFromWorkScheduleToWorkingDay(dayOfWeek, openTime, closeTime);
        } else {
            club.updateDayFromWorkScheduleToNonWorkingDay(dayOfWeek);
        }
        clubRepository.save(club);
    }
}
