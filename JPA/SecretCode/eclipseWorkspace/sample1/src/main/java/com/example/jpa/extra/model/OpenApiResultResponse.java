package com.example.jpa.extra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenApiResultResponse {
    private OpenApiResultResponseHeader header;
    private OpenApiResultResponseBody body;
}
