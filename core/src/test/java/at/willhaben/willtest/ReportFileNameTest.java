package at.willhaben.willtest;

import at.willhaben.willtest.util.TestReportFile;
import org.junit.Test;
import org.junit.runner.Description;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ReportFileNameTest {

    private static final String PREFIX = "prefix_";
    private static final String POSTFIX = "_postfix";
    private static final String METHOD_NAME = "Testmethod";
    private static final String INVALID_CHARS = ":<>*/?";

    @Test
    public void testFileNameGeneration() {
        Description testDescription = Description.createTestDescription(ReportFileNameTest.class, METHOD_NAME);

        TestReportFile reportFile = TestReportFile.forTest(testDescription)
                .withPrefix(PREFIX)
                .withPostix(POSTFIX)
                .build();


        String regex = "TR_" + PREFIX + ReportFileNameTest.class.getSimpleName() + "_" + METHOD_NAME +
                "-\\d{4}\\.\\d{2}\\.\\d{2}-\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{3}" + POSTFIX;
        String fileName = reportFile.getGeneratedName();
        assertThat("'" + fileName + "' doesn't match regex '" + regex + "'.",
                fileName.matches(regex), is(true));

        assertThat(fileName, allOf(not(containsString(":")),
                not(containsString("<")),
                not(containsString(">")),
                not(containsString("*")),
                not(containsString("/")),
                not(containsString("?"))));
    }
}
