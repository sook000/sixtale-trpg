package org.infinity.sixtalebackend.domain.genre.service;

import org.infinity.sixtalebackend.domain.genre.domain.Genre;
import org.infinity.sixtalebackend.domain.scenario.dto.ScenarioListResponseDto;

import java.util.List;

public interface GenreService {
    List<Genre> getGenreList();
}
