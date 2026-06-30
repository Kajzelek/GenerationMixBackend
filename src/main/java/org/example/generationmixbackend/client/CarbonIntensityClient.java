package org.example.generationmixbackend.client;

import org.example.generationmixbackend.dto.CarbonIntensityResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CarbonIntensityClient {
    private final RestClient restClient;

    private static final DateTimeFormatter API_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");

    public CarbonIntensityClient(){
        this.restClient = RestClient.builder()
                .baseUrl("https://api.carbonintensity.org.uk")
                .build();
    }

    public CarbonIntensityResponse getCarbonIntensity(ZonedDateTime from, ZonedDateTime to){
        String fromStr = from.format(API_DATE_FORMATTER);
        String toStr = to.format(API_DATE_FORMATTER);

        URI uri = UriComponentsBuilder.fromUriString("https://api.carbonintensity.org.uk/generation/{from}/{to}")
                .buildAndExpand(fromStr, toStr)
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(CarbonIntensityResponse.class);
    }
}
