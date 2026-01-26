package com.code808.calmdesk.domain.consultation.dto;

import com.code808.calmdesk.domain.consultation.entity.Consultation;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConsultationCreateRequest {
    @NotBlank(message = "제목은 필수 입력관입니다")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다")
    private String description;

    public Consultation toEntity() {
        return Consultation.builder()
                .title(title)
                .description(description)
                .build();
    }
}
