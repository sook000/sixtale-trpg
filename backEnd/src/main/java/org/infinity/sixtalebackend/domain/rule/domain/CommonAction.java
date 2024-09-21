package org.infinity.sixtalebackend.domain.rule.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.infinity.sixtalebackend.domain.model.DiceType;

@Entity
@Getter
@Builder
@Table(name = "common_action")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isBasic;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isDice;

    @Enumerated(EnumType.STRING)
    private DiceType diceType;

    private Integer diceCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private Rule rule;
}
