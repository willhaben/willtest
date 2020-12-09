package at.willhaben.willtest.maven.utils;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Copied over from https://bitbucket.willhaben.at/users/whmalema/repos/fail-on-contexterrors-maven-plugin/browse
 * @see <a href="https://bitbucket.willhaben.at/users/whmalema/repos/fail-on-contexterrors-maven-plugin/browse/src/main/java/at/willhaben/maven/FailFastOnSpringContextLoadingProblemsRunListener.java">FailFastOnSpringContextLoadingProblemsRunListener.java</a>
 */
@RunListener.ThreadSafe
public class FailFastOnSpringContextLoadingProblemsRunListener extends RunListener {


    private static int numberOfContextProblems = 0;
    private static int numberOfFailures = 0;

    @Override
    public void testRunStarted(Description description) throws Exception {
        if (numberOfContextProblems == 0)
            super.testRunStarted(description);
        else {
            abortTest();
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        if (numberOfContextProblems == 0)
            super.testStarted(description);
        else {
            abortTest();
        }
    }

    private void abortTest() {
        String errorMessage = "Found failing ITs because the Application Context couldn't be loaded. \n" +
                "Check the error above, fix it and try again!";
        System.err.println("[ERROR] " + errorMessage);
        throw new RuntimeException(errorMessage);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
        System.err.println("Found " + numberOfFailures + " failures when running ITs");
        System.err.println("Found " + numberOfContextProblems + " failures when loading spring context.");
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        numberOfFailures++;
        Throwable cause = failure.getException();
        String message = cause.toString();
        if (message.contains("java.lang.IllegalStateException: Failed to load ApplicationContext")) {
            numberOfContextProblems++;
            System.err.println("Found failures when loading spring context. Cause: " + cause);
        }



    }

}
