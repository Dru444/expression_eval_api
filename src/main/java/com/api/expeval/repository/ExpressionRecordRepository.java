package com.api.expeval.repository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.api.expeval.model.ExpressionRecord;

public interface ExpressionRecordRepository extends JpaRepository<ExpressionRecord, Long> {
  List<ExpressionRecord> findByResult(BigDecimal result);
}
