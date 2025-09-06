package com.example.demo.presentation.store.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewUpdateDto {
    @NotBlank
    @Size(min = 5, max = 500)
    private String content;

    @Min(value = 1)
    @Max(value = 5)
    private int rating;
}
