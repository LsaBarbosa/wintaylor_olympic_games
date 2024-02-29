package br.com.santanna.wintaylor_olympic_games.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OverAgeByContinentResponseDTO {

    private List<OverAgeResponseDTO> continents;
}
