package at.willhaben.willtest.util;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;
import ru.yandex.qatools.ashot.util.InnerScript;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Takes a screenshot of the whole website by scrolling down an combine all screenshots. Its also possible to
 * remove a fixed header navbar.
 */
public class FixedTopBarShootingStrategy extends ShootingStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedTopBarShootingStrategy.class);

    private int headerToCut = 0;
    private int scrollTimeout;
    private By topElementToRemove;

    /**
     * Creates shootingstragety which takes screenshot of the whole page and the
     * ability to remove a fixed header navbar
     * @param topElementToRemove unique identifier of the top navbar
     */
    public FixedTopBarShootingStrategy(By topElementToRemove) {
        this.topElementToRemove = topElementToRemove;
    }

    /**
     * Creates shootingstragety which takes screenshot of the whole page and the
     * ability to remove a fixed header navbar
     * @param headerToCut size of the header to remove in px
     */
    public FixedTopBarShootingStrategy(int headerToCut) {
        this.headerToCut = headerToCut;
    }

    @Override
    public BufferedImage getScreenshot(WebDriver wd) {
        calculateHeaderSizeToCut(wd);
        JavascriptExecutor js = (JavascriptExecutor) wd;

        int allH = getFullHeight(wd);
        int allW = getFullWidth(wd);
        int winH = getWindowHeight(wd);

        winH = winH - headerToCut;
        int scrollTimes = allH / winH;
        int tail = allH - winH * scrollTimes;

        BufferedImage finalImage = new BufferedImage(allW, allH, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = finalImage.createGraphics();

        js.executeScript("scrollTo(0, arguments[0])", 0);
        BufferedImage firstPart = simple().getScreenshot(wd);
        graphics.drawImage(firstPart, 0, 0, null);

        for (int n = 1; n < scrollTimes; n++) {
            js.executeScript("scrollTo(0, arguments[0])", winH * n);
            BufferedImage part = getHeaderCutImage(wd);
            graphics.drawImage(part, 0, n * winH + headerToCut, null);
        }

        if (tail > 0) {
            js.executeScript("scrollTo(0, document.body.scrollHeight)");
            BufferedImage last = getHeaderCutImage(wd);
            BufferedImage tailImage = last.getSubimage(0, last.getHeight() - tail, last.getWidth(), tail);
            graphics.drawImage(tailImage, 0, scrollTimes * winH, null);
        }
        graphics.dispose();

        return finalImage;
    }

    private void calculateHeaderSizeToCut(WebDriver webDriver) {
        if (headerToCut == 0) {
            try {
                WebElement headerElement = webDriver.findElement(topElementToRemove);
                int height = headerElement.getSize().getHeight();
                if (height > headerToCut) {
                    headerToCut = height;
                }
            } catch (NoSuchElementException | TimeoutException e) {
                LOGGER.warn("Can't find element [" + topElementToRemove + "] to calculate the height of the top " +
                        "navigation. Remove height is set to zero.");
            }
        }
    }

    private int getFullHeight(WebDriver driver) {
        return ((Number) InnerScript.execute(InnerScript.PAGE_HEIGHT_JS, driver)).intValue();
    }

    private int getFullWidth(WebDriver driver) {
        return ((Number) InnerScript.execute(InnerScript.VIEWPORT_WIDTH_JS, driver)).intValue();
    }

    private int getWindowHeight(WebDriver driver) {
        return ((Number) InnerScript.execute(InnerScript.VIEWPORT_HEIGHT_JS, driver)).intValue();
    }

    private BufferedImage getHeaderCutImage(WebDriver webDriver) {
        BufferedImage baseImage = simple().getScreenshot(webDriver);
        int h = baseImage.getHeight();
        int w = baseImage.getWidth();
        return baseImage.getSubimage(0, headerToCut, w, h - headerToCut);
    }
}
