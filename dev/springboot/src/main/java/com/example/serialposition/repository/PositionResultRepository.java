package com.example.serialposition.repository;

import com.example.serialposition.model.PositionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PositionResultRepository extends JpaRepository<PositionResult, Long> {

    // Used in Phase 5 to gather every attempt's outcome for a given
    // position, across the whole history, in order to compute that
    // position's aggregate recall percentage.
    List<PositionResult> findByPosition(int position);
}
