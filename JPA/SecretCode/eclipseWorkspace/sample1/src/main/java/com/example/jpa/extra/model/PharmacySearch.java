package com.example.jpa.extra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacySearch {

    private String sido;
    private String gugun;

    public String getSearchSido() {
        return sido != null ? sido : "";
    }

    public String getSearchGugun() {
        return gugun != null ? gugun : "";
    }
}
