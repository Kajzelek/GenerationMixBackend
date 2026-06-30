package org.example.generationmixbackend.service;

import org.example.generationmixbackend.client.CarbonIntensityClient;
import org.example.generationmixbackend.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class EnergyServiceTest {

    private CarbonIntensityClient carbonIntensityClient;
    private EnergyService energyService;

    @BeforeEach
    void setUp() {
        carbonIntensityClient = Mockito.mock(CarbonIntensityClient.class);
        energyService = new EnergyService(carbonIntensityClient);
    }


    @Test
    void shouldFindOptimalWindowCorrectly() {
        ZonedDateTime now = ZonedDateTime.parse("2026-06-29T00:00:00Z");

        GenerationData g1 = new GenerationData(now, now.plusMinutes(30), List.of(new FuelMix("wind", 10.0)));
        GenerationData g2 = new GenerationData(now.plusMinutes(30), now.plusMinutes(60), List.of(new FuelMix("solar", 80.0)));
        GenerationData g3 = new GenerationData(now.plusMinutes(60), now.plusMinutes(90), List.of(new FuelMix("wind", 90.0)));
        GenerationData g4 = new GenerationData(now.plusMinutes(90), now.plusMinutes(120), List.of(new FuelMix("coal", 100.0), new FuelMix("hydro", 20.0)));

        CarbonIntensityResponse mockResponse = new CarbonIntensityResponse(List.of(g1, g2, g3, g4));
        Mockito.when(carbonIntensityClient.getCarbonIntensity(any(), any())).thenReturn(mockResponse);

        OptimalWindowResponse result = energyService.getOptimalWindow(1);

        assertNotNull(result);
        assertEquals(now.plusMinutes(30), result.start());
        assertEquals(now.plusMinutes(90), result.end());
        assertEquals(85.0, result.averageCleanEnergyPercentage());
    }

    @Test
    void shouldThrowExceptionWhenHoursAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> energyService.getOptimalWindow(0));
        assertThrows(IllegalArgumentException.class, () -> energyService.getOptimalWindow(7));
    }

    @Test
    void shouldAggregateDailyMixAndCalculateCleanEnergyCorrectly(){
        LocalDate today = LocalDate.now(java.time.ZoneOffset.UTC);
        LocalDate tomorrow = today.plusDays(1);

        ZonedDateTime day1_int1 = today.atTime(10, 0, 0).atZone(java.time.ZoneOffset.UTC);
        ZonedDateTime day1_int2 = today.atTime(10, 30, 0).atZone(java.time.ZoneOffset.UTC);
        ZonedDateTime day2_int1 = tomorrow.atTime(15, 0, 0).atZone(java.time.ZoneOffset.UTC);

        GenerationData g1 = new GenerationData(day1_int1, day1_int1.plusMinutes(30), List.of(
                new FuelMix("biomass", 10.0),
                new FuelMix("gas", 40.0)
        ));

        GenerationData g2 = new GenerationData(day1_int2, day1_int2.plusMinutes(30), List.of(
                new FuelMix("biomass", 20.0),
                new FuelMix("gas", 50.0)
        ));

        GenerationData g3 = new GenerationData(day2_int1, day2_int1.plusMinutes(30), List.of(
                new FuelMix("wind", 30.0),
                new FuelMix("nuclear", 20.0)
        ));

        CarbonIntensityResponse mockResponse = new CarbonIntensityResponse(List.of(g1, g2, g3));
        Mockito.when(carbonIntensityClient.getCarbonIntensity(any(), any())).thenReturn(mockResponse);

        List<DailyMixResponse> result = energyService.getThreeDayPrediction();

        assertNotNull(result);
        assertEquals(2, result.size());

        DailyMixResponse firstDay = result.get(0);
        assertEquals(today, firstDay.date());
        assertEquals(15.0, firstDay.averageGenerationMix().get("biomass"));
        assertEquals(45.0, firstDay.averageGenerationMix().get("gas"));
        assertEquals(15.0, firstDay.cleanEnergyPercentage());

        DailyMixResponse secondDay = result.get(1);
        assertEquals(tomorrow, secondDay.date());
        assertEquals(30.0, secondDay.averageGenerationMix().get("wind"));
        assertEquals(20.0, secondDay.averageGenerationMix().get("nuclear"));
        assertEquals(50.0, secondDay.cleanEnergyPercentage());
    }

    @Test
    void shouldReturnEmptyListWhenApiResponseIsEmpty() {
        Mockito.when(carbonIntensityClient.getCarbonIntensity(any(), any())).thenReturn(new CarbonIntensityResponse(null));

        List<DailyMixResponse> result = energyService.getThreeDayPrediction();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
