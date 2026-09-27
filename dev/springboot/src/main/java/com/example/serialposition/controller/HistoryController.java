package com.example.serialposition.controller;

import com.example.serialposition.dto.AttemptDetailDto;
import com.example.serialposition.dto.AttemptSummaryDto;
import com.example.serialposition.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recall/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<List<AttemptSummaryDto>> getHistory() {
        return ResponseEntity.ok(historyService.getHistory());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttemptDetailDto> getAttemptDetail(@PathVariable Long id) {
        return historyService.getAttemptDetail(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Clears all stored history — backs the "Clear all data" button in
    // the frontend's Settings panel.
    @DeleteMapping
    public ResponseEntity<Void> clearHistory() {
        historyService.clearHistory();
        return ResponseEntity.noContent().build();
    }
}
