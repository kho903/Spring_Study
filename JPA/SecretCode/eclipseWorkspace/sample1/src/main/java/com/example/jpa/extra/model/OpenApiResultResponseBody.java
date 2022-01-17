package com.example.jpa.extra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenApiResultResponseBody {

    private OpenApiResultResponseBodyItems items;

    private int numOfRows;
    private int pageNo;
    private int totalCount;
}
