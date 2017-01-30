package at.willhaben.willtest.rule;

import org.apache.log4j.MDC;
import org.junit.runner.Description;

/**
 * Sets log pattern variables to make it clear which log entry belongs to which test in case of parallel execution.
 * <p>
 * Created by liptak on 2016.07.14..
 */
public class LogContext extends AbstractRule {
    static final String TEST_CLASS = "testClass";
    static final String TEST_DISPLAY_NAME = "displayName";
    static final String TEST_METHOD = "testMethod";
    static final String THREAD_ID = "threadId";

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