package br.com.santanna.wintaylor_olympic_games.controller;

import br.com.santanna.wintaylor_olympic_games.dto.*;
import br.com.santanna.wintaylor_olympic_games.service.AthleteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OlympicController.class)
@AutoConfigureMockMvc
public class OlympicControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AthleteService athleteService;
    @InjectMocks
    private OlympicController olympicController;

    @Test
    public void testLoadJsonData() throws Exception {
        when(athleteService.jsonReader()).thenReturn("Data loaded successfully");

        mockMvc.perform(post("/olympic-games/data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data loaded successfully"));
    }

    @Test
    public void testGetSportByCountryCode() throws Exception {
        when(athleteService.sportByCountry(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/olympic-games/sport/?country=USA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetAthletsOverAgeEndpoint() throws Exception {

        OverAgeResponseDTO overAgeResponseDTO = new OverAgeResponseDTO("North America", 122);
        OverAgeByContinentResponseDTO mockResponse = new OverAgeByContinentResponseDTO(Collections.singletonList(overAgeResponseDTO));

        when(athleteService.getOverAge(eq("football"), eq(20.0))).thenReturn(mockResponse);

        mockMvc.perform(get("/olympic-games/athletes/stats/age")
                        .param("sport", "football")
                        .param("age", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.continents[0].continent").value("North America"))
                .andExpect(jsonPath("$.continents[0].numberOfAthletes").value(122));
    }

    @Test
    public void testGetCountryWithHighestBMI() throws Exception {

        String continent = "North America";
        String gender = "female";
        HighestBMIResponseDTO responseDTO = new HighestBMIResponseDTO("USA", 21.13);
        when(athleteService.highestBmi(eq(continent), eq(gender))).thenReturn(responseDTO);


        mockMvc.perform(get("/olympic-games/continent/bmi")
                        .param("continent", continent)
                        .param("gender", gender))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.country").value("USA"))
                .andExpect(jsonPath("$.averageBMI").value(21.13));
    }

    @Test
    public void testAddAthleteTOBrazilianFootballTeam() throws Exception {

        AthletDTO athlete = new AthletDTO();
        athlete.setContinent("South America");
        athlete.setCountry("BRA");
        athlete.setHeight(1.75);
        athlete.setWeight(68);
        athlete.setBMI(22.2);
        athlete.setAge(24.2);
        athlete.setGender("male");
        athlete.setSport("football");

        when(athleteService.addAthleteToBrazilianFootballTeam(any(AthletDTO.class))).thenReturn(new AthletResponseDTO(athlete));

        mockMvc.perform(post("/olympic-games/athletes/add-football-br")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "    \"continent\": \"South America\",\n" +
                                "    \"country\": \"BRA\",\n" +
                                "    \"height\": 1.75,\n" +
                                "    \"weight\": 68,\n" +
                                "    \"age\": 24.2,\n" +
                                "    \"gender\": \"male\",\n" +
                                "    \"sport\": \"football\",\n" +
                                "    \"bmi\": 22.2\n" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.continent").value("South America"))
                .andExpect(jsonPath("$.country").value("BRA"))
                .andExpect(jsonPath("$.height").value(1.75))
                .andExpect(jsonPath("$.weight").value(68))
                .andExpect(jsonPath("$.age").value(24.2))
                .andExpect(jsonPath("$.gender").value("male"))
                .andExpect(jsonPath("$.sport").value("football"))
                .andExpect(jsonPath("$.BMI").value(22.2));
    }

    @Test
    public void testAllAthleteFromBrazilianFootballTeamEndpoint() throws Exception {

        List<AthletResponseDTO> athleteResponses = new ArrayList<>();
        athleteResponses.add(new AthletResponseDTO(new AthletDTO("South America", "BRA", 1.8, 70, 21.5, 25, "male", "football")));
        athleteResponses.add(new AthletResponseDTO(new AthletDTO("South America", "BRA", 1.75, 68, 22.2, 24, "male", "football")));

        when(athleteService.getAllMaleAthleteFromBrazilianFootball()).thenReturn(athleteResponses);
        mockMvc.perform(get("/olympic-games/athletes/get-football-players-br"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].continent").value("South America"))
                .andExpect(jsonPath("$[0].country").value("BRA"))
                .andExpect(jsonPath("$[0].height").value(1.8))
                .andExpect(jsonPath("$[0].weight").value(70))
                .andExpect(jsonPath("$[0].BMI").value(21.5))
                .andExpect(jsonPath("$[0].age").value(25))
                .andExpect(jsonPath("$[0].gender").value("male"))
                .andExpect(jsonPath("$[0].sport").value("football"))
                .andExpect(jsonPath("$[1].continent").value("South America"))
                .andExpect(jsonPath("$[1].country").value("BRA"))
                .andExpect(jsonPath("$[1].height").value(1.75))
                .andExpect(jsonPath("$[1].weight").value(68))
                .andExpect(jsonPath("$[1].BMI").value(22.2))
                .andExpect(jsonPath("$[1].age").value(24))
                .andExpect(jsonPath("$[1].gender").value("male"))
                .andExpect(jsonPath("$[1].sport").value("football"));
    }

    @Test
    public void testEditPlayerInfo() throws Exception {

        AthletDTO athleteDTO = new AthletDTO();
        athleteDTO.setContinent("South America");
        athleteDTO.setCountry("ESP");
        athleteDTO.setHeight(1.8);
        athleteDTO.setWeight(60);
        athleteDTO.setAge(19.2);
        athleteDTO.setGender("female");
        athleteDTO.setSport("aquatics");
        athleteDTO.setBMI(18.5);

        AthletResponseDTO responseDTO = new AthletResponseDTO(athleteDTO);

        when(athleteService.editAthlet(anyInt(), any())).thenReturn(responseDTO);

        mockMvc.perform(put("/olympic-games/athletes/edit-athlete?keyObject=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"continent\":\"South America\",\"country\":\"ESP\",\"height\":1.8,\"weight\":60,\"age\":19.2,\"gender\":\"female\",\"sport\":\"aquatics\",\"BMI\":18.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.continent").value("South America"))
                .andExpect(jsonPath("$.country").value("ESP"))
                .andExpect(jsonPath("$.height").value(1.8))
                .andExpect(jsonPath("$.weight").value(60))
                .andExpect(jsonPath("$.age").value(19.2))
                .andExpect(jsonPath("$.gender").value("female"))
                .andExpect(jsonPath("$.sport").value("aquatics"))
                .andExpect(jsonPath("$.BMI").value(18.5));
    }
    @Test
    public void testPreSelectAthletesForParisOlympics() throws Exception {

        AthletResponseDTO expectedAthlete = new AthletResponseDTO();
        expectedAthlete.setContinent("South America");
        expectedAthlete.setCountry("ESP");
        expectedAthlete.setHeight(1.8);
        expectedAthlete.setWeight(60);
        expectedAthlete.setAge(19.2);
        expectedAthlete.setGender("female");
        expectedAthlete.setSport("aquatics");
        expectedAthlete.setBMI(18.5);

        when(athleteService.preSelectForParis()).thenReturn(Collections.singletonList(expectedAthlete));

        mockMvc.perform(get("/olympic-games/athletes/pre-select")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].continent").value("South America"))
                .andExpect(jsonPath("$[0].country").value("ESP"))
                .andExpect(jsonPath("$[0].height").value(1.8))
                .andExpect(jsonPath("$[0].weight").value(60))
                .andExpect(jsonPath("$[0].age").value(19.2))
                .andExpect(jsonPath("$[0].gender").value("female"))
                .andExpect(jsonPath("$[0].sport").value("aquatics"))
                .andExpect(jsonPath("$[0].BMI").value(18.5));
    }

    @Test
    public void testAddAthleteToBrazilOlympicTeamEndpoint() throws Exception {

        AthletResponseDTO athleteResponseDTO = new AthletResponseDTO(new AthletDTO("South America", "BRA", 1.80, 60, 18.0, 18, "female", "athletics"));

        when(athleteService.addAthleteToBrazilianTeam(any())).thenReturn(Collections.singletonList(athleteResponseDTO));

        String requestBody = "{\"continent\":\"South America\",\"country\":\"BRA\",\"height\":1.80,\"weight\":60,\"age\":18.0,\"gender\":\"female\",\"sport\":\"athletics\"}";

        mockMvc.perform(post("/olympic-games/athletes/add-olympic-br")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].continent").value("South America"))
                .andExpect(jsonPath("$[0].country").value("BRA"))
                .andExpect(jsonPath("$[0].height").value(1.80))
                .andExpect(jsonPath("$[0].weight").value(60))
                .andExpect(jsonPath("$[0].age").value(18.0))
                .andExpect(jsonPath("$[0].gender").value("female"))
                .andExpect(jsonPath("$[0].sport").value("athletics"));
    }
}