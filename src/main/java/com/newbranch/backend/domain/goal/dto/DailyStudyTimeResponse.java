package com.newbranch.backend.domain.goal.dto;

import com.newbranch.backend.domain.goal.entity.DailyStudyTime;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyStudyTimeResponse {

    private Long id;
    private LocalDate studyDate;
    private Integer availableMinutes;

    public static DailyStudyTimeResponse from(DailyStudyTime dst) {
        return DailyStudyTimeResponse.builder()
                .id(dst.getId())
                .studyDate(dst.getStudyDate())
                .availableMinutes(dst.getAvailableMinutes())
                .build();
    }
}
