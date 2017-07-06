package at.willhaben.willtest.maven.utils;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runner.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jcabi.matchers.RegexMatchers.containsPattern;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class SuiteGeneratingRunListenerTest {
    private static String FIX_DATE = "20170211_142342";
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    private SuiteGeneratingRunListener suiteGeneratingRunListener;

    @Before
    public void prepare() throws ParseException {
        ExposingSuiteGeneratingRunListener spy = spy(new ExposingSuiteGeneratingRunListener());
        doReturn(new SimpleDateFormat("yyyyMMdd_HHmmss").parse(FIX_DATE))
                .when(spy)
                .getCurrentDate();
        suiteGeneratingRunListener = spy;
        System.setProperty("basedir", temporaryFolder.getRoot().getAbsolutePath());
    }

    @Test
    public void test() throws Exception {
        //for the sake of testing I just use simple jdk classes
        suiteGeneratingRunListener.testStarted(
                descriptionStubOf(Long.class.getName(), "methodA", "methodA[alma]"));
        suiteGeneratingRunListener.testStarted(
                descriptionStubOf(Long.class.getName(), "methodC", "methodC[alma]"));
        //to test importing only one class in case of name conflict I use here 2 Date classes
        suiteGeneratingRunListener.testStarted(
                descriptionStubOf(java.util.Date.class.getName(), "methodB", "methodB[alma]"));
        suiteGeneratingRunListener.testStarted(
                descriptionStubOf(java.sql.Date.class.getName(), "methodBB", "methodBB[alma]"));
        suiteGeneratingRunListener.testStarted(
                descriptionStubOf(FileInputStream.class.getName(), "methodX", "methodX[alma]"));
        suiteGeneratingRunListener.testRunFinished(mock(Result.class));
        String name = SuiteGeneratingRunListener.SUITE_TARGET_CLASS_NAME_PREFIX.replace('.', File.separatorChar) + "_" + FIX_DATE + ".java";
        File expectedTargetFile = new File(
                temporaryFolder.getRoot().getAbsolutePath() + File.separator +
                        "target" + File.separator + name);
        assertThat(expectedTargetFile.exists(),is(true));
        String actualContent = convertWindowsNewLineToLinux(Files.toString(expectedTargetFile, StandardCharsets.UTF_8));
        assertThat(
                "Util date should be imported, since the first class is imported even if simple name collision happens.",
                actualContent,
                containsString("import java.util.Date;"));
        assertThat(
                "Sql date should not be imported, since util date is already imported.",
                actualContent,
                not(containsString("import java.sql.Date;")));
        assertThat(
                "Sql date should be in annotation with FQN present.",
                actualContent,
                containsPattern("\\sjava.sql.Date.class,"));
        assertThat(
                "Util date should be in annotation with simple name present.",
                actualContent,
                containsPattern("\\sDate.class,"));
        try ( InputStream expectedFileAsStream = this.getClass().getResourceAsStream("/ExpectedSuite.java") ) {
            String expectedFileContent = convertWindowsNewLineToLinux(
                    new String( ByteStreams.toByteArray(expectedFileAsStream), StandardCharsets.UTF_8 ));
            assertThat(actualContent,is(expectedFileContent));
        }
    }

    private String convertWindowsNewLineToLinux(String input) {
        return input.replaceAll("\r\n", "\n");
    }

    private Description descriptionStubOf(String className, String methodName, String displayName) {
        Description description = mock(Description.class);
        when(description.getClassName()).thenReturn(className);
        when(description.getMethodName()).thenReturn(methodName);
        when(description.getDisplayName()).thenReturn(displayName);
        return description;
    }

    private static class ExposingSuiteGeneratingRunListener extends SuiteGeneratingRunListener {
        @Override
        public Date getCurrentDate() {
            return super.getCurrentDate();
        }
    }
}