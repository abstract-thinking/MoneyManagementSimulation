package com.example.risk.service.rsl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Service
public class RslService {

    private final String url;

    public RslService() {
        this.url = "https://www.finanztreff.de/indizes/einzelwerte/NASDAQ-100-Index/";
    }

    @Cacheable("fetchTable")
    public String fetchTable() {
        return new RestTemplate()
                .postForEntity(url, new HttpEntity<>(createFormData(), createHeaders()), String.class)
                .getBody();
    }

    private MultiValueMap<String, String> createFormData() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("exchangeId", "0");
        map.add("ansicht", "techkennzahlen");
        map.add("seoTag", "NASDAQ-100-Index");
        map.add("sortierung", "idkRelativeStrengthLevy");
        map.add("ascdesc", "DESC");
        map.add("ajax", "0");

        return map;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        return headers;
    }
}