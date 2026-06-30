package org.example.generationmixbackend.dto;

import java.time.ZonedDateTime;

public record OptimalWindowResponse(
        ZonedDateTime start,
        ZonedDateTime end,
        Double averageCleanEnergyPercentage
) {}