package br.com.santanna.wintaylor_olympic_games.service;

import br.com.santanna.wintaylor_olympic_games.dto.*;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AthleteServiceTest {
    private AthleteService athleteService;

    @BeforeEach
    public void setUp() {
        athleteService = new AthleteService();
        athleteService.jsonReader();
    }

    @Test
    public void sportByCountry() {
        String country = "BRA";
        List<CountrySportsDTO> sportsList = athleteService.sportByCountry(country);

        assertFalse(sportsList.isEmpty(), "A lista de esportes não deve estar vazia para o país especificado.");

        assertEquals(sportsList.size(), sportsList.stream().distinct().count(), "Não deve haver esportes duplicados na lista.");

        for (Object obj : sportsList) {
            assertTrue(obj instanceof CountrySportsDTO, "Todos os elementos da lista devem ser do tipo CountrySportsDTO.");
        }
    }

    @Test
    public void testStats() {

        String country = "BRA";
        String sport = "football";

        StatsResponseDTO statsResponse = athleteService.stats(country, sport);
        assertNotNull(statsResponse);
        assertTrue(statsResponse.getMaleCount() >= 0);
        assertTrue(statsResponse.getFemaleCount() >= 0);
        assertTrue(statsResponse.getAverageAge() >= 0);
    }

    @Test
    public void testGetOverAge() {

        String sport = "football";
        double age = 30;

        OverAgeByContinentResponseDTO overAgeResponseDTO = athleteService.getOverAge(sport, age);
        assertNotNull(overAgeResponseDTO);
        assertFalse(overAgeResponseDTO.getContinents().isEmpty());
    }

    @Test
    public void testHighestBmi() {
        String continent = "Europe";
        String gender = "male";
        HighestBMIResponseDTO result = athleteService.highestBmi(continent, gender);
        assertNotNull(result);
        assertNotNull(result.getCountry());
        assertTrue(result.getAverageBMI() > 0);
    }

    @Test
    public void testAddAthleteToBrazilianFootballTeam() {
        AthletDTO athlete = new AthletDTO();
        athlete.setContinent("South America");
        athlete.setCountry("BRA");
        athlete.setHeight(1.75);
        athlete.setWeight(68);
        athlete.setBMI(22.2);
        athlete.setAge(24.2);
        athlete.setGender("male");
        athlete.setSport("football");

        AthletResponseDTO response = athleteService.addAthleteToBrazilianFootballTeam(athlete);

        assertNotNull(response);

        boolean athleteAddedCorrectly = athleteService.getAllMaleAthleteFromBrazilianFootball().stream()
                .anyMatch(a -> a.getCountry().equalsIgnoreCase("BRA") &&
                        a.getSport().equalsIgnoreCase("football") &&
                        a.getGender().equalsIgnoreCase("male") &&
                        a.getContinent().equalsIgnoreCase("South America") &&
                        a.getHeight() == 1.75 &&
                        a.getWeight() == 68 &&
                        a.getBMI() == 22.2 &&
                        a.getAge() == 24.2);
        assertTrue(athleteAddedCorrectly);
        boolean athleteADDEDWrongly = athleteService.getAllMaleAthleteFromBrazilianFootball().stream()
                .anyMatch(a -> a.getCountry().equalsIgnoreCase("AFG") &&
                        a.getSport().equalsIgnoreCase("athletics") &&
                        a.getGender().equalsIgnoreCase("female") &&
                        a.getContinent().equalsIgnoreCase("South America") &&
                        a.getHeight() == 1.75 &&
                        a.getWeight() == 68 &&
                        a.getBMI() == 22.2 &&
                        a.getAge() == 24.2);
        assertFalse(athleteADDEDWrongly);
    }

    @Test
    public void testGetAllAthleteFromBrazilianFootball() {

        List<AthletResponseDTO> brazilianFootballTeam = athleteService.getAllMaleAthleteFromBrazilianFootball();
        assertFalse(brazilianFootballTeam.isEmpty(), "A lista de atletas da seleção de futebol do Brasil não deve estar vazia");
        for (AthletResponseDTO athlete : brazilianFootballTeam) {
            assertEquals("BRA", athlete.getCountry(), "O país do atleta deve ser Brasil");
            assertEquals("football", athlete.getSport(), "O esporte do atleta deve ser futebol");
            assertEquals("male", athlete.getGender(), "O gênero do atleta deve ser masculino");
        }
    }

    @Test
    public void testEditAthlete() {

        AthletDTO athleteToUpdate = new AthletDTO("North America", "BRA", 1.80, 24, 19.8, 20.0, "male", "football");

        athleteService.addAthleteToBrazilianFootballTeam(athleteToUpdate);

        int athleteKey = athleteService.getAllMaleAthleteFromBrazilianFootball().size() - 1;

        AthletDTO updatedAthlete = new AthletDTO("North America", "BRA", 1.80, 30, 19.8, 20.0, "male", "football");

        AthletResponseDTO response = athleteService.editAthlet(athleteKey, updatedAthlete);

        assertNotNull(response);
        assertEquals("North America", response.getContinent());
        assertEquals("BRA", response.getCountry());
        assertEquals(1.80, response.getHeight());
        assertEquals(30, response.getWeight());
        assertEquals(19.8, response.getBMI());
        assertEquals(20.0, response.getAge());
        assertEquals("male", response.getGender());
        assertEquals("football", response.getSport());
    }

    @Test
    public void testPreSelectForParis() {
        List<AthletResponseDTO> selectedAthletes = athleteService.preSelectForParis();
        for (AthletResponseDTO athlete : selectedAthletes) {
            assertEquals("female", athlete.getGender(), "Os atletas pré-selecionados devem ser do sexo feminino");
        }
        for (AthletResponseDTO athlete : selectedAthletes) {
            assertNotEquals("football", athlete.getSport(), "O futebol não deve estar entre as modalidades pré-selecionadas");
        }
        for (AthletResponseDTO athlete : selectedAthletes) {
            assertTrue(athlete.getAge() < 20, "Os atletas pré-selecionados devem ter menos de 20 anos");
        }
    }

    @Test
    public void testAddAthleteToBrazilianTeam() {
        AthletDTO athlete = new AthletDTO("North America", "BRA", 1.80, 24, 19.8, 18, "female", "athletics");
        athleteService.addAthleteToBrazilianTeam(athlete);
        List<AthletResponseDTO> selectedAthletes = athleteService.preSelectForParis();

        boolean athleteAddedCorrectly = selectedAthletes.stream()
                .anyMatch(a -> a.getCountry().equalsIgnoreCase("BRA") &&
                        a.getSport().equalsIgnoreCase("athletics") &&
                        a.getGender().equalsIgnoreCase("female") &&
                        a.getAge() == 18);
        assertTrue(athleteAddedCorrectly, "A atleta não foi adicionada corretamente à seleção de atletismo do Brasil.");

        System.out.println("Resultados da pré-seleção para os Jogos Olímpicos de Paris:");
        for (AthletResponseDTO selectedAthlete : selectedAthletes) {
            if (selectedAthlete.getContinent().equalsIgnoreCase("North America") &&
                    selectedAthlete.getCountry().equalsIgnoreCase("BRA") &&
                    selectedAthlete.getHeight() == 1.80 &&
                    selectedAthlete.getWeight() == 24 &&
                    selectedAthlete.getBMI() == 19.8 &&
                    selectedAthlete.getAge() == 18 &&
                    selectedAthlete.getGender().equalsIgnoreCase("female") &&
                    selectedAthlete.getSport().equalsIgnoreCase("athletics")) {

                System.out.println(selectedAthlete.toCustomString());
                break;
            }
        }

    }
}
