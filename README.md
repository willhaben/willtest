# willtest

[![Build Status](https://travis-ci.org/willhaben/willtest.svg?branch=master)](https://travis-ci.org/willhaben/willtest)
[![Maven Central](https://img.shields.io/maven-central/v/at.willhaben.willtest/core)](https://mvnrepository.com/artifact/at.willhaben.willtest)

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
* Currently Selenium with version [3.14.0] is used
* JUnit 5 with version [5.5.2] is used

## Getting Started
The code is built up from several small building blocks. Only the ```core``` module is needed to start start
writing browser tests. Nevertheless the ```misc``` module will speed up development with some helper classes
and an abstract ```PageObject``` which can be extended.

```xml
<dependency>
    <groupId>at.willhaben.willtest</groupId>
    <artifactId>core</artifactId>
    <version>3.0.0-M1</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>at.willhaben.willtest</groupId>
    <artifactId>misc</artifactId>
    <version>3.0.0-M1</version>
    <scope>compile</scope>
</dependency>
```

[Geckodriver](https://github.com/mozilla/geckodriver/releases) is also needed to run a test with Firefox + Selenium.
The path of geckodriver executable is expected in [webdriver.gecko.driver](http://learn-automation.com/use-firefox-selenium-using-geckodriver-selenium-3/) 
system property. In the [POM of the examples module](https://github.com/willhaben/willtest/blob/master/examples/pom.xml)
there is an automated way shown, which downloads and applies the geckodriver and chromedriver in surefire settings.

Now the environment is ready to be used like this:

```java
@ExtendWith(DriverParameterResolverExtension.class)
@BrowserUtil({ScreenshotProvider.class, PageSourceProvider.class})
class FirstExample {

    private static final String REPO_HEADER_LOCATOR = "div.repohead-details-container h1";
    private static final String WILLTEST_GITHUB_PAGE = "https://github.com/willhaben/willtest";

    @Test
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

If the test class is executed (it can be found in examples module), screenshot and 
HTML source are automatically saved into the surefire reports case of the ```buggyTest``` method. 
This is done by the Willtest framework when u specify the two provider classes in the ```@BrowserUtil```
annotation.

## Recipes

All the code examples below can be found in the examples maven submodule.
### Using Selenium HUB to run Firefox

```SeleniumRule``` uses by default local firefox instances to run the tests. If this behaviour needs to be changed, 
the name of the ```SeleniumProvider``` implementation has to be defined as system property.
For example, to get a Firefox instance on a specific selenium hub, the following values has to be defined:

```
seleniumProvider=at.willhaben.willtest.rule.SeleniumHubFirefoxProvider
seleniumHub=YOUR_SELENIUM_HUB_URL
```

In the background the ```SeleniumProvider``` is instantiated using Reflection by the 
```SeleniumProviderFactory``` class.

### PageObject
The write tests with the page-object-pattern you can use the provided ```PageObject``` as base
class. It provides some commonly needed functions to improve the readability of your page objects.
Every method is documented in JavaDoc.

### Creating new SeleniumProvider implementations
By design Willtest was not intended to depend on any dependency injection framework, so a really simple workaround
is added to let the user to create new ```SeleniumProvider``` implementations.

If a ```SeleniumProvider``` has a default constructor, it can be instantiated by the ```SeleniumRule``` class based on its 
fully qualified name. If some dependencies has to be injected to it, the constructor of ```SeleniumRule``` accepts
an array of class<->object pairs in form of ```ParameterObject``` instances. These dependencies are tried to be injected
using the a setter called "set" + class name.

Example code:

```java
public class DummySeleniumProvider extends AbstractSeleniumProvider<DummySeleniumProvider,WebDriver> {
    private Pattern patternField;
    
    /**
     * This will be injected by {@link at.willhaben.willtest.misc.rule.SeleniumProviderFactory} based on the setter name
     * @param patternField
     */
    public void setPattern(Pattern patternField) {
        this.patternField = patternField;
    }

    @Override
    public DummySeleniumProvider getThis() {
        return this;
    }

    @Override
    protected WebDriver constructWebDriver(DesiredCapabilities desiredCapabilities) {
        WebDriver webDriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
        doReturn(patternField.pattern()).when(webDriver).getCurrentUrl();
        return webDriver;
    }
}
```

```java
public class DummyTest {
    private static Pattern THIS_WILL_BE_INJECTED_INTO_DUMMY_SELENIUM_PROVIDER = Pattern.compile("fooooo");
    private static String originalProvider;

    @AfterClass
    public static void afterClass() {
        if ( originalProvider != null ) {
            System.setProperty(SeleniumProviderFactory.SELENIUM_PROVIDER_CLASS_NAME, originalProvider);
        }
    }

    @Rule
    public SeleniumRule seleniumRule = new SeleniumRule(
            new SeleniumProviderFactory.ParameterObject(Pattern.class,THIS_WILL_BE_INJECTED_INTO_DUMMY_SELENIUM_PROVIDER))
            .withoutImplicitWait()
            .withoutScriptTimeout();

    @Test
    public void testInjection() {
        assertThat(seleniumRule.getWebDriver().getCurrentUrl(),is("fooooo"));
    }
}
```

Alternative way is to completely dismiss the ```SeleniumRule``` class and implement a new similar class, which wires all
the necessary classes together. ```SeleniumProvider``` as interface can be also simply implemented with completely dismissing 
```AbstractSeleniumProvider```. These are the harder way.

### File Upload
The ```SeleniumRule``` configures by default a simple way to upload any file from the filesystem or from the classpath.
Files from jars on the classpath are also supported. It also does not matter if your test runs in a local browser, or in
a Selenium HUB.

Uploading of a simple file is that simple:

```java
htmlInputElementWithFileType.sendKeys(pathToFile);
```

Uploading of a classpath resource is slightly more complicated: 
```java
htmlInputElementWithFileType.sendKeys(seleniumRule.getResourceHelper().getResourceAsFile("hose1.jpg").getAbsolutePath());
```

In the background ```ResourceHelper``` and ```FileDetectorConfigurator``` are working together to make such upload easily
 possible. ```ResourceHelper``` creates temporary files to be uploaded, which are also automatically deleted after the 
 test.

### Adjusting WebDriver settings in a test

Using the ```WebDriverConfigurationParticipant``` interface 
[DesiredCapabilities](https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities) can be added easily, which are 
used by the ```SeleniumProvider``` implementations during the WebDriver instantiation. It is also possible to do some 
 changes on the ```WebDriver``` after instantiation.
 
 Firefox specific settings can be changed using the ```FirefoxConfigurationParticipant``` interface.
 
The example below sets a geo-location inside the given Firefox Browser, and sets its window size:

```java
public class ChangeSettings {
    private static final String GEO_LOCATION_IN_THE_MIDDLE_OF_SANKT_POELTEN =
            "data:application/json,{\"location\":{\"lat\":48.201023,\"lng\":15.620250},\"accuracy\":10}";

    @Rule
    public SeleniumRule seleniumRule = new SeleniumRule()
            //with withDefaultFirefoxSettings settings is getting your location is granted. 
            //See at.willhaben.willtest.config.DefaultFirefoxConfigurationParticipant
            .withDefaultFirefoxSettings()
            .withFirefoxConfigurationParticipant(
                new FirefoxConfigurationParticipant() {
                    @Override
                    public void adjustFirefoxProfile(FirefoxProfile firefoxProfile) {
                        firefoxProfile.setPreference("geo.wifi.uri",GEO_LOCATION_IN_THE_MIDDLE_OF_SANKT_POELTEN);
                    }
                })
            .addWebDriverConfigurationParticipant(new WebDriverConfigurationParticipant<WebDriver>() {
                @Override
                public void postConstruct(WebDriver webDriver) {
                    webDriver.manage().window().setSize(new Dimension(800, 600));
                }
            });

    @Test
    public void testMyLocation() {
        WebDriver webDriver = seleniumRule.getWebDriver();
        webDriver.get("https://mycurrentlocation.net/");
        assertThat(webDriver.findElement(By.cssSelector("td#latitude")).getText(),is("48.20102"));
        assertThat(webDriver.findElement(By.cssSelector("td#longitude")).getText(),is("15.62025"));
    }
}
```
### Using AdBlocker
If the ads make the tests slow, adblocker can be activated in the tests easily using ```AdBlockerConfigurator```.
To activate this, the following section should be added into the maven configuration, which downloads the necessary xpi
file:
```xml
<plugin>
	<groupId>org.codehaus.gmaven</groupId>
	<artifactId>groovy-maven-plugin</artifactId>
	<executions>
		<execution>
			<id>download-adblocker</id>
			<phase>generate-test-resources</phase>
			<goals>
				<goal>execute</goal>
			</goals>
			<configuration>
				<source><![CDATA[
					AntBuilder ant = new AntBuilder();
					String fileName = "adblock.xpi";
					String adBlockerPath = project.build.testOutputDirectory + File.separator + fileName;
					File adBlockerFile = new File(adBlockerPath);
					if ( !adBlockerFile.exists() ) {
						String adBlockerURL = "https://addons.mozilla.org/firefox/downloads/latest/adblock-plus/addon-1865-latest.xpi";
						println("Downloading adblocker firefox plugin from " + adBlockerURL + "...");
						ant.mkdir(dir:project.build.testOutputDirectory);
						ant.get(
								src:adBlockerURL,
								dest:adBlockerPath,
								skipexisting:false );
					}
					]]></source>
			</configuration>
		</execution>
	</executions>
</plugin>
```

After customizing the POM file activating adblocker is that easy:
```xml
new SeleniumRule().withAdBlocker();
```

### Retry Tests
It happens sometimes, that a Selenium HUB has some weird problems like "Connection refused", 
"Unable to bind to locking port" or "this.getChromeWindowFromDocumentWindow(...) is undefined". Using the Retry
 rule it is possible to retry tests if the exception matches a given matcher.
 
```java
public class RetryExampleTest {
    private static int counter;
    
    @Rule
    public SeleniumRule seleniumRule = new SeleniumRule()
            //Important to bind the retry rule early in the rule chain so that other rules like SeleniumProvider will be
            // re-run in case of test failure
            .secondOuterRule(
                    new Retry(
                            new ExceptionMatcher<WebDriverException>(
                                    WebDriverException.class,
                                    containsString("Unable to bind to locking port")),
                            5));

    @Test
    public void testRetry() {
        counter++;
        if ( counter < 4 ) {
            throw new WebDriverException("Unable to bind to locking port 63333");
        }
        //this line will be reached, since the retry rule works
    }
}
```

### Add Eventlistener

For a better documentation of failed tests it's possible to add an implementation of the ```WebDriverEventListener``` 
interface to the SeleniumRule. 
[WebDriverEventlistener](https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/support/events/WebDriverEventListener.html)

There is a simple default implementation ```SeleniumEventListener``` in the log4j module. It simply creates a separate
logfile (prefix '_action.log') with a description of the test steps.

The following test example will produce 

* [ INFO] 13:30:28 Try to find following element: By.cssSelector: .header-search-input
* [ INFO] 13:30:28 Input in: -> css selector: .header-search-input --- Input text: ['will']['test']
* [ INFO] 13:30:28 Input in: -> css selector: .header-search-input --- Input text: ['']
* [ INFO] 13:30:28 Try to find following element: By.cssSelector: .foooooooo
* [ERROR] 13:30:44 


Test failed with error: ...

```java
public class EventListenerExample {

    @Rule
    public final SeleniumRule rule = createRuleWithDefaultEventListener();

    private static SeleniumRule createRuleWithDefaultEventListener() {
        return new SeleniumRule()
                .addWebDriverEventListener(new SeleniumEventListener());
    }

    @Test
    public void testEventListenerWithError() {
        WebDriver webDriver = rule.getWebDriver();
        webDriver.get("https://github.com");
        WebElement searchInput = webDriver.findElement(By.cssSelector(".header-search-input"));
        searchInput.sendKeys("will", "test");
        searchInput.sendKeys(Keys.ENTER);
        String searchKeyword = webDriver.findElement(By.cssSelector(".header-search-input")).getAttribute("value");
        assertThat(searchKeyword, is("foooooo"));
    }
}
```

## Project Roadmap
Planned changes:
* Appium integration
* Creating a rule, which supports asserting the browser request using a proxy

## Contribution
Willhaben accepts pull requests or other type of feedback. We do our best to reply in a short period of time.

## License
MIT, Copyright (c) 2017 willhaben internet service GmbH & Co KG
