package at.willhaben.willtest.util;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.runner.Description;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Decides where are the report files saved. By default report files are saved into {@value #DEFAULT_REPORT_FOLDER},
 * which can be overridden by {@value #REPORT_FOLDER_SYSTEM_PROPERTY} system property. File names will generated
 * using the following schema:
 * <p>
 * "{@value COMMON_PREFIX_FOR_ALL_REPORT_FILES} + PREFIX_IF_ANY + CLASS_NAME + METHOD_NAME + TIMESTAMP + POSTFIX_IF_ANY
 */
public class TestReportFile {
    private static final String REPORT_FOLDER_SYSTEM_PROPERTY = "willtest.reportFolder";
    private static final String COMMON_PREFIX_FOR_ALL_REPORT_FILES = "TR_";
    private static final String DEFAULT_REPORT_FOLDER = "surefire-reports";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd-HH.mm.ss.SSS");

    private Description testDescription;
    private ExtensionContext testExtensionContext;
    private String prefix = "";
    private String postfix = "";

    /**
     * Starts building of a test report file
     *
     * @param testDescription
     * @return this builder
     */
    public static Builder forTest(Description testDescription) {
        return new Builder(testDescription);
    }

    public static Builder forTest(ExtensionContext context) {
        return new Builder(context);
    }

    /**
     * Gives back the {@link File} object, which can be used as target for report.
     *
     * @return the file which can be used as report file
     */
    public File getFile() {
        String reportFolderPath = Environment.getValue(REPORT_FOLDER_SYSTEM_PROPERTY, DEFAULT_REPORT_FOLDER);
        File reportFolder = new File(reportFolderPath);
        if (!reportFolder.exists() && !reportFolder.mkdirs()) {
            throw new RuntimeException("Could not create folder " + reportFolder + "!");
        }
        return new File(reportFolder, generateFileName());
    }

    private String getClassName() {
        if (testDescription == null) {
            return testExtensionContext.getRequiredTestClass().getSimpleName();
        } else {
            return testDescription.getTestClass().getSimpleName();
        }
    }

    private String getMethodName() {
        if(testDescription == null) {
            return testExtensionContext.getRequiredTestMethod().getName();
        } else {
            return testDescription.getMethodName().replace('.', '_');
        }
    }

    public String getGeneratedName() {
        return generateFileName();
    }

    private String generateFileName() {
        String className = getClassName();
        String methodName = getMethodName();
        String timeStamp = DATE_FORMAT.format(ZonedDateTime.now());
        return escapeFileName(COMMON_PREFIX_FOR_ALL_REPORT_FILES + prefix + className +
                "_" + methodName + "-" + timeStamp + postfix);
    }

    private String escapeFileName(String originalName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < originalName.length(); i++) {
            char c = originalName.charAt(i);
            if ((c >= '0' && c <= '9') ||
                    (c >= 'A' && c <= 'Z') ||
                    (c >= 'a' && c <= 'z') ||
                    (c == '.') ||
                    (c == '_') ||
                    (c == '-')) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static class Builder {
        private final TestReportFile testReportFile = new TestReportFile();

        Builder(Description testDescription) {
            testReportFile.testDescription = testDescription;
        }

        Builder(ExtensionContext context) {
            testReportFile.testExtensionContext = context;
        }

        /**
         * @param prefix name prefix to be used. F.i. "PaymentModule"
         * @return this builder
         */
        public Builder withPrefix(String prefix) {
            testReportFile.prefix = prefix;
            return this;
        }

        /**
         * @param postfix name postfix to be used. F.i. ".png"
         * @return this builder
         */
        public Builder withPostix(String postfix) {
            testReportFile.postfix = postfix;
            return this;
        }

        public TestReportFile build() {
            return testReportFile;
        }
    }
}
