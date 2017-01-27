package at.willhaben.willtest.rule;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liptak on 2016.11.21..
 */
public class ResourceHelper extends AbstractRule {
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
     * @param resourcePath
     * @return
     */
    public File getResourceAsFile(final String resourcePath) {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Classpath resource with path '" + resourcePath + "' does not exist!");
            }

            String normalizedPath = resourcePath.replace('/', File.separatorChar);
            String targetFilePath = temporaryFolder.getRoot().getAbsolutePath() + File.separatorChar + normalizedPath;
            File targetFile = new File(targetFilePath);
            Files.write(ByteStreams.toByteArray(resourceAsStream), targetFile);
            return targetFile;
        } catch (IOException e) {
            throw new RuntimeException("Could not copy resource into a temp file. Resource: '" + resourcePath + "'!", e);
        }
    }
}
