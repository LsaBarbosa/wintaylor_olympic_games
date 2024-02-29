package br.com.santanna.wintaylor_olympic_games.controller;

import java.util.List;

import br.com.santanna.wintaylor_olympic_games.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.santanna.wintaylor_olympic_games.service.AthleteService;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/olympic-games")
public class OlympicController {
    @Autowired
    private AthleteService athleteService;

    @PostMapping("/data")
    public String loadJsonData() {
        return athleteService.jsonReader();
    }

    @GetMapping("/sport/")
    public List<CountrySportsDTO> getSportByCountryCode(@RequestParam String country) {
        return athleteService.sportByCountry(country);
    }
    
    @GetMapping("/athletes/stats")
    public StatsResponseDTO getAthleteStatsByCountryAndSport(@RequestParam String country,
                                                             @RequestParam String sport) {
        return athleteService.stats(country, sport);
    }
    
    @GetMapping("/athletes/stats/age")
    public OverAgeByContinentResponseDTO getAthletsOverAge(@RequestParam String sport, @RequestParam double age) {
        return athleteService.getOverAge(sport, age);
    }
    
    @GetMapping("continent/bmi")
    public HighestBMIResponseDTO getCountryWithHighestBMI(@RequestParam String continent, @RequestParam String gender) {
        return athleteService.highestBmi(continent, gender);
    }

    @PostMapping("/athletes/add-football-br")
    public ResponseEntity<AthletResponseDTO> addAthleteToBrazilianFootballTeam(@RequestBody AthletDTO athlete) {
        try {
            AthletResponseDTO responseDTO = athleteService.addAthleteToBrazilianFootballTeam(athlete);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/athletes/get-football-players-br")
    public List<AthletResponseDTO> allAthleteFromBrazilianFootballTeam() {
        return athleteService.getAllMaleAthleteFromBrazilianFootball();
    }

    @PutMapping("/athletes/edit-athlete")
    public ResponseEntity<AthletResponseDTO> editPlayerInfo(@RequestParam Integer keyObject, @RequestBody AthletDTO athlete) {
        AthletResponseDTO responseDTO = athleteService.editAthlet(keyObject, athlete);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
    @GetMapping("/athletes/pre-select")
    public ResponseEntity<List<AthletResponseDTO>> preSelectAthletesForParisOlympics() {
        try {
            List<AthletResponseDTO> selectedAthletes = athleteService.preSelectForParis();
            if (selectedAthletes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(selectedAthletes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/athletes/add-olympic-br")
    public ResponseEntity<List<AthletResponseDTO>> addAthleteToBrazilOlympicTeam(@RequestBody AthletDTO newAthlete) {
        try {
            List<AthletResponseDTO> responseDTOs = athleteService.addAthleteToBrazilianTeam(newAthlete);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTOs);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).body(null);
        }
    }
}
