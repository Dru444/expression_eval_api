
package com.api.expeval.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.api.expeval.dto.ExpressionRequest;
import com.api.expeval.dto.ExpressionResponse;
import com.api.expeval.service.ExpressionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/expressions")
@Validated
@Tag(name = "Expression Evaluation", description = "Operations related to expression evaluation")
public class ExpressionController {
  private static final Logger log = LoggerFactory.getLogger(ExpressionController.class);
  private final ExpressionService service;

  public ExpressionController(ExpressionService service) {
    this.service = service;
  }

  @PostMapping("/calculate")
  @Operation(summary = "Evaluate a mathematical expression", description = "Parses and calculates the result of a given expression.")
  @ApiResponse(responseCode = "200", description = "Expression successfully evaluated")
  @ApiResponse(responseCode = "400", description = "Invalid expression or request")
  public ExpressionResponse calculate(@Valid @RequestBody ExpressionRequest request) {
    log.info("Calculation request received");
    return service.evaluate(request.getExpression());
  }

  @GetMapping("/find-by-result")
  @Operation(summary = "Find previous evaluations by result value", description = "Retrieves a list of expressions that evaluated to the specified value.")
  @ApiResponse(responseCode = "200", description = "List of matching expressions")
  public List<ExpressionResponse> findByResult(@NotNull @RequestParam("value") BigDecimal value) {
    return service.findByResult(value);
  }
}
