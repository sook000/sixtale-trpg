package org.infinity.sixtalebackend.domain.map.repository;

import org.infinity.sixtalebackend.domain.map.domain.Map;
import org.infinity.sixtalebackend.domain.map.domain.NPCEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NPCEventRepository extends JpaRepository<NPCEvent, Long> {
    List<NPCEvent> findByMap(Map map);
}
