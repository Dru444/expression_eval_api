package com.api.expeval.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.api.expeval.exception.ExpressionEvaluationException;
import com.api.expeval.exception.ExpressionValidationException;

@DisplayName("ExpressionParser massive test suite")
class ExpressionParserBulkTest {

    @ParameterizedTest(name = "Test case {index}: {0} = {1}")
    @MethodSource("provideValidExpressions")
    void testValidExpressions(String expression, String expectedResult) {
        BigDecimal result = ExpressionParser.evaluate(expression);
        BigDecimal expected = new BigDecimal(expectedResult);
        assertEquals(0, result.compareTo(expected), 
            "Expression: " + expression + " failed. Expected: " + expected.toPlainString() + ", but got: " + result.toPlainString());
    }

    @ParameterizedTest(name = "Test case {index}: {0} should throw {1}")
    @MethodSource("provideInvalidExpressions")
    void testInvalidExpressions(String expression, Class<? extends Throwable> expectedException) {
        assertThrows(expectedException, () -> ExpressionParser.evaluate(expression),
            "Expression: " + expression + " should have thrown " + expectedException.getSimpleName());
    }

    private static Stream<Arguments> provideValidExpressions() {
        return Stream.of(
            // Basic Arithmetic
            Arguments.of("1+1", "2"),
            Arguments.of("10-5", "5"),
            Arguments.of("4*3", "12"),
            Arguments.of("20/4", "5.0000000000"),
            Arguments.of("15%4", "3"),
            Arguments.of("2^3", "8"),
            Arguments.of("1.5+2.5", "4.0"),
            Arguments.of("10/3", "3.3333333333333333"),
            Arguments.of("0+0", "0"),
            Arguments.of("0-5", "-5"),

            // Precedence
            Arguments.of("1+2*3", "7"),
            Arguments.of("(1+2)*3", "9"),
            Arguments.of("10-2^3", "2"),
            Arguments.of("(10-2)^3", "512"),
            Arguments.of("2+3*4^2", "50"),
            Arguments.of("(2+3)*4^2", "80"),
            Arguments.of("100/10/2", "5.0000000000"),
            Arguments.of("100/(10/2)", "20.0000000000"),
            Arguments.of("10%3*2", "2"),
            Arguments.of("10%(3*2)", "4"),

            // Unary Operators
            Arguments.of("-5+3", "-2"),
            Arguments.of("5+(-3)", "2"),
            Arguments.of("-5-(-3)", "-2"),
            Arguments.of("+5+3", "8"),
            Arguments.of("-(5+3)", "-8"),
            Arguments.of("-(-5)", "5"),
            Arguments.of("2*-3", "-6"),
            Arguments.of("-2^-2", "0.25"),
            Arguments.of("(-2)^2", "4"),
            Arguments.of("2^-1", "0.5"),

            // Decimals
            Arguments.of("0.1+0.2", "0.3"),
            Arguments.of("1.1*1.1", "1.21"),
            Arguments.of("1/0.5", "2.0000000000"),
            Arguments.of("4^0.5", "2"),
            Arguments.of("2^1.5", "2.8284271247461903"),
            Arguments.of("0.5%0.2", "0.1"),
            Arguments.of("1.23456789+9.87654321", "11.1111111"),
            Arguments.of("10.0/2.0", "5.0000000000"),
            Arguments.of("-1.5*2", "-3.0"),
            Arguments.of("2.5^2", "6.25"),

            // Nested Parentheses
            Arguments.of("((1+2))", "3"),
            Arguments.of("((1+2)*(3+4))", "21"),
            Arguments.of("1+(2+(3+(4+5)))", "15"),
            Arguments.of("(((10-2)*3)/4)^2", "36.0000000000"),
            Arguments.of("(1+2)*(3+(4-1))", "18"),
            Arguments.of("100/(2*(3+2))", "10.0000000000"),
            Arguments.of("((5%2)*10)^2", "100"),
            Arguments.of("2^(1+(1+1))", "8"),
            Arguments.of("-(1-(1-1))", "-1"),
            Arguments.of("(1+2)*(2+1)", "9"),

            // Complex Mixed
            Arguments.of("2+3*4-5/2", "11.5000000000"),
            Arguments.of("10^2 % 7 * 3", "6"),
            Arguments.of("(2^3 + 4^2) / 2", "12.0000000000"),
            Arguments.of("-1 + 2 * -3 / 4", "-2.5000000000"),
            Arguments.of("100 * (1 + 0.05)^2", "110.25"),
            Arguments.of("1.5 * (2 + 3) / 0.5", "15.0000000000"),
            Arguments.of("10 - (2 + 3) * (4 - 6)", "20"),
            Arguments.of("2^2^3", "64"),
            Arguments.of("2.5%1.2 + 0.1", "0.2"),
            Arguments.of("(1+1)^(1+1)", "4"),

            // Large and Small Numbers
            Arguments.of("1000000*1000000", "1000000000000"),
            Arguments.of("0.0000000001+0.0000000001", "0.0000000002"),
            Arguments.of("10^10", "10000000000"),
            Arguments.of("1/1000000", "0.0000010000"),
            Arguments.of("-1000000000-1000000000", "-2000000000"),
            Arguments.of("10^2", "100"),
            Arguments.of("0.15", "0.15"),
            Arguments.of("999999999+1", "1000000000"),
            Arguments.of("0.0000000001*0.1", "0.00000000001"),
            Arguments.of("1/3", "0.3333333333333333"),

            // Whitespace and Formatting
            Arguments.of("\t1\n+\r1 ", "2"),
            Arguments.of("( 1 + ( 2 * 3 ) )", "7"),
            Arguments.of("10 / 2", "5.0000000000"),
            Arguments.of("5 % 2", "1"),
            Arguments.of("- 5", "-5"),
            Arguments.of(" ( - 5 ) ", "-5"),

            // Additional Cases
            Arguments.of("10+20-30+40", "40"),
            Arguments.of("2*3*4*5", "120"),
            Arguments.of("2+2+2+2+2", "10"),
            Arguments.of("1.1+2.2+3.3", "6.6"),
            Arguments.of("((1+1)+(1+1))", "4"),
            Arguments.of("100-10-10-10", "70"),
            Arguments.of("1*1*1*1", "1"),
            Arguments.of("0*123", "0"),
            Arguments.of("0/123", "0.0000000000"),
            Arguments.of("100%10", "0"),
            Arguments.of("1^100", "1"),
            Arguments.of("10^0", "1"),
            Arguments.of("0^10", "0"),
            Arguments.of("-(+5)", "-5"),
            Arguments.of("+(-5)", "-5"),
            Arguments.of("2*(3+(4*(5+6)))", "94"),
            Arguments.of("1.2*2.3", "2.76"),
            Arguments.of("10/2.5", "4.0000000000"),
            Arguments.of("100%3", "1"),
            Arguments.of("2^3^2", "64"),
            Arguments.of("1++1", "2")
        );
    }

    private static Stream<Arguments> provideInvalidExpressions() {
        return Stream.of(
            // Syntax Errors (101-115)
            Arguments.of("1+*1", ExpressionValidationException.class),
            Arguments.of("1+/", ExpressionValidationException.class),
            Arguments.of("(1+2", ExpressionValidationException.class),
            Arguments.of("1+2)", ExpressionValidationException.class),
            Arguments.of("()", ExpressionValidationException.class),
            Arguments.of("+", ExpressionValidationException.class),
            Arguments.of("*1", ExpressionValidationException.class),

            // More Syntax Errors (91-95)
            Arguments.of("1.2.3", ExpressionValidationException.class),
            Arguments.of("abc", ExpressionValidationException.class),
            Arguments.of("1 + @", ExpressionValidationException.class),
            Arguments.of("2^3^", ExpressionValidationException.class),
            Arguments.of(" ", ExpressionValidationException.class),

            // Evaluation Errors (96-100)
            Arguments.of("10/0", ExpressionEvaluationException.class),
            Arguments.of("10%0", ExpressionEvaluationException.class),
            Arguments.of("(-4)^0.5", ExpressionEvaluationException.class),
            Arguments.of("10^1000.5", ExpressionEvaluationException.class),
            Arguments.of("0/0", ExpressionEvaluationException.class)
        );
    }
}
