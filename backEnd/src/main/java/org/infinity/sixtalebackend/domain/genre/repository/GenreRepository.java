package org.infinity.sixtalebackend.domain.genre.repository;

import org.infinity.sixtalebackend.domain.genre.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface GenreRepository extends JpaRepository<Genre,Long> {
}
