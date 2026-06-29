package org.example.generationmixbackend.service;

import org.example.generationmixbackend.client.CarbonIntensityClient;
import org.example.generationmixbackend.dto.CarbonIntensityResponse;
import org.example.generationmixbackend.dto.DailyMixResponse;
import org.example.generationmixbackend.dto.FuelMix;
import org.example.generationmixbackend.dto.GenerationData;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnergyService {
    private final CarbonIntensityClient carbonIntensityClient;

    private static final Set<String> CLEAN_ENERGY_FUELS = Set.of("wind", "solar", "hydro", "nuclear", "biomass");

    public EnergyService(CarbonIntensityClient carbonIntensityClient) {
        this.carbonIntensityClient = carbonIntensityClient;
    }

    public List<DailyMixResponse> getThreeDayPrediction(){

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);

        Set<LocalDate> targetDates = Set.of(today, tomorrow, dayAfterTomorrow);

        ZonedDateTime from = today.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime to = dayAfterTomorrow.atTime(23, 30, 0).atZone(ZoneOffset.UTC);

        CarbonIntensityResponse response = carbonIntensityClient.getCarbonIntensity(from, to);
            if(response == null || response.data() == null){
                return Collections.emptyList();
            }

        Map<LocalDate, List<GenerationData>> dataByDate = response.data().stream()
                .filter(d -> targetDates.contains(d.from().toLocalDate()))
                .collect(Collectors.groupingBy(d -> d.from().toLocalDate()));

        List<DailyMixResponse> result = new ArrayList<>();

        for(Map.Entry<LocalDate, List<GenerationData>> entry : dataByDate.entrySet()){
            LocalDate date = entry.getKey();
            List<GenerationData> intervalsForDay = entry.getValue();


            List<FuelMix> allFuelsForDay = intervalsForDay.stream()
                    .flatMap(d -> d.generationmix().stream())
                    .toList();

            Map<String, Double> averageMix = allFuelsForDay.stream()
                    .collect(Collectors.groupingBy(
                            FuelMix::fuel,
                            Collectors.averagingDouble(FuelMix::perc)
                    ));


            double cleanEnergySum = averageMix.entrySet().stream()
                    .filter(e -> CLEAN_ENERGY_FUELS.contains(e.getKey().toLowerCase()))
                    .mapToDouble(Map.Entry::getValue)
                    .sum();


            averageMix.replaceAll((k, v) -> Math.round(v * 100.0) / 100.0);
            double roundedCleanEnergy = Math.round(cleanEnergySum * 100.0) / 100.0;

            result.add(new DailyMixResponse(date, averageMix, roundedCleanEnergy));
        }

        result.sort(Comparator.comparing(DailyMixResponse::date));
        return result;
    }
}
