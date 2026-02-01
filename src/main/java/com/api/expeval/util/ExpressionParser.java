
package com.api.expeval.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Stack;
import com.api.expeval.exception.ExpressionEvaluationException;
import com.api.expeval.exception.ExpressionValidationException;

public class ExpressionParser {
  private static final MathContext MATH_CONTEXT = new MathContext(32, RoundingMode.HALF_UP);
  private static final int DIVISION_SCALE = 16;

  public static BigDecimal evaluate(String exp) {
    if (exp == null || exp.trim().isEmpty()) {
      throw new ExpressionValidationException("Expression is blank.");
    }

    Stack<BigDecimal> nums = new Stack<>();
    Stack<Character> ops = new Stack<>();
    TokenType previous = TokenType.NONE;
    int i = 0;
    while (i < exp.length()) {
      char current = exp.charAt(i);
      if (Character.isWhitespace(current)) {
        i++;
        continue;
      }

      if (Character.isDigit(current) || current == '.') {
        i = readNumber(exp, i, nums, false);
        previous = TokenType.NUMBER;
        continue;
      }

      if (current == '+' || current == '-') {
        boolean unary = previous == TokenType.NONE || previous == TokenType.OPERATOR
            || previous == TokenType.LEFT_PAREN;
        if (unary) {
          int nextIndex = i + 1;
          while (nextIndex < exp.length() && Character.isWhitespace(exp.charAt(nextIndex))) {
            nextIndex++;
          }
          if (nextIndex < exp.length() && exp.charAt(nextIndex) == '(') {
            if (current == '-') {
              nums.push(BigDecimal.ZERO);
              ops.push('-');
            }
            previous = TokenType.OPERATOR;
            i = nextIndex;
            continue;
          }
          i = readNumber(exp, nextIndex, nums, current == '-');
          previous = TokenType.NUMBER;
          continue;
        }
      }

      if (isOperator(current)) {
        if (previous != TokenType.NUMBER && previous != TokenType.RIGHT_PAREN) {
          throw new ExpressionValidationException("Operator without left operand at position " + i);
        }
        while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(current)) {
          apply(nums, ops.pop());
        }
        ops.push(current);
        previous = TokenType.OPERATOR;
        i++;
        continue;
      }

      if (current == '(') {
        ops.push(current);
        previous = TokenType.LEFT_PAREN;
        i++;
        continue;
      }

      if (current == ')') {
        if (previous == TokenType.OPERATOR || previous == TokenType.LEFT_PAREN) {
          throw new ExpressionValidationException("Empty parentheses at position " + i);
        }
        while (!ops.isEmpty() && ops.peek() != '(') {
          apply(nums, ops.pop());
        }
        if (ops.isEmpty() || ops.pop() != '(') {
          throw new ExpressionValidationException("Unmatched closing parenthesis at position " + i);
        }
        previous = TokenType.RIGHT_PAREN;
        i++;
        continue;
      }

      throw new ExpressionValidationException("Invalid character '" + current + "' at position " + i);
    }

    if (previous == TokenType.OPERATOR) {
      throw new ExpressionValidationException("Expression ends with an operator.");
    }

    while (!ops.isEmpty()) {
      char op = ops.pop();
      if (op == '(') {
        throw new ExpressionValidationException("Unmatched opening parenthesis.");
      }
      apply(nums, op);
    }

    if (nums.size() != 1) {
      throw new ExpressionValidationException("Malformed expression.");
    }
    return nums.pop();
  }

  private static int readNumber(String exp, int start, Stack<BigDecimal> nums, boolean negative) {
    StringBuilder sb = new StringBuilder();
    int i = start;
    if (negative) {
      sb.append('-');
    }
    boolean hasDot = false;
    boolean hasDigit = false;
    while (i < exp.length()) {
      char current = exp.charAt(i);
      if (Character.isDigit(current)) {
        sb.append(current);
        hasDigit = true;
        i++;
        continue;
      }
      if (current == '.') {
        if (hasDot) {
          throw new ExpressionValidationException("Invalid number format at position " + i);
        }
        hasDot = true;
        sb.append(current);
        i++;
        continue;
      }
      break;
    }

    if (!hasDigit) {
      throw new ExpressionValidationException("Invalid number format at position " + start);
    }

    try {
      nums.push(new BigDecimal(sb.toString(), MATH_CONTEXT));
    } catch (NumberFormatException ex) {
      throw new ExpressionValidationException("Invalid number format.");
    }
    return i;
  }

  private static void apply(Stack<BigDecimal> nums, char op) {
    if (nums.size() < 2) {
      throw new ExpressionValidationException("Operator without enough operands.");
    }
    BigDecimal b = nums.pop();
    BigDecimal a = nums.pop();
    switch (op) {
      case '+':
        nums.push(a.add(b, MATH_CONTEXT));
        break;
      case '-':
        nums.push(a.subtract(b, MATH_CONTEXT));
        break;
      case '*':
        nums.push(a.multiply(b, MATH_CONTEXT));
        break;
      case '^':
        try {
          // BigDecimal.pow() only accepts integer exponents. 
          // For decimal exponents, we need a more complex implementation or limit it to integers.
          // Given the context of a simple expression evaluator, let's try to handle integer exponents first,
          // or use Math.pow for more complex ones, though it loses precision.
          // Let's check if b is an integer.
          if (b.stripTrailingZeros().scale() <= 0) {
            nums.push(a.pow(b.intValueExact(), MATH_CONTEXT));
          } else {
            // For non-integer exponents, we use double-based Math.pow then back to BigDecimal
            double res = Math.pow(a.doubleValue(), b.doubleValue());
            if (Double.isInfinite(res) || Double.isNaN(res)) {
              throw new ExpressionEvaluationException("Power operation resulted in an invalid number.");
            }
            nums.push(new BigDecimal(String.valueOf(res), MATH_CONTEXT));
          }
        } catch (ArithmeticException ex) {
          throw new ExpressionEvaluationException("Error in power operation: " + ex.getMessage());
        }
        break;
      case '/':
        if (b.compareTo(BigDecimal.ZERO) == 0) {
          throw new ExpressionEvaluationException("Division by zero.");
        }
        nums.push(a.divide(b, DIVISION_SCALE, RoundingMode.HALF_UP));
        break;
      case '%':
        if (b.compareTo(BigDecimal.ZERO) == 0) {
          throw new ExpressionEvaluationException("Modulo by zero.");
        }
        nums.push(a.remainder(b, MATH_CONTEXT));
        break;
      default:
        throw new ExpressionValidationException("Unsupported operator: " + op);
    }
  }

  private static boolean isOperator(char op) {
    return op == '+' || op == '-' || op == '*' || op == '/' || op == '^' || op == '%';
  }

  private static int precedence(char op) {
    if (op == '+' || op == '-') {
      return 1;
    }
    if (op == '*' || op == '/' || op == '%') {
      return 2;
    }
    if (op == '^') {
      return 3;
    }
    return 0;
  }

  private enum TokenType {
    NONE,
    NUMBER,
    OPERATOR,
    LEFT_PAREN,
    RIGHT_PAREN
  }
}
