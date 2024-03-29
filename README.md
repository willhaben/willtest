# willtest

[![Build Status](https://travis-ci.org/willhaben/willtest.svg?branch=master)](https://travis-ci.org/willhaben/willtest)
[![Maven Central](https://img.shields.io/maven-central/v/at.willhaben.willtest/core)](https://mvnrepository.com/artifact/at.willhaben.willtest)

Do not edit this file. See [this](https://github.com/willhaben/willtest#update-the-documentation) for instructions.

## Overview
Willtest makes integrating JUnit and Selenium easier with providing a set of 
[JUnit Extension](https://junit.org/junit5/docs/current/user-guide/#extensions) and other utility classes which let you 
complete tasks like:
* Managing lifecycle of WebDriver instances
* Reporting capabilities like saving logs, screenshots and HTML source
* Fetching information from javascript alerts and errors
* Supporting uploading of any file from file system or classpath
* Abstract PageObject with some common functions
* Main browsers are supported (Firefox, Chrome, Edge, InternetExplorer)
* Support for mobile tests with appium (Android, iOS)

## Requirements
* Currently Selenium with version [3.141.59] is used
* JUnit 5 with version [5.7.0] is used

## Getting Started
The code is built up from several small building blocks. Only the ```core``` module is needed to start
writing browser tests. Nevertheless the ```misc``` module will speed up development with some helper classes
and an abstract ```PageObject``` which can be extended.

```xml
<dependency>
    <groupId>at.willhaben.willtest</groupId>
    <artifactId>core</artifactId>
    <version>3.1.9</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>at.willhaben.willtest</groupId>
    <artifactId>misc</artifactId>
    <version>3.1.9</version>
    <scope>compile</scope>
</dependency>
```

[Geckodriver](https://github.com/mozilla/geckodriver/releases) is also needed to run a test with Firefox + Selenium.
The path of the geckodriver executable is expected in the [webdriver.gecko.driver](http://learn-automation.com/use-firefox-selenium-using-geckodriver-selenium-3/) 
system property. An [automated way](https://github.com/webdriverextensions/webdriverextensions-maven-plugin)
is shown in the [POM of the examples module](https://github.com/willhaben/willtest/blob/master/examples/pom.xml).
It downloads and applies the geckodriver and chromedriver in the surefire settings.

Now the environment is ready to be used like this:

```java
@ExtendWith(DriverParameterResolverExtension.class)
@BrowserUtil({ScreenshotProvider.class, PageSourceProvider.class})
class FirstExample {

    private static final String REPO_HEADER_LOCATOR = "div.repohead-details-container h1";
    private static final String WILLTEST_GITHUB_PAGE = "https://github.com/willhaben/willtest";

    @Test
    // The started WebDriver will be injected in the test method parameter
    void openPage(WebDriver driver) {
        driver.get(WILLTEST_GITHUB_PAGE);
        WebElement element = driver.findElement(By.cssSelector(REPO_HEADER_LOCATOR));
        assertThat(element.getText(), is("willhaben/willtest"));
    }

    @Test
    void buggyTest(WebDriver driver) {
        driver.get(WILLTEST_GITHUB_PAGE);
        WebElement element = driver.findElement(By.cssSelector(REPO_HEADER_LOCATOR));
        assertThat(element.getText(), is("fooooo"));
    }
}
```

If the test class is executed (it can be found in the examples module) a screenshot and 
an HTML source are automatically saved into the surefire reports, in case of the ```buggyTest``` method. 
This is done by the Willtest framework when you specify the two provider classes in the ```@BrowserUtil```
annotation.

## Recipes
All the code examples below can be found in the examples maven submodule.

### Using Selenium HUB to run Firefox

By default the willtest framework will start a local firefox instance to run the tests. If this behaviour 
needs to be changed there are some properties to change this.

|name|description|default|possible|
|---|---|---|---|
|browser|Select which browser should be started|```firefox```|```firefox```, ```chrome```, ```ie```, ```edge```|
|remote|Execute tests locally or on a selenium grid|```false```|```false```, ```true```|
|seleniumHub|Specify the url to the selenium grid or remote browser|EMPTY|```http://sel-grid:1234/wd/hub```|

### PageObject
To write tests with the page-object-pattern you can use the provided ```PageObject``` as a base
class. It provides some commonly needed functions to improve the readability of your page objects and tests.
Every method is documented in JavaDoc.

### Customization options
Extending the willtest framework is done with the ```@BrowserUtil``` annotation. There are multiple interfaces
which can be implemented to change the behaviour of the framework. The annotation can be used on class and method level.
The method level will override the class level one.

#### Browseroptions
The browser options and capabilities can be modified by implementing the ```Optionmodifier``` interface.

```java
public class BrowserSetup implements OptionModifier {
    
    @Override
    public ChromeOptions modifyChromeOptions(ChromeOptions options) {
        options.merge(getDesiredCapabilities("chrome"));
        options.addArguments("--start-maximized");
        return options;
    }

    public DesiredCapabilities getDesiredCapabilities(String nameOfBrowser) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("applicationCacheEnabled", false);
        capabilities.setJavascriptEnabled(true);
        capabilities.setBrowserName(nameOfBrowser);
        return capabilities;
    }
}

// ad the class to the test
@ExtendWith(DriverParameterResolverExtension.class)
@BrowserUtil(BrowserSetup.class)
class FirstExample {}
```

#### Webdriver modifications
If you need to modify the ```WebDriver``` (e.g. maximise the browser) the ```WebDriverPostInterceptor``` interface 
can be used.

```java
public class PostProcessSetup implements WebDriverPostInterceptor {
    @Override
    public void postProcessWebDriver(WebDriver driver) {
        driver.manage().window().maximize();
    }
}

// ad the class to the test
@ExtendWith(DriverParameterResolverExtension.class)
@BrowserUtil(PostProcessSetup.class)
class FirstExample {}
```

#### On failure behaviour
When you need to clear up resources or signal a test failure then the interface ```FailureListener``` must be used.

### File Upload
The Willtest framework configures by default a simple way to upload any file from the filesystem or from the classpath.
Files from jars on the classpath are also supported. It does not matter if your test runs in a local browser, or in
a Selenium HUB.

Uploading a simple file is that simple:

```java
htmlInputElementWithFileType.sendKeys(pathToFile);
```

Uploading a classpath resource is slightly more complicated. It is necessary to copy the file to a temp directory
and use this path with the ```sendKeys``` method. The TempDirectory extension from 
[Junit Pioneer](https://junit-pioneer.org/) can be used for this.
 
## Asserting browser request
For testing browser requests and asserting against them it is possible to start a 
[browsermob-proxy](https://github.com/lightbody/browsermob-proxy) alongside the test.
This can be done by specifying the ```ProxyWrapper``` class as test method parameter. The framework checks the test
method parameters and automatically starts the proxy and injects the parameter.

```java
@ExtendWith(DriverParameterResolverExtension.class)
class ProxyExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyExample.class);

    @Test
    void createProxyAndCaptureRequest(WebDriver driver, ProxyWrapper proxyWrapper) {
        proxyWrapper.getProxy().enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        proxyWrapper.getProxy().newHar();
        driver.get("https://www.google.com");
        proxyWrapper.getProxy().getHar()
                .getLog()
                .getEntries()
                .stream()
                .map(HarEntry::getRequest)
                .forEach(request -> {
                    LOGGER.info(request.getUrl());
                });
    }
}
```

## Screenshot settings
If the site has a fixed top bar it is possible to provide a custom ```ShootingStrategy``` to cut this out on the
screenshot. See the ```ScreenshotProviderExample```.

## Allure
If you want to use allure reporting, add all dependencies (see the [allure-java github page](https://github.com/allure-framework/allure-java)) to your repository and call the mvn install or test with `-Dallure=true`.
Screenshots will be added automatically if you use them.

## Contribution
Willhaben accepts pull requests or other type of feedback. We do our best to reply in a short period of time.

## Update the documentation
Do not edit the *README.md* because it is auto generated. Use the *readme-md-template.md* file and run a
```mvn clean process-resources``` or ```mvn clean install```. This is used to inject maven properties like the current
version in the README.

## License
MIT, Copyright (c) 2021 willhaben internet service GmbH & Co KG
