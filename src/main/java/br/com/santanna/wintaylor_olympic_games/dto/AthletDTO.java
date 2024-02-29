package br.com.santanna.wintaylor_olympic_games.dto;

import br.com.santanna.wintaylor_olympic_games.domain.Athlete;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AthletDTO {
 @JsonProperty("continent")
 private String continent;
 @JsonProperty("country")
 private String country;
 @JsonProperty("height")
 private double height;
 @JsonProperty("weight")
 private Integer weight;
 @JsonProperty("BMI")
 private double BMI;
 @JsonProperty("age")
 private double age;
 @JsonProperty("gender")
 private String gender;
 @JsonProperty("sport")
 private String sport;


}
