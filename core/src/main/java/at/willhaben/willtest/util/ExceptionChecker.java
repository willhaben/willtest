package at.willhaben.willtest.util;

public class ExceptionChecker {

    public static boolean isAssumptionViolation(Throwable throwable) {
        String exceptionClassName = throwable.getClass().getName();
        return exceptionClassName.equals("org.junit.AssumptionViolatedException") ||
                exceptionClassName.equals("org.opentest4j.TestAbortedException");
    }

    public static boolean isAssertJMultipleFailureError(Throwable throwable) {
        String exceptionClassName = throwable.getClass().getName();
        return exceptionClassName.equals("org.assertj.core.error.AssertJMultipleFailuresError");
    }
}
