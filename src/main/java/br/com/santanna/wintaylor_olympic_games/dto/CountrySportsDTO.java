package br.com.santanna.wintaylor_olympic_games.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountrySportsDTO {
    private List<String> sports;
}
