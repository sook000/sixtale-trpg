package org.infinity.sixtalebackend.domain.map.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.sixtalebackend.domain.map.domain.Map;
import org.infinity.sixtalebackend.domain.map.domain.NPCEvent;
import org.infinity.sixtalebackend.domain.map.domain.PlaceEvent;
import org.infinity.sixtalebackend.domain.map.dto.*;
import org.infinity.sixtalebackend.domain.map.repository.MapRepository;
import org.infinity.sixtalebackend.domain.map.repository.NPCEventRepository;
import org.infinity.sixtalebackend.domain.map.repository.PlaceEventRepository;
import org.infinity.sixtalebackend.domain.room.domain.Room;
import org.infinity.sixtalebackend.domain.room.repository.RoomRepository;
import org.infinity.sixtalebackend.domain.scenario.domain.Scenario;
import org.infinity.sixtalebackend.domain.scenario.repository.ScenarioRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class MapServiceImpl implements MapService {

    private final RoomRepository roomRepository;
    private final ScenarioRepository scenarioRepository;
    private final MapRepository mapRepository;
    private final PlaceEventRepository placeEventRepository;
    private final NPCEventRepository npcEventRepository;

    @Override
    public MapListResponse getMapList(Long roomID) {
        try {
            Room room = roomRepository.findById(roomID).get();
            Scenario scenario = scenarioRepository.findById(room.getScenario().getId()).get();
            List<Map> mapList = mapRepository.findByScenario(scenario);

            List<MapResponse> maps = mapList.stream()
                    .map(m -> MapResponse.builder()
                            .id(m.getId())
                            .name(m.getName())
                            .description(m.getDescription())
                            .scenarioID(m.getScenario().getId())
                            .isNpc(m.isNpc())
                            .isPlace(m.isPlace())
                            .imageURL(m.getImageURL())
                            .bgmURL(m.getBgmURL())
                            .build())
                    .collect(Collectors.toList());

            return MapListResponse.builder().mapList(maps).build();
        }catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }

    @Override
    public MapResponse readMap(Long roomID, Long mapID) {
        Map map = mapRepository.findById(mapID).get();

        MapResponse response = MapResponse.builder()
                .id(map.getId())
                .name(map.getName())
                .scenarioID(map.getScenario().getId())
                .description(map.getDescription())
                .isNpc(map.isNpc())
                .isPlace(map.isPlace())
                .imageURL(map.getImageURL())
                .bgmURL(map.getBgmURL())
                .build();

        return response;
    }

    @Override
    public PlaceEventListResponse getPlaceEventList(Long roomID, Long mapID) {
        Map map = mapRepository.findById(mapID).get();
        List<PlaceEvent> placeEventList = placeEventRepository.findByMap(map);

        List<PlaceEventResponse> placeEvents = placeEventList.stream()
                .map(m -> PlaceEventResponse.builder()
                        .id(m.getId())
                        .mapId(m.getMap().getId())
                        .row(m.getRow())
                        .col(m.getCol())
                        .description(m.getDescription())
                        .nextMapId(m.getNextMap().getId())
                        .nextMapUrl(m.getNextMap().getImageURL())
                        .build())
                .collect(Collectors.toList());

        return PlaceEventListResponse.builder().placeEvents(placeEvents).build();
    }

    @Override
    public NPCEventListResponse getNPCEventList(Long roomID, Long mapID) {
        Map map = mapRepository.findById(mapID).get();
        List<NPCEvent> npcEventList = npcEventRepository.findByMap(map);

        List<NPCEventResponse> npcEvents = npcEventList.stream()
                .map(m -> NPCEventResponse.builder()
                        .id(m.getId())
                        .mapId(m.getMap().getId())
                        .description(m.getDescription())
                        .currentHp(m.getCurrentHp())
                        .build())
                .collect(Collectors.toList());

        return NPCEventListResponse.builder().npcEvents(npcEvents).build();
    }

    @Override
    public PlaceEventResponse readPlaceEvent(Long roomID, Long mapID, Long placeEventID) {
        PlaceEvent placeEvent = placeEventRepository.findById(placeEventID).get();

        PlaceEventResponse response = PlaceEventResponse.builder()
                .id(placeEvent.getId())
                .mapId(placeEvent.getMap().getId())
                .row(placeEvent.getRow())
                .col(placeEvent.getCol())
                .description(placeEvent.getDescription())
                .nextMapId(placeEvent.getNextMap().getId())
                .nextMapUrl(placeEvent.getNextMap().getImageURL())
                .build();

        return response;
    }

    @Override
    public NPCEventResponse readNPCEvent(Long roomID, Long mapID, Long npcEventID) {
        NPCEvent npcEvent = npcEventRepository.findById(npcEventID).get();

        NPCEventResponse response = NPCEventResponse.builder()
                .id(npcEvent.getId())
                .mapId(npcEvent.getMap().getId())
                .description(npcEvent.getDescription())
                .currentHp(npcEvent.getCurrentHp())
                .build();

        return response;
    }
}
