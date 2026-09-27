package com.example.serialposition.controller;

import com.example.serialposition.dto.AggregateStatsDto;
import com.example.serialposition.dto.RecallRequestDto;
import com.example.serialposition.dto.RecallResultDto;
import com.example.serialposition.service.AggregationService;
import com.example.serialposition.service.RecallService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RecallController {

    private final RecallService recallService;
    private final AggregationService aggregationService;

    public RecallController(RecallService recallService, AggregationService aggregationService) {
        this.recallService = recallService;
        this.aggregationService = aggregationService;
    }

    @PostMapping("/recall")
    public ResponseEntity<RecallResultDto> submitRecall(@Valid @RequestBody RecallRequestDto request) {
        RecallResultDto result = recallService.scoreAttempt(request);
        return ResponseEntity.ok(result);
    }

    // Aggregates every stored attempt into a per-position recall
    // percentage — this is what the Phase 6 Chart.js graph will plot.
    @GetMapping("/recall/aggregate")
    public ResponseEntity<AggregateStatsDto> getAggregateStats() {
        return ResponseEntity.ok(aggregationService.getAggregateStats());
    }
}
