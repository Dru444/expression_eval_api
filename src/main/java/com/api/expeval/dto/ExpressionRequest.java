package com.api.expeval.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpressionRequest {

  @NotBlank(message = "Expression is required.")
  @Size(max = 1000, message = "Expression is too long.")
  private String expression;
}
