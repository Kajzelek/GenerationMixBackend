package org.example.generationmixbackend.dto;

import java.time.LocalDate;
import java.util.Map;

public record DailyMixResponse(
        LocalDate date,
        Map<String, Double> averageGenerationMix,
        Double cleanEnergyPercentage,
) {}
