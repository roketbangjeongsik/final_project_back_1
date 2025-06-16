package com.finalproject.backend.dto.research;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDTO {
    private List<Integer> ids;
} 