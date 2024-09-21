package org.infinity.sixtalebackend.domain.map.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class MapListResponse {
    private List<MapResponse> mapList;
}
