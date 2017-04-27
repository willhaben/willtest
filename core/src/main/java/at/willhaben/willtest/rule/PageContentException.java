package at.willhaben.willtest.rule;

/**
 * An exception, which contains the page content of the browser at the time of the test error. Will be used
 * probably to retry a test based on the page content. See {@link Retry} for details.
 */
public class PageContentException extends Exception {
    private static final long serialVersionUID = -2549945920794470230L;
    private final String pageContent;

    public PageContentException(String pageContent) {
        super("This is just an exception transporting page source along with the containing exception. " +
                "Page content will be probably used to decide if the test will be retried.");
        this.pageContent = pageContent;
    }

    public String getPageContent() {
        return pageContent;
    }
}
