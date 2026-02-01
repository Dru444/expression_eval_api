package com.api.expeval.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.api.expeval.model.ExpressionRecord;
import com.api.expeval.repository.ExpressionRecordRepository;

@Service
public class ExpressionRecordService {
  private final ExpressionRecordRepository repository;

  public ExpressionRecordService(ExpressionRecordRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public void saveRecord(ExpressionRecord record) {
    repository.save(record);
  }

  @Transactional(readOnly = true)
  public List<ExpressionRecord> findByResult(BigDecimal normalizedResult) {
    return repository.findByResult(normalizedResult);
  }
}
