package at.willhaben.willtest.util;

public class AssumptionUtil {

    public static boolean isAssumptionViolation(Throwable throwable) {
        String exceptionClassName = throwable.getClass().getName();
        return exceptionClassName.equals("org.junit.AssumptionViolatedException") ||
                exceptionClassName.equals("org.opentest4j.TestAbortedException");
    }
}
