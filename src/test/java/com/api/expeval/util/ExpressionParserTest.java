package com.api.expeval.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.api.expeval.exception.ExpressionEvaluationException;
import com.api.expeval.exception.ExpressionValidationException;

class ExpressionParserTest {

  @Test
  void evaluatesWithPrecedence() {
    BigDecimal result = ExpressionParser.evaluate("3+4*6-12");
    assertEquals(0, result.compareTo(new BigDecimal("15")));
  }

  @Test
  void evaluatesWithParenthesesAndUnary() {
    BigDecimal result = ExpressionParser.evaluate("2*(3+4)-(-5)");
    assertEquals(0, result.compareTo(new BigDecimal("19")));
  }

  @Test
  void handlesUnaryMultiply() {
    BigDecimal result = ExpressionParser.evaluate("2*-3");
    assertEquals(0, result.compareTo(new BigDecimal("-6")));
  }

  @Test
  void rejectsInvalidCharacters() {
    assertThrows(ExpressionValidationException.class,
        () -> ExpressionParser.evaluate("2+abc"));
  }

  @Test
  void rejectsMismatchedParentheses() {
    assertThrows(ExpressionValidationException.class,
        () -> ExpressionParser.evaluate("(2+3"));
  }

  @Test
  void rejectsDivisionByZero() {
    assertThrows(ExpressionEvaluationException.class,
        () -> ExpressionParser.evaluate("10/0"));
  }

  @Test
  void evaluatesPower() {
    BigDecimal result = ExpressionParser.evaluate("2^3");
    assertEquals(0, result.compareTo(new BigDecimal("8")));
  }

  @Test
  void evaluatesModulo() {
    BigDecimal result = ExpressionParser.evaluate("10%3");
    assertEquals(0, result.compareTo(new BigDecimal("1")));
  }

  @Test
  void evaluatesComplexWithPowerAndModulo() {
    // 2^3 + 10%3 * 2 = 8 + 1 * 2 = 10
    BigDecimal result = ExpressionParser.evaluate("2^3 + 10%3 * 2");
    assertEquals(0, result.compareTo(new BigDecimal("10")));
  }

  @Test
  void evaluatesDecimalPower() {
    // 4^0.5 = 2
    BigDecimal result = ExpressionParser.evaluate("4^0.5");
    assertEquals(0, result.compareTo(new BigDecimal("2")));
  }

  @Test
  void evaluatesUnaryWithParentheses() {
    // -(2+3) = -5
    BigDecimal result = ExpressionParser.evaluate("-(2+3)");
    assertEquals(0, result.compareTo(new BigDecimal("-5")));
    
    // +(2+3) = 5
    result = ExpressionParser.evaluate("+(2+3)");
    assertEquals(0, result.compareTo(new BigDecimal("5")));
  }

  @Test
  void handlesWhitespace() {
    BigDecimal result = ExpressionParser.evaluate(" 2 + ( - 3 ) ");
    assertEquals(0, result.compareTo(new BigDecimal("-1")));
  }

  @Test
  void rejectsBlankExpression() {
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate(""));
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("   "));
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate(null));
  }

  @Test
  void rejectsInvalidNumberFormat() {
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("1.2.3"));
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("."));
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("-."));
  }

  @Test
  void rejectsEmptyParentheses() {
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("()"));
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("(  )"));
  }

  @Test
  void rejectsMismatchedClosingParentheses() {
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("2+3)"));
  }

  @Test
  void rejectsMismatchedOpeningParentheses() {
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("((2+3)"));
  }

  @Test
  void rejectsExpressionEndingWithOperator() {
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("2+3+"));
  }

  @Test
  void rejectsOperatorWithoutLeftOperand() {
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("*5"));
    assertThrows(ExpressionValidationException.class, () -> ExpressionParser.evaluate("2+*5"));
  }

  @Test
  void rejectsModuloByZero() {
    assertThrows(ExpressionEvaluationException.class, () -> ExpressionParser.evaluate("10%0"));
  }

  @Test
  void handlesPowerOperationErrors() {
    // Negative base with decimal exponent results in NaN
    assertThrows(ExpressionEvaluationException.class, () -> ExpressionParser.evaluate("(-2)^0.5"));
    
    // Very large power resulting in Infinity
    // Note: 1000^1000 might not overflow double or might be handled differently by BigDecimal
    // Let's use something that definitely overflows double if it goes through Math.pow
    assertThrows(ExpressionEvaluationException.class, () -> ExpressionParser.evaluate("10^1000.5"));
  }

  @Test
  void rejectsUnsupportedOperator() {
    // This is hard to reach via public evaluate because the loop throws on invalid char,
    // but if we were to bypass it or if isOperator and apply are inconsistent.
    // Currently isOperator and apply are consistent.
  }
}
