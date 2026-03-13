package ru.andef.andefracing.backend.data.repositories.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.andef.andefracing.backend.data.entities.Client;
import ru.andef.andefracing.backend.data.entities.club.Club;
import ru.andef.andefracing.backend.data.entities.location.City;
import ru.andef.andefracing.backend.data.entities.location.Region;
import ru.andef.andefracing.backend.data.repositories.ClientRepository;
import ru.andef.andefracing.backend.data.repositories.location.CityRepository;
import ru.andef.andefracing.backend.data.repositories.location.RegionRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql(scripts = "classpath:scripts/db/create-test-schema.sql")
class ClubRepositoryTest {

    private final ClubRepository clubRepository;
    private final ClientRepository clientRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;

    @Autowired
    ClubRepositoryTest(
            ClubRepository clubRepository,
            ClientRepository clientRepository,
            RegionRepository regionRepository,
            CityRepository cityRepository
    ) {
        this.clubRepository = clubRepository;
        this.clientRepository = clientRepository;
        this.regionRepository = regionRepository;
        this.cityRepository = cityRepository;
    }

    private Region createRegion() {
        Region region = new Region((short) 0, "Region", new ArrayList<>());
        return regionRepository.save(region);
    }

    private City createCity(Region region) {
        City city = new City((short) 0, region, "City");
        return cityRepository.save(city);
    }

    private Club createClub(City city, String name) {
        Club club = new Club(
                0,
                city,
                name,
                "+7-000-000-00-00",
                "test@example.com",
                "Test address",
                (short) 10,
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        return clubRepository.save(club);
    }

    private Client createClient(String name, String phone) {
        Client client = new Client(name, phone, "password");
        return clientRepository.save(client);
    }

    @Test
    void getClientFavoriteClubsReturnsOnlyFavoritesOfClientOrderedByName() {
        Region region = createRegion();
        City city = createCity(region);

        Club clubA = createClub(city, "Alpha Club");
        Club clubB = createClub(city, "Beta Club");
        Club clubC = createClub(city, "Gamma Club");

        Client client1 = createClient("Client1", "+7-111-111-11-11");
        Client client2 = createClient("Client2", "+7-222-222-22-22");

        client1.addFavoriteClub(clubB);
        client1.addFavoriteClub(clubA);
        clientRepository.save(client1);

        client2.addFavoriteClub(clubC);
        clientRepository.save(client2);

        Page<Club> page = clubRepository.getClientFavoriteClubs(client1.getId(), PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
        List<Club> content = page.getContent();
        assertEquals(2, content.size());
        assertEquals(clubA.getId(), content.get(0).getId());
        assertEquals(clubB.getId(), content.get(1).getId());
        assertTrue(content.stream().noneMatch(club -> club.getId() == clubC.getId()));
    }
}

