package com.example.jpa.extra;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class ApiExtraController {

    @GetMapping("/api/extra/pharmacy")
    public String pharmacy() {

        String apyKey = "";

        String url = "http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10";

        String apiResult = "";
        try {
            URI uri = new URI(String.format(url, apyKey));

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
