package com.example.serialposition.service;

import com.example.serialposition.dto.AttemptDetailDto;
import com.example.serialposition.dto.AttemptSummaryDto;
import com.example.serialposition.dto.PositionResultDto;
import com.example.serialposition.model.PositionResult;
import com.example.serialposition.model.TestAttempt;
import com.example.serialposition.repository.PositionResultRepository;
import com.example.serialposition.repository.TestAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final TestAttemptRepository testAttemptRepository;
    private final PositionResultRepository positionResultRepository;

    public HistoryService(TestAttemptRepository testAttemptRepository,
                           PositionResultRepository positionResultRepository) {
        this.testAttemptRepository = testAttemptRepository;
        this.positionResultRepository = positionResultRepository;
    }

    public List<AttemptSummaryDto> getHistory() {
        return testAttemptRepository.findAllByOrderByTimestampDesc().stream()
                .map(a -> new AttemptSummaryDto(
                        a.getId(), a.getTimestamp(), a.getTotalWords(),
                        a.getTotalRecalled(), a.getRecallPercentage()))
                .collect(Collectors.toList());
    }

    public Optional<AttemptDetailDto> getAttemptDetail(Long id) {
        return testAttemptRepository.findById(id).map(attempt -> {
            List<PositionResultDto> positions = attempt.getPositionResults().stream()
                    .sorted(Comparator.comparingInt(PositionResult::getPosition))
                    .map(pr -> new PositionResultDto(pr.getPosition(), pr.getWord(), pr.isRecalled()))
                    .collect(Collectors.toList());
            return new AttemptDetailDto(
                    attempt.getId(), attempt.getTimestamp(), attempt.getTotalWords(),
                    attempt.getTotalRecalled(), attempt.getRecallPercentage(), positions);
        });
    }

    // Wipes every stored attempt. Position results are deleted first since
    // they hold the foreign key to test_attempt.
    @Transactional
    public void clearHistory() {
        positionResultRepository.deleteAllInBatch();
        testAttemptRepository.deleteAllInBatch();
    }
}
