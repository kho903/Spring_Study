package com.example.jpa.extra.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenApiResultResponseBodyItemsItem {

    private String dutyAddr;
    private String dutyMapimg; // json 데이터 내의 오타
    private String dutyName;
    private String dutyTel1;
    private int dutyTime1c;
    private String dutyTime1s;
    private int dutyTime2c;
    private String dutyTime2s;
    private int dutyTime3c;
    private String dutyTime3s;
    private int dutyTime4c;
    private String dutyTime4s;
    private int dutyTime5c;
    private String dutyTime5s;
    private int dutyTime6c;
    private String dutyTime6s;
    private int dutyTime7c;
    private String dutyTime7s;
    private int dutyTime8c;
    private String dutyTime8s;
    private String hpid;
    private String postCdn1;
    private String postCdn2;
    private int rnum;
    private double wgs84Lat;
    private double wgs84Lon;
}
