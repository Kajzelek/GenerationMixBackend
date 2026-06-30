package org.example.generationmixbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.generationmixbackend.dto.DailyMixResponse;
import org.example.generationmixbackend.dto.OptimalWindowResponse;
import org.example.generationmixbackend.service.EnergyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/energy")
@CrossOrigin(origins = "*")
@Tag(name = "Energy API", description = "Endpoint do analizy miksu energetycznego National Grid UK")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/daily-mix")
    @Operation(
            summary = "Pobierz średni miks energetyczny na 3 dni",
            description = "Zwraca zestawienie procentowego udziału źródeł energii oraz ogólny wskaźnik czystej energii dla dnia dzisiejszego i dwóch kolejnych."
    )
    public ResponseEntity<List<DailyMixResponse>> getDailyMix() {
        return ResponseEntity.ok(energyService.getThreeDayPrediction());
    }

    @GetMapping("/optimal-window")
    @Operation(
            summary = "Znajdź optymalne okno ładowania auta elektrycznego",
            description = "Używa algorytmu okna przesuwnego, aby wskazać przedział czasowy o zadanej długości z najwyższym udziałem ekologicznej energii."
    )
    public ResponseEntity<OptimalWindowResponse> getOptimalWindow(@RequestParam(defaultValue = "3") int hours) {
        try {
            OptimalWindowResponse response = energyService.getOptimalWindow(hours);
            if (response == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
