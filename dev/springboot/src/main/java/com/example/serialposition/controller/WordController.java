package com.example.serialposition.controller;

import com.example.serialposition.service.WordPoolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/words")
public class WordController {

    private final WordPoolService wordPoolService;

    public WordController(WordPoolService wordPoolService) {
        this.wordPoolService = wordPoolService;
    }

    @GetMapping("/random")
    public ResponseEntity<List<String>> getRandomWords(@RequestParam(defaultValue = "10") int count) {
        return ResponseEntity.ok(wordPoolService.getRandomWords(count));
    }
}
