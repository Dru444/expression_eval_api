package com.api.expeval.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExpressionResponse {

  private Long id;
  private String expression;
  private BigDecimal result;
  private String status;
  private String errorMessage;
  private Instant createdAt;
}
