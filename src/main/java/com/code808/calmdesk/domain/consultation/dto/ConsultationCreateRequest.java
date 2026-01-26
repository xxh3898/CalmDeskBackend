package com.code808.calmdesk.domain.consultation.dto;

import com.code808.calmdesk.domain.consultation.entity.Consultation;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConsultationCreateRequest {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    public Consultation toEntity() {
        return Consultation.builder()
                .title(title)
                .description(description)
                .build();
    }
}
