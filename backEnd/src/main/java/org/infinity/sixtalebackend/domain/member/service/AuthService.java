package org.infinity.sixtalebackend.domain.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.infinity.sixtalebackend.domain.member.domain.Member;

public interface AuthService {
    public Member socialLogin(String code, String registrationID);
    public String getAccessToken(String code, String registrationID);
    public JsonNode getUserResource(String accessToken, String registrationID);
    public void withdraw(Member meber);

    void saveAccessToken(Member member, String accessToken);
}
