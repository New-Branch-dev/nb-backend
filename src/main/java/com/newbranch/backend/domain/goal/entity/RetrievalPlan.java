package com.newbranch.backend.domain.goal.entity;

import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "retrieval_plans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RetrievalPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rp_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lg_id", nullable = false)
    private LearningGoal learningGoal;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "repetition_count")
    private Integer repetitionCount;

    private String method;

    @Column(name = "recommended_method")
    private String recommendedMethod;

    public void update(LocalDate startDate, LocalDate endDate, Integer repetitionCount, String method) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.repetitionCount = repetitionCount;
        this.method = method;
    }
}
