package at.willhaben.willtest.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;
import ru.yandex.qatools.ashot.util.InnerScript;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by michael on 08.08.17.
 */
public class FixedTopBarShootingStrategy extends ShootingStrategy {

    private int headerToCut = 0;
    private int scrollTimeout;

    public FixedTopBarShootingStrategy(int scrollTimeout, int headerToCut) {
        this(scrollTimeout);
        this.headerToCut = headerToCut;
    }

    public FixedTopBarShootingStrategy(int scrollTimeout) {
        this.scrollTimeout = scrollTimeout;
    }

    @Override
    public BufferedImage getScreenshot(WebDriver wd) {
        JavascriptExecutor js = (JavascriptExecutor) wd;

        int allH = getFullHeight(wd);
        int allW = getFullWidth(wd);
        int winH = getWindowHeight(wd);

        int scrollTimes = allH / winH;
        int tail = allH - winH * scrollTimes + headerToCut * scrollTimes;

        BufferedImage finalImage = new BufferedImage(allW, allH, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = finalImage.createGraphics();

        BufferedImage firstPart = simple().getScreenshot(wd);
        graphics.drawImage(firstPart, 0, 0, null);

        for (int n = 1; n < scrollTimes; n++) {
            js.executeScript("scrollTo(0, arguments[0])", winH * n - headerToCut * n);
            waitForScrolling();
            BufferedImage part = getHeaderCuttedImage(wd);
            graphics.drawImage(part, 0, n * winH - headerToCut * (n-1) , null);
        }

        if (tail > 0) {
            js.executeScript("scrollTo(0, document.body.scrollHeight)");
            waitForScrolling();
            BufferedImage last = getHeaderCuttedImage(wd);
            BufferedImage tailImage = last.getSubimage(0, last.getHeight() - tail, last.getWidth(), tail);
            graphics.drawImage(tailImage, 0, scrollTimes * winH - headerToCut * (scrollTimes), null);
        }
        graphics.dispose();

        return finalImage;
    }

    private void waitForScrolling() {
        try {
            Thread.sleep(scrollTimeout);
        } catch (InterruptedException ignored) {
        }
    }

    public int getFullHeight(WebDriver driver) {
        return ((Number) InnerScript.execute(InnerScript.PAGE_HEIGHT_JS, driver)).intValue();
    }

    public int getFullWidth(WebDriver driver) {
        return ((Number) InnerScript.execute(InnerScript.VIEWPORT_WIDTH_JS, driver)).intValue();
    }

    public int getWindowHeight(WebDriver driver) {
        return ((Number) InnerScript.execute(InnerScript.VIEWPORT_HEIGHT_JS, driver)).intValue();
    }

    private BufferedImage getHeaderCuttedImage(WebDriver webDriver) {
        BufferedImage baseImage = simple().getScreenshot(webDriver);
        int h = baseImage.getHeight();
        int w = baseImage.getWidth();
        return baseImage.getSubimage(0, headerToCut, w, h - headerToCut);
    }
}
