package br.com.santanna.wintaylor_olympic_games.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HighestBMIResponseDTO {
    private String country;
    private double averageBMI;
}
