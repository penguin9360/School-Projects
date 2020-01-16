package edu.ncsu.csc.itrust.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.meterware.httpunit.HttpUnitOptions;

import edu.ncsu.csc.itrust.enums.TransactionType;

/**
 * Test class for logging into iTrust.
 */
public class PreregisterTest extends iTrustSeleniumTest {

    /*
     * The URL for iTrust, change as needed
     */
    /**ADDRESS*/
    public static final String ADDRESS = "http://localhost:8080/iTrust/";

    /**
     * Set up for testing.
     */
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
        // turn off htmlunit warnings
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
        HttpUnitOptions.setExceptionsThrownOnScriptError(false);
    }

    /**
     * Tear down from testing.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test the Preregister user feature.
     * Puts in standard required info and registers
     */
    public void testPreregister() throws Exception {
        // Log in as a patient
        HtmlUnitDriver wd = new HtmlUnitDriver();

        wd.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        wd.get(ADDRESS);
        wd.findElement(By.linkText("Pre Register Here!")).click();
        assertEquals("User Register", wd.getTitle());
        wd.findElement(By.name("firstName")).sendKeys("First");
        wd.findElement(By.name("lastName")).sendKeys("Last");
        wd.findElement(By.name("email")).sendKeys("email@domain.com");
        wd.findElement(By.name("password")).sendKeys("password");
        wd.findElement(By.name("passwordVerify")).sendKeys("password");
        wd.findElement(By.cssSelector("input[type=\"submit\"]")).click();
        assertTrue(wd.getPageSource().contains("MID"));
    }

    public void testMissingRequiredPreregister() throws Exception {
        // Log in as a patient
        HtmlUnitDriver wd = new HtmlUnitDriver();

        wd.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        wd.get(ADDRESS);
        wd.findElement(By.linkText("Pre Register Here!")).click();
        assertEquals("User Register", wd.getTitle());
        wd.findElement(By.name("firstName")).sendKeys("First");
        wd.findElement(By.name("lastName")).sendKeys("Last");
        wd.findElement(By.name("password")).sendKeys("password");
        wd.findElement(By.name("passwordVerify")).sendKeys("password");
        wd.findElement(By.cssSelector("input[type=\"submit\"]")).click();
        assertFalse(wd.getPageSource().contains("Pre registered user!"));
    }

    public void testBadPasswordVerifyPreregister() throws Exception {
        // Log in as a patient
        HtmlUnitDriver wd = new HtmlUnitDriver();

        wd.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        wd.get(ADDRESS);
        wd.findElement(By.linkText("Pre Register Here!")).click();
        assertEquals("User Register", wd.getTitle());
        wd.findElement(By.name("firstName")).sendKeys("First");
        wd.findElement(By.name("lastName")).sendKeys("Last");
        wd.findElement(By.name("email")).sendKeys("email@domain.com");
        wd.findElement(By.name("password")).sendKeys("password");
        wd.findElement(By.name("passwordVerify")).sendKeys("diffPassword");
        wd.findElement(By.cssSelector("input[type=\"submit\"]")).click();
        assertFalse(wd.getPageSource().contains("Pre registered user!"));
    }
}