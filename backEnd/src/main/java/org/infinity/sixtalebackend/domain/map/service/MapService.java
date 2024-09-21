package org.infinity.sixtalebackend.domain.map.service;

import org.infinity.sixtalebackend.domain.map.dto.*;

public interface MapService {
    MapListResponse getMapList(Long roomID);
    MapResponse readMap(Long roomID, Long mapID);
    PlaceEventListResponse getPlaceEventList(Long roomID, Long mapID);
    NPCEventListResponse getNPCEventList(Long roomID, Long mapID);
    PlaceEventResponse readPlaceEvent(Long roomID, Long mapID, Long placeEventID);
    NPCEventResponse readNPCEvent(Long roomID, Long mapID, Long npcEventID);
}
