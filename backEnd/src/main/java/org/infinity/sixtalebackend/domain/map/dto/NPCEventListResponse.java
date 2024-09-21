package org.infinity.sixtalebackend.domain.map.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.infinity.sixtalebackend.domain.map.domain.NPCEvent;

import java.util.List;

@Getter
@Builder
public class NPCEventListResponse {

    List<NPCEventResponse> npcEvents;

}
