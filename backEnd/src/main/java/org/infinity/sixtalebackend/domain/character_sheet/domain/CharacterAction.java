package org.infinity.sixtalebackend.domain.character_sheet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.infinity.sixtalebackend.domain.room.domain.PlayMember;
import org.infinity.sixtalebackend.domain.rule.domain.ActionOption;
import org.infinity.sixtalebackend.domain.rule.domain.JobAction;

@Entity
@Getter
@Builder
@Table(name = "character_action")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharacterAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_action_id", nullable = false)
    private JobAction jobAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_option_id")
    private ActionOption actionOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "play_member_id", nullable = false)
    private PlayMember playMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_sheet_id", nullable = false)
    private CharacterSheet characterSheet;

}
