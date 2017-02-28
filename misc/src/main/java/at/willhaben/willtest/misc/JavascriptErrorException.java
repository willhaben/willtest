package at.willhaben.willtest.misc;

import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains a list of javascript errors fetched from a browser during a test. The error message of this exception
 * contains the javascript errors text.
 */
public class JavascriptErrorException extends Exception {
    private static final long serialVersionUID = -6718614835487885991L;
    private final List<JavaScriptError> javaScriptErrors;


    public JavascriptErrorException(String message, List<JavaScriptError> javaScriptErrors) {
        super(message);
        this.javaScriptErrors = javaScriptErrors;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + getJavascriptErrorsMessagePostfix();
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage() + getJavascriptErrorsMessagePostfix();
    }

    private String getJavascriptErrorsMessagePostfix() {
        return "\nJavascript errors: \n" + javaScriptErrors.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }

    /**
     * @return the list of {@link JavaScriptError} instances fetched from the browser during test execution.
     */
    public List<JavaScriptError> getJavaScriptErrors() {
        return javaScriptErrors;
    }
}
