package br.com.santanna.wintaylor_olympic_games.service;

import java.util.*;
import java.util.stream.Collectors;

import br.com.santanna.wintaylor_olympic_games.dto.*;
import br.com.santanna.wintaylor_olympic_games.exceptions.InternalServerErrorException;
import br.com.santanna.wintaylor_olympic_games.exceptions.NotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AthleteService {
    private HashMap<String, AthletDTO> olympicAthletes = new HashMap<>();
    public String jsonReader() {
        final String DATA_URL = "https://cdn.jsdelivr.net/gh/highcharts/highcharts@24912efc85/samples/data/olympic2012.json";
        RestTemplate restTemplate = new RestTemplate();

        try {
            List<AthletDTO> athlets = restTemplate.
                    exchange(DATA_URL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<AthletDTO>>()
                    {}).getBody();
            if (athlets == null || athlets.isEmpty()) {
                throw new NotFoundException("No athletes found.");
            }
            for (int i = 0; i < athlets.size(); i++) {
                olympicAthletes.put(String.valueOf(i), athlets.get(i));
            }

            return "Data loaded successfully";
        } catch (RestClientException e) {
           throw new InternalServerErrorException("Failed to load data: " + e.getMessage());

        }

    }

    public List<CountrySportsDTO> sportByCountry(String country) {

        List<AthletDTO> sportsByCountry = olympicAthletes.values().stream()
                .filter(a -> a.getCountry().equalsIgnoreCase(country)).collect(Collectors.toList());

        List<String> sportList = sportsByCountry.stream().map(AthletDTO::getSport).distinct().collect(Collectors.toList());

         List<CountrySportsDTO> countrySportsList = new ArrayList<>();

         for (String sport : sportList){
             CountrySportsDTO countrySportsDTO = new CountrySportsDTO(Collections.singletonList(sport));
             countrySportsList.add(countrySportsDTO);
         }

        return countrySportsList;
    }

    public StatsResponseDTO stats(String country, String sport) {

        List<AthletDTO> athletesByCountryAndSport = olympicAthletes.values().stream()
                .filter(a -> a.getCountry().equalsIgnoreCase(country))
                .filter(a -> a.getSport().equalsIgnoreCase(sport)).collect(Collectors.toList());


        long maleCount = athletesByCountryAndSport.stream().filter(a -> a.getGender().equalsIgnoreCase("male")).count();
        long femaleCount = athletesByCountryAndSport.size() - maleCount;


        double totalAge = athletesByCountryAndSport.stream().mapToDouble(AthletDTO::getAge).sum();
        double averageAge = athletesByCountryAndSport.isEmpty() ? 0 : totalAge / athletesByCountryAndSport.size();

        return new StatsResponseDTO(maleCount, femaleCount, averageAge);
    }

    public OverAgeByContinentResponseDTO getOverAge(String sport, double age) {
        Map<String, Long> overAgeByContinent = olympicAthletes.values().stream()
                .filter(athlete -> athlete.getSport().equalsIgnoreCase(sport))
                .filter(athlete -> athlete.getAge() > age)
                .collect(Collectors.groupingBy(AthletDTO::getContinent, Collectors.counting()));

        List<OverAgeResponseDTO> continents = overAgeByContinent.entrySet().stream()
                .map(entry -> new OverAgeResponseDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new OverAgeByContinentResponseDTO(continents);
    }
    public HighestBMIResponseDTO highestBmi(String continent, String gender) {

        List<AthletDTO> athletesByContinentAndGender = olympicAthletes.values().stream()
                .filter(a -> a.getContinent().equalsIgnoreCase(continent))
                .filter(a -> a.getGender().equalsIgnoreCase(gender)).collect(Collectors.toList());

        double sumBMI = athletesByContinentAndGender.stream().mapToDouble(AthletDTO::getBMI).sum();
        double averageBMI = athletesByContinentAndGender.isEmpty() ? 0 : sumBMI / athletesByContinentAndGender.size();
        OptionalDouble maxBMI = athletesByContinentAndGender.stream().mapToDouble(AthletDTO::getBMI).max();

        AthletDTO countryWithMaxBMI = athletesByContinentAndGender.stream()
                .filter(athlete -> athlete.getBMI() == maxBMI.getAsDouble())
                .findFirst()
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"No country found for the highest BMI."));

        return new HighestBMIResponseDTO(countryWithMaxBMI.getCountry(), averageBMI);
    }

    public AthletResponseDTO addAthleteToBrazilianFootballTeam(AthletDTO athlete) {
        boolean isBrazilian = athlete.getCountry().equalsIgnoreCase("BRA");
        boolean isAthletics = athlete.getSport().equalsIgnoreCase("football");
        boolean isMale = athlete.getGender().equalsIgnoreCase("male");


        if (!(isBrazilian && isAthletics && isMale)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only Male Brazilian athletes can be added to the Brazilian football Team");
        }
        int nextIndex =olympicAthletes.size();
        olympicAthletes.put(Integer.toString(nextIndex),athlete);

        return new AthletResponseDTO(athlete);
    }


    public List<AthletResponseDTO> getAllMaleAthleteFromBrazilianFootball() {
        List<AthletDTO> filterAddList = olympicAthletes.values().stream()
                .filter(athlete -> athlete.getCountry().equalsIgnoreCase("BRA"))
                .filter(athlete -> athlete.getSport().equalsIgnoreCase("football"))
                .filter(athlete -> athlete.getGender().equalsIgnoreCase("male"))
                .collect(Collectors.toList());

        return filterAddList.stream().map(AthletResponseDTO::new).collect(Collectors.toList());
    }

    public AthletResponseDTO editAthlet(Integer keyObject, AthletDTO athlete) {

        olympicAthletes.put(String.valueOf(keyObject), athlete);
        AthletResponseDTO responseDTO = new AthletResponseDTO(athlete);
        return responseDTO;
    }

    public List<AthletResponseDTO> preSelectForParis() {
        List<AthletDTO> selectedAthletes = olympicAthletes.values().stream()
                .filter(athlete -> athlete.getGender().equalsIgnoreCase("female"))
                .filter(athlete -> !athlete.getSport().equalsIgnoreCase("football"))
                .filter(athlete -> athlete.getAge() < 20).collect(Collectors.toList());

        return selectedAthletes.stream().map(AthletResponseDTO::new).collect(Collectors.toList());
    }

    public List<AthletResponseDTO> addAthleteToBrazilianTeam(AthletDTO athlete) {

        boolean isBrazilian = athlete.getCountry().equalsIgnoreCase("BRA");
        boolean isAthletics = athlete.getSport().equalsIgnoreCase("athletics");
        boolean isFemale = athlete.getGender().equalsIgnoreCase("female");
        boolean is18YearsOld = athlete.getAge() == 18;

        if (!(isBrazilian && isAthletics && isFemale && is18YearsOld)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only female Brazilian athletes who are 18 years old can be added to the Brazilian athletics Team");
        }


        int nextIndex = olympicAthletes.size();
        olympicAthletes.put(Integer.toString(nextIndex), athlete);



        return  preSelectForParis();


 }
}