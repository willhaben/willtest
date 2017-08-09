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
     * @param scrollTimeout wait ms after scrolling
     * @param topElementToRemove unique identifier of the top navbar
     */
    public FixedTopBarShootingStrategy(int scrollTimeout, By topElementToRemove) {
        this(scrollTimeout);
        this.topElementToRemove = topElementToRemove;
    }

    /**
     * Creates shootingstragety which takes screenshot of the whole page and the
     * ability to remove a fixed header navbar
     * @param scrollTimeout wait ms after scrolling
     * @param headerToCut size of the header to remove in px
     */
    public FixedTopBarShootingStrategy(int scrollTimeout, int headerToCut) {
        this(scrollTimeout);
        this.headerToCut = headerToCut;
    }

    /**
     * Creates shootingstragety which takes screenshot of the whole page
     * @param scrollTimeout wait ms after scrolling
     */
    public FixedTopBarShootingStrategy(int scrollTimeout) {
        this.scrollTimeout = scrollTimeout;
    }

    @Override
    public BufferedImage getScreenshot(WebDriver wd) {
        calculateHeaderSizeToCut(wd);
        JavascriptExecutor js = (JavascriptExecutor) wd;

        int allH = getFullHeight(wd);
        int allW = getFullWidth(wd);
        int winH = getWindowHeight(wd);

        int scrollTimes = allH / winH;
        int tail = allH - winH * scrollTimes + headerToCut * scrollTimes;

        BufferedImage finalImage = new BufferedImage(allW, allH, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = finalImage.createGraphics();

        js.executeScript("scrollTo(0, arguments[0])", 0);
        BufferedImage firstPart = simple().getScreenshot(wd);
        graphics.drawImage(firstPart, 0, 0, null);

        for (int n = 1; n < scrollTimes; n++) {
            js.executeScript("scrollTo(0, arguments[0])", winH * n - headerToCut * n);
            waitForScrolling();
            BufferedImage part = getHeaderCutImage(wd);
            graphics.drawImage(part, 0, n * winH - headerToCut * (n-1) , null);
        }

        if (tail > 0) {
            js.executeScript("scrollTo(0, document.body.scrollHeight)");
            waitForScrolling();
            BufferedImage last = getHeaderCutImage(wd);
            BufferedImage tailImage = last.getSubimage(0, last.getHeight() - tail, last.getWidth(), tail);
            graphics.drawImage(tailImage, 0, scrollTimes * winH - headerToCut * (scrollTimes), null);
        }
        graphics.dispose();

        return finalImage;
    }

    private void calculateHeaderSizeToCut(WebDriver webDriver) {
        if (headerToCut == 0) {
            WebDriverWait webDriverWait = new WebDriverWait(webDriver, 1, 100);
            webDriverWait.until(driver -> {
                try {
                    WebElement headerElement = driver.findElement(topElementToRemove);
                    int height = headerElement.getSize().getHeight();
                    if (height > headerToCut) {
                        headerToCut = height;
                    }
                    return true;
                } catch (NoSuchElementException | TimeoutException e) {
                    LOGGER.warn("Can't find element [" + topElementToRemove + "] to calculate the height of the top navigation. Remove height is set to zero.");
                    return false;
                }
            });
        }
    }

    private void waitForScrolling() {
        try {
            Thread.sleep(scrollTimeout);
        } catch (InterruptedException ignored) {
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
