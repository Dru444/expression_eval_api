
package com.api.expeval.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.api.expeval.dto.ExpressionResponse;
import com.api.expeval.exception.ExpressionValidationException;
import com.api.expeval.model.ExpressionRecord;
import com.api.expeval.model.ExpressionStatus;
import com.api.expeval.util.ExpressionParser;

@Service
public class ExpressionService {
  public static final int RESULT_SCALE = 16;

  private static final Logger log = LoggerFactory.getLogger(ExpressionService.class);
  private final ExpressionRecordService recordService;

  public ExpressionService(ExpressionRecordService recordService) {
    this.recordService = recordService;
  }

  public ExpressionResponse evaluate(String exp) {
    ExpressionRecord record = new ExpressionRecord();
    record.setExpression(exp == null ? "" : exp);
    record.setStatus(ExpressionStatus.SUCCESS);
    try {
      BigDecimal result = ExpressionParser.evaluate(exp);
      BigDecimal normalized = normalizeResult(result);
      record.setResult(normalized);
      recordService.saveRecord(record);
      log.info("Expression evaluated successfully: {} | Result: {}", exp, normalized);
      return toResponse(record, normalized.stripTrailingZeros());
    } catch (RuntimeException ex) {
      record.setStatus(ExpressionStatus.ERROR);
      record.setErrorMessage(ex.getMessage());
      recordService.saveRecord(record);
      log.warn("Expression evaluation failed: {}", exp, ex);
      throw ex;
    }
  }

  public List<ExpressionResponse> findByResult(BigDecimal value) {
    if (value == null) {
      throw new ExpressionValidationException("Result value is required.");
    }
    BigDecimal normalized = normalizeResult(value);
    return recordService.findByResult(normalized).stream()
        .map(record -> toResponse(record, record.getResult().stripTrailingZeros()))
        .collect(Collectors.toList());
  }

  private ExpressionResponse toResponse(ExpressionRecord record, BigDecimal result) {
    return ExpressionResponse.builder()
        .id(record.getId())
        .expression(record.getExpression())
        .result(result)
        .status(record.getStatus().name())
        .errorMessage(record.getErrorMessage())
        .createdAt(record.getCreatedAt())
        .build();
  }

  private BigDecimal normalizeResult(BigDecimal result) {
    return result.setScale(RESULT_SCALE, RoundingMode.HALF_UP);
  }
}
