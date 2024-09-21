package org.infinity.sixtalebackend.domain.map.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.infinity.sixtalebackend.domain.map.domain.Map;

@Getter
@Builder
public class PlaceEventResponse {

    private Long id;
    private Long mapId;
    private Integer row;
    private Integer col;
    private String description;
    private Long nextMapId;
    private String nextMapUrl;

}
