package org.infinity.sixtalebackend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberNicknameCheckResponse {
    private int statusCode;
    private String responseMessage;
    private Data data;
    @Getter
    @AllArgsConstructor
    public static class Data {
        private boolean success;
    }
}
