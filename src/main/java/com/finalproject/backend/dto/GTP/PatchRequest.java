package com.finalproject.backend.dto.GTP;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchRequest {
    @Schema(
            description = "통합 diff 패치 문자열 (GitHub Compare API의 각 file.patch를 이어붙인 것)",
            example     = "@@ -1,2 +1,2 @@\n- old line\n+ new line"
    )
    private String patch;
}
