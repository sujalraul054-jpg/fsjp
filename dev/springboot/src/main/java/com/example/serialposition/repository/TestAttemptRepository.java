package com.example.serialposition.repository;

import com.example.serialposition.model.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

    // Used by the history screen — newest attempts first.
    List<TestAttempt> findAllByOrderByTimestampDesc();
}
