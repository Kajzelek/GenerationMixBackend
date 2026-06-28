package org.example.generationmixbackend.dto;

import java.util.List;
import java.time.ZonedDateTime;

public record GenerationData(
        ZonedDateTime from,
        ZonedDateTime to,
        List<FuelMix> generationmix) {}

