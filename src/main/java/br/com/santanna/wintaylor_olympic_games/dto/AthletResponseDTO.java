package br.com.santanna.wintaylor_olympic_games.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AthletResponseDTO {
 private String continent;
 private String country;
 private double height;
 private Integer weight;
 @JsonProperty("BMI")
 private double BMI;
 private double age;
 private String gender;
 private String sport;
 public AthletResponseDTO(AthletDTO athletDTO) {
  this.continent = athletDTO.getContinent();
  this.country = athletDTO.getCountry();
  this.height = athletDTO.getHeight();
  this.weight = athletDTO.getWeight();
  this.BMI = athletDTO.getBMI();
  this.age = athletDTO.getAge();
  this.gender = athletDTO.getGender();
  this.sport = athletDTO.getSport();
 }

 public String toCustomString() {
  return "Athlete: [Continent: " + continent +
          ", Country: " + country +
          ", Height: " + height +
          ", Weight: " + weight +
          ", BMI: " + BMI +
          ", Age: " + age +
          ", Gender: " + gender +
          ", Sport: " + sport +
          "]";
 }

 }
