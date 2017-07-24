package at.willhaben.willtest.browserstack.rule;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

/**
 * Created by michael on 10.07.17.
 */
public class BrowserstackListener extends RunListener {

    @Override
    public void testRunStarted(Description description) throws Exception {
        System.out.println("TestRunStarted");
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        System.out.println("TestRunFinished");
    }
}
