package com.api.expeval.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.api.expeval.model.ExpressionRecord;
import com.api.expeval.model.ExpressionStatus;
import com.api.expeval.repository.ExpressionRecordRepository;
import com.api.expeval.exception.ExpressionValidationException;

@SpringBootTest
class ExpressionServiceIntegrationTest {

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private ExpressionRecordRepository repository;

    @Test
    void testErrorIsPersistedWhenEvaluationFails() {
        String invalidExp = "10 / 0";
        
        // Ensure division by zero throws an exception
        assertThrows(RuntimeException.class, () -> {
            expressionService.evaluate(invalidExp);
        });

        // Check if the record was saved in the database despite the exception
        List<ExpressionRecord> records = repository.findAll();
        boolean foundErrorRecord = records.stream()
                .anyMatch(r -> r.getExpression().equals(invalidExp) && r.getStatus() == ExpressionStatus.ERROR);
        
        assertTrue(foundErrorRecord, "Error record should be persisted in the database");
    }

    @Test
    void testValidationErrorIsPersisted() {
        String malformedExp = "2 + (3";
        
        assertThrows(ExpressionValidationException.class, () -> {
            expressionService.evaluate(malformedExp);
        });

        List<ExpressionRecord> records = repository.findAll();
        boolean foundErrorRecord = records.stream()
                .anyMatch(r -> r.getExpression().equals(malformedExp) && r.getStatus() == ExpressionStatus.ERROR);
        
        assertTrue(foundErrorRecord, "Validation error record should be persisted in the database");
    }

    @Test
    void testFindByResultNull() {
        assertThrows(ExpressionValidationException.class, () -> {
            expressionService.findByResult(null);
        });
    }

    @Test
    void testEvaluateBlank() {
        assertThrows(ExpressionValidationException.class, () -> {
            expressionService.evaluate("");
        });
    }

    @Test
    void testPrePersist() {
        ExpressionRecord record = new ExpressionRecord();
        // Since we can't easily trigger JPA PrePersist without saving, 
        // and we already have tests that save records and implicitly verify this (via SUCCESS status etc),
        // we can add a direct call or rely on repository.save.
        record.setExpression("1+1");
        record.setStatus(ExpressionStatus.SUCCESS);
        ExpressionRecord saved = repository.save(record);
        assertNotNull(saved.getCreatedAt());
    }
}
