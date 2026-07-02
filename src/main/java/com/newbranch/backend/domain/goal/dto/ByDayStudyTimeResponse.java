package com.newbranch.backend.domain.goal.dto;

import com.newbranch.backend.domain.goal.entity.ByDayStudyTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ByDayStudyTimeResponse {

    private Long bstId;
    private Integer dayOfWeek;
    private Integer availableMinutes;

    public static ByDayStudyTimeResponse from(ByDayStudyTime bst) {
        return ByDayStudyTimeResponse.builder()
                .bstId(bst.getId())
                .dayOfWeek(bst.getDayOfWeek())
                .availableMinutes(bst.getAvailableMinutes())
                .build();
    }
}
