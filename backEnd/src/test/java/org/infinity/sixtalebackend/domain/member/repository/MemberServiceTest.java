/*
package org.infinity.sixtalebackend.domain.member.repository;

import org.infinity.sixtalebackend.domain.member.service.MemberSerivceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberSerivceImpl memberSerivce;

    @Test
    public void testIsNicknameDuplicated_NicknameExists() {
        String nickname = "existingNickname";
        when(memberRepository.existsByNickname(nickname)).thenReturn(true);

        boolean isDuplicated = memberSerivce.isNicknameDuplicated(nickname);

        assertThat(isDuplicated).isTrue();
        verify(memberRepository, times(1)).existsByNickname(nickname);
    }

    @Test
    public void testIsNicknameDuplicated_NicknameDoesNotExist() {
        String nickname = "newNickname";
        when(memberRepository.existsByNickname(nickname)).thenReturn(false);
        boolean isDuplicated = memberSerivce.isNicknameDuplicated(nickname);

        assertThat(isDuplicated).isFalse();
        verify(memberRepository, times(1)).existsByNickname(nickname);
    }
}
*/
