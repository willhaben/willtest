package at.willhaben.willtest.rule;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Makes possible to upload a classpath resource file using Selenium.
 * <p>
 * Selenium can upload only {@link File} instances (See {@link org.openqa.selenium.remote.FileDetector}).
 * In tests you might want to upload a file, which is inside a JAR file you are having on the classpath.
 * This rule can copy such resources into a temp file, which will be then available during the test for being uploaded.
 * After the test the cleanup of such files happens automatically.
 * See also {@link at.willhaben.willtest.config.FileDetectorConfigurator}.
 */
public class ResourceHelper extends TestFailureAwareRule {
    private final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Override
    protected void before(Description description) throws Throwable {
        super.before(description);
        temporaryFolder.create();
    }

    @Override
    protected void after(Description description, Throwable testFailure) throws Throwable {
        super.after(description, testFailure);
        temporaryFolder.delete();
    }

    /**
     * Gets resource either from a jar or from a file as stream, and writes its content into a temp file.
     *
     * @param resourcePath classpath resource path
     * @return temp file path. This file will be cleaned up automatically after the test.
     */
    public File getResourceAsFile(final String resourcePath) {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Classpath resource with path '" + resourcePath + "' does not exist!");
            }

            String normalizedPath = resourcePath.replace('/', File.separatorChar);
            String targetFilePath = temporaryFolder.getRoot().getAbsolutePath() + File.separatorChar + normalizedPath;
            File targetFile = new File(targetFilePath);
            createParentFolderIfNeccessary(targetFile);
            Files.write(ByteStreams.toByteArray(resourceAsStream), targetFile);
            return targetFile;
        } catch (IOException e) {
            throw new RuntimeException("Could not copy resource into a temp file. Resource: '" + resourcePath + "'!", e);
        }
    }

    private void createParentFolderIfNeccessary(File targetFile) {
        File folderContainingTargetFile = targetFile.getParentFile();
        if (!folderContainingTargetFile.exists() && !folderContainingTargetFile.mkdirs()) {
            throw new RuntimeException("Could not create folder " + folderContainingTargetFile + "!");
        }
    }
}
