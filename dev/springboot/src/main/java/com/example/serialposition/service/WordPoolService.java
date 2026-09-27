package com.example.serialposition.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The full pool of words a test can be built from. GET /api/words/random
 * hands the frontend a fresh, shuffled selection every time a test
 * starts, instead of the same fixed 10 words every attempt.
 *
 * Every word here must also have an entry in SemanticAssociationService's
 * WORD_ASSOCIATIONS map — the two lists are kept separate but must stay
 * in sync; add a word to both if you expand the pool.
 */
@Service
public class WordPoolService {

    private static final List<String> WORD_POOL = List.of(
            "Apple", "Table", "River", "Phone", "Cloud",
            "House", "Green", "Tiger", "Book", "Star",
            "Chair", "Dog", "Mountain", "Ocean", "Bread",
            "Clock", "Flower", "Train", "Moon", "Fire",
            "Snow", "Guitar", "Bridge", "Candle", "Shoe"
    );

    public List<String> getRandomWords(int count) {
        int clamped = Math.max(1, Math.min(count, WORD_POOL.size()));
        List<String> shuffled = new ArrayList<>(WORD_POOL);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, clamped);
    }

    public int getPoolSize() {
        return WORD_POOL.size();
    }
}
