package at.willhaben.willtest.rule;

import org.junit.runner.Description;
import org.slf4j.MDC;

/**
 * Sets log pattern variables to make it clear which log entry belongs to which test in case of parallel execution.
 * <p>
 */
public class LogContext extends AbstractRule {
    public static final String TEST_CLASS = "testClass";
    public static final String TEST_DISPLAY_NAME = "displayName";
    public static final String TEST_METHOD = "testMethod";
    public static final String THREAD_ID = "threadId";

    @Override
    public void before(Description description) throws Throwable {
        super.before(description);
        MDC.put(TEST_CLASS, description.getClassName());
        MDC.put(TEST_DISPLAY_NAME, description.getDisplayName());
        MDC.put(TEST_METHOD, description.getMethodName());
        MDC.put(THREAD_ID, Long.toString(Thread.currentThread().getId(), 32));
    }

    @Override
    public void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        MDC.remove(TEST_CLASS);
        MDC.remove(TEST_DISPLAY_NAME);
        MDC.remove(TEST_METHOD);
    }
}
