package com.example.jpa.extra;

import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.extra.model.AirInput;
import com.example.jpa.extra.model.OpenApiResult;
import com.example.jpa.extra.model.PharmacySearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class ApiExtraController {

    @GetMapping("/api/extra/pharmacy")
    public String pharmacy() {

        String apiKey = "";

        String url = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10";

        String apiResult = "";
        try {
            URI uri = new URI(String.format(url, apiKey));

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String result = restTemplate.getForObject(uri, String.class);
            apiResult = result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    @GetMapping("/api/extra/pharmacy/v2")
    public ResponseEntity<?> pharmacyV2() {

        String apiKey = "";

        String url = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10";

        String apiResult = "";
        try {
            URI uri = new URI(String.format(url, apiKey));

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String result = restTemplate.getForObject(uri, String.class);
            apiResult = result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        OpenApiResult jsonResult = null;
        try {
            jsonResult = objectMapper.readValue(apiResult, OpenApiResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseResult.success(jsonResult);
    }

    @GetMapping("/api/extra/pharmacy/v3")
    public ResponseEntity<?> pharmacyV3(@RequestBody PharmacySearch pharmacySearch) {

        String apiKey = "";

        String url = String.format("http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10", apiKey);

        String apiResult = "";
        try {

            url += String.format("&Q0=%s&Q1=%s",
                    URLEncoder.encode(pharmacySearch.getSearchSido(), "UTF-8"),
                    URLEncoder.encode(pharmacySearch.getSearchGugun(), "UTF-8")
            );

            URI uri = new URI(url);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String result = restTemplate.getForObject(uri, String.class);
            apiResult = result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        OpenApiResult jsonResult = null;
        try {
            jsonResult = objectMapper.readValue(apiResult, OpenApiResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseResult.success(jsonResult);
    }


    @GetMapping("/api/extra/air")
    public String air(@RequestBody AirInput airInput) {

        String apiKey = "";

        String url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=%s&returnType=json&numOfRows=100&pageNo=1&sidoName=%s&ver=1.0";
        String apiResult = "";
        try {
            URI uri = new URI(String.format(url, apiKey, URLEncoder.encode(airInput.getSearchSido(), "UTF-8")));

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String result = restTemplate.getForObject(uri, String.class);
            apiResult = result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return apiResult;
    }

}
