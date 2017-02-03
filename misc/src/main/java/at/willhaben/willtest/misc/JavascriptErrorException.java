package at.willhaben.willtest.misc;

import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;

import java.util.List;
import java.util.stream.Collectors;

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
}
