package org.infinity.sixtalebackend.domain.equipment.controller;

import lombok.AllArgsConstructor;
import org.infinity.sixtalebackend.domain.equipment.dto.EquipmentListResponse;
import org.infinity.sixtalebackend.domain.equipment.service.EquipmentService;
import org.infinity.sixtalebackend.domain.rule.dto.JobListResponse;
import org.infinity.sixtalebackend.global.common.response.DefaultResponse;
import org.infinity.sixtalebackend.global.common.response.ResponseMessage;
import org.infinity.sixtalebackend.global.common.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
@AllArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/{ruleID}/equipments")
    public ResponseEntity readJobList(@PathVariable Long ruleID) {
        try {
            EquipmentListResponse response = equipmentService.readEquipmentList(ruleID);
            return new ResponseEntity(DefaultResponse.res(StatusCode.OK, ResponseMessage.READ_RULE_EQUIPMENT, response), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.BAD_REQUEST, ResponseMessage.READ_RULE_EQUIPMENT_FAIL), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
