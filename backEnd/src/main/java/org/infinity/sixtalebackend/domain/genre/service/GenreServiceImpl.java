package org.infinity.sixtalebackend.domain.genre.service;

import lombok.RequiredArgsConstructor;
import org.infinity.sixtalebackend.domain.genre.domain.Genre;
import org.infinity.sixtalebackend.domain.genre.repository.GenreRepository;
import org.infinity.sixtalebackend.domain.scenario.dto.ScenarioListResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public List<Genre> getGenreList() {
        List<Genre> genres = genreRepository.findAll();
        return genres;
    }
}
