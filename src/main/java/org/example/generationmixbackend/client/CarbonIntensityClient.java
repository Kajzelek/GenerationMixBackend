package org.example.generationmixbackend.client;

import org.example.generationmixbackend.dto.CarbonIntensityResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CarbonIntensityClient {
    private final RestClient restClient;

    public CarbonIntensityClient(){
        this.restClient = RestClient.builder()
                .baseUrl("https://api.carbonintensity.org.uk")
                .build();
    }

    public CarbonIntensityResponse getCarbonIntensity(ZonedDateTime from, ZonedDateTime to){
        String fromStr = from.format(DateTimeFormatter.ISO_INSTANT);
        String toStr = to.format(DateTimeFormatter.ISO_INSTANT);

        return restClient.get()
                .uri("/generation/from/{from}/{to}", fromStr, toStr)
                .retrieve()
                .body(CarbonIntensityResponse.class);
    }


}
