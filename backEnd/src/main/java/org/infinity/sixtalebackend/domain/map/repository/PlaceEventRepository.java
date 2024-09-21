package org.infinity.sixtalebackend.domain.map.repository;

import org.infinity.sixtalebackend.domain.map.domain.Map;
import org.infinity.sixtalebackend.domain.map.domain.PlaceEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceEventRepository extends JpaRepository<PlaceEvent, Long> {
    List<PlaceEvent> findByMap(Map map);
}
