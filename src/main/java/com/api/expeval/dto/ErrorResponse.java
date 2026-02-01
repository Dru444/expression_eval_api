package com.api.expeval.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

  private Instant timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
}
