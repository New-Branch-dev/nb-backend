package com.newbranch.backend.domain.mylearning.entity;

import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_learning_styles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLearningStyle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uls_id")
    private Long ulsId;

    @Column(name = "users_id", nullable = false)
    private Long usersId;

    @Column(name = "learning_style", length = 100)
    private String learningStyle;

    @Column(name = "recommended_method", length = 255)
    private String recommendedMethod;

    @Builder
    public UserLearningStyle(Long usersId, String learningStyle, String recommendedMethod) {
        this.usersId = usersId;
        this.learningStyle = learningStyle;
        this.recommendedMethod = recommendedMethod;
    }

    public void update(String learningStyle, String recommendedMethod) {
        this.learningStyle = learningStyle;
        this.recommendedMethod = recommendedMethod;
    }
}
