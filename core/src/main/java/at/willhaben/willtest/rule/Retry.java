package at.willhaben.willtest.rule;

import org.hamcrest.Matcher;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This rule gives you the possibility to retry a test if the exception matches a given {@link Matcher}.
 * It is possible to change the maximum of retries, default configuration is {@value #DEFAULT_MAXIMAL_TRIALS}.
 */
@Deprecated
public class Retry implements TestRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverLog.class);
    private static final int DEFAULT_MAXIMAL_TRIALS = 3;

    private final int maximalTrials;
    private final Matcher<? extends Throwable> matcher;

    /**
     * Retries the test if the given matcher matches the exception thrown. Maximum number of retries is
     * {@value #DEFAULT_MAXIMAL_TRIALS}
     * @param matcher if the matcher matches the exception the test will be retried
     */
    public Retry(Matcher<? extends Throwable> matcher) {
        this(matcher, DEFAULT_MAXIMAL_TRIALS);
    }

    /**
     * Retries the test if the given matcher matches the exception thrown. Maximum number of retries is
     * {@value #DEFAULT_MAXIMAL_TRIALS}
     * @param matcher if the matcher matches the exception the test will be retried
     * @param maximalTrials no of maximal trials
     */
    public Retry(Matcher<? extends Throwable> matcher, int maximalTrials) {
        this.matcher = matcher;
        this.maximalTrials = maximalTrials;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new RetryingStatement(base);
    }

    private class RetryingStatement extends Statement {
        private final Statement wrappedStatement;

        private RetryingStatement(Statement wrappedStatement) {
            this.wrappedStatement = wrappedStatement;
        }

        @Override
        public void evaluate() throws Throwable {
            int retryCount = 0;
            boolean retry;
            do {
                try {
                    wrappedStatement.evaluate();
                    retry = false;
                } catch (Throwable throwable) {
                    retry = matcher.matches(throwable);
                    if(retryCount < maximalTrials-1 && retry) {
                        retryCount++;
                        LOGGER.info("Recoverable exception was thrown during the test. Retrying ("
                                 + retryCount + "/" + maximalTrials + ")...", throwable);
                    } else {
                        throw throwable;
                    }
                }
            } while(retry);
        }
    }
}
