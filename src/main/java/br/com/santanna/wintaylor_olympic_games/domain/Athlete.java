package br.com.santanna.wintaylor_olympic_games.domain;

 

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Athlete {
    private String continent;
    private String country;
    private double height;
    private Integer weight;
    private double BMI;
    private double age;
    private String gender;
    private String sport;
}
