package org.example.generationmixbackend.dto;

import java.util.List;

public record CarbonIntensityResponse(
        List<GenerationData> data
) {}
