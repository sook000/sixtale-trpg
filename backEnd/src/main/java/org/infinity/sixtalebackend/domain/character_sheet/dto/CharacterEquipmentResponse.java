package org.infinity.sixtalebackend.domain.character_sheet.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CharacterEquipmentResponse {
    private Long id;
    private Long equipmentID;
    private String name;
    private String description;
    private Long typeID;
    private String typeName;
    private Integer weight;
    private Integer currentCount;
    private String imageURL;
}