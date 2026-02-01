package com.api.expeval;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All Tests Suite")
@SelectPackages("com.api.expeval")
class ExpressionEvalApiApplicationTest {
}
