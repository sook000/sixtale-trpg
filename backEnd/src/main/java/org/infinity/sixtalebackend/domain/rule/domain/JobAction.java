package org.infinity.sixtalebackend.domain.rule.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.infinity.sixtalebackend.domain.model.DiceType;

import java.util.List;

@Entity
@Getter
@Builder
@Table(name="job_action")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean isCore;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDice;

    @Enumerated(EnumType.STRING)
    private DiceType diceType;

    private Integer diceCount;

    @ColumnDefault("1")
    @Column(nullable = false)
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @OneToMany(mappedBy = "jobAction", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ActionOption> actionOptions;
}
