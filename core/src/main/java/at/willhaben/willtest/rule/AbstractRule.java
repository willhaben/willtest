package at.willhaben.willtest.rule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Similar to {@link org.junit.rules.ExternalResource}, but gives possibility to do something in case of errors.
 * Also if there is a test error, this class takes care, that errors from its implementations are added as
 * suppressed exceptions to the test error to avoid loosing that original problem. If the test was successful, but
 * {@link #after(Description, Throwable)} throws an error, then the test will fail.
 * Created by liptak on 2016.08.24..
 */
public abstract class AbstractRule implements TestRule {
    /**
     * Can do something before ein test. By default it does nothing.
     *
     * @param description
     * @throws Throwable
     */
    protected void before(Description description) throws Throwable {
    }

    /**
     * Called after all tests always. By default it does nothing.
     *
     * @param description
     * @param testFailure in case of failed test it contains an exception. Otherwise null
     * @throws Throwable if <code>testFailure</code> is null, it will fly, if <code>testFailure</code> is not null,
     *                   it will be added to that as suppressed exception. See {@link Throwable#addSuppressed(Throwable)}
     */
    protected void after(Description description, Throwable testFailure) throws Throwable {
    }

    /**
     * Called if the test has thrown an error. By default it does nothing.
     *
     * @param description
     * @param testFailure
     * @throws Throwable this will be added to <code>testFailure</code> as suppressed exception.
     *                   See {@link Throwable#addSuppressed(Throwable)}
     */
    protected void onError(Description description, Throwable testFailure) throws Throwable {
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new SafeStatement(description, base);
    }

    /**
     * {@link Statement} implementation, which takes care of calling {@link AbstractRule#before(Description)},
     * {@link AbstractRule#after(Description, Throwable)}, {@link AbstractRule#onError(Description, Throwable)}, and
     * handles their exceptions.
     */
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
