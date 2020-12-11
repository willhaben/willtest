package at.willhaben.willtest.maven.utils;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

@RunListener.ThreadSafe
public class FailFastOnSpringContextLoadingProblemsRunListener extends RunListener {


    private static int numberOfContextProblems = 0;
    private static int numberOfFailures = 0;


    private void abortTest(Failure failure) {
        throw new RuntimeException("Found failing IT ("+ failure.getDescription().getClassName() +"). Cause: the Application Context couldn't be loaded. \n" +
                "Try to execute tests of " + failure.getDescription().getDisplayName() + " in your IDE.\n");
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
            abortTest(failure);
        }



    }

}
