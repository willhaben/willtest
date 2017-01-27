package at.willhaben.willtest.rule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Similar to {@link org.junit.rules.ExternalResource}, but gives possibility to do something in case of errors.
 * Also if there is a test error, this class takes care, that errors from its implementations are added as
 * suppressed exceptions to this error to avoid loosing that original problem.
 * Created by liptak on 2016.08.24..
 */
public abstract class AbstractRule implements TestRule {
    protected void before(Description description) throws Throwable {
    }

    protected void after(Description description, Throwable testFailure) throws Throwable {
    }

    protected void onError(Description description, Throwable testFailure) throws Throwable {
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new SafeStatement(description, base);
    }

    private class SafeStatement extends Statement {
        private final Description description;
        private final Statement base;

        SafeStatement(Description description, Statement base) {
            this.description = description;
            this.base = base;
        }

        @Override
        public void evaluate() throws Throwable {
            Throwable testFailure = null;
            try {
                before(description);
                base.evaluate();
            } catch (Throwable th) {
                testFailure = th;
                try {
                    onError(description, th);
                } catch (Throwable errorHappenedInOnError) {
                    th.addSuppressed(errorHappenedInOnError);
                }
                throw th;
            } finally {
                try {
                    after(description, testFailure);
                } catch (Throwable errorHappenedInAfter) {
                    if (testFailure != null) {
                        testFailure.addSuppressed(errorHappenedInAfter);
                    } else {
                        throw errorHappenedInAfter;
                    }
                }
            }
        }
    }
}
