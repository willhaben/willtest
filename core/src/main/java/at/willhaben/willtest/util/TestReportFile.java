package at.willhaben.willtest.util;

import org.junit.runner.Description;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by liptak on 2016.08.24..
 */
public class TestReportFile {
    private static final String COMMON_PREFIX_FOR_ALL_REPORT_FILES = "TR_";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss.SSS");

    private Description testDescription;
    private String prefix = "";
    private String postfix = "";

    public File getFile() {
        File surefireReportsFolder = new File("surefire-reports");
        if (!surefireReportsFolder.exists() && !surefireReportsFolder.mkdirs()) {
            throw new RuntimeException("Could not create folder " + surefireReportsFolder + "!");
        }
        return new File(surefireReportsFolder, generateFileName());
    }

    private String generateFileName() {
        String className = testDescription.getTestClass().getSimpleName();
        String methodName = testDescription.getMethodName().replace('.', '_');
        String timeStamp = DATE_FORMAT.format(ZonedDateTime.now());
        return COMMON_PREFIX_FOR_ALL_REPORT_FILES + prefix + className + "_" + methodName + "-" + timeStamp + postfix;
    }

    public static class Builder {
        private final TestReportFile testReportFile = new TestReportFile();

        Builder(Description testDescription) {
            testReportFile.testDescription = testDescription;
        }

        public Builder withPrefix(String prefix) {
            testReportFile.prefix = prefix;
            return this;
        }

        public Builder withPostix(String postfix) {
            testReportFile.postfix = postfix;
            return this;
        }

        public TestReportFile build() {
            return testReportFile;
        }
    }

    public static Builder forTest(Description testDescription) {
        return new Builder(testDescription);
    }
}
