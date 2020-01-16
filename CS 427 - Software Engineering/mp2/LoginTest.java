package edu.ncsu.csc.itrust.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.WebElement;

import com.meterware.httpunit.HttpUnitOptions;

import edu.ncsu.csc.itrust.enums.TransactionType;

/**
 * Test class for logging into iTrust.
 */
public class LoginTest extends iTrustSeleniumTest {
	
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
	 * Test the behavior expected when a user enters a non numeric
	 * string into the username box. iTrust currently excpects
	 * to see a NumberFormatException.
	 */
	public void testNonNumericLogin() {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get(ADDRESS);
		// log in using the given username and password
		driver.findElement(By.id("j_username")).clear();
		driver.findElement(By.id("j_username")).sendKeys("foo");
		driver.findElement(By.id("j_password")).clear();
		driver.findElement(By.id("j_password")).sendKeys("1234");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertFalse(driver.getPageSource().contains("NumberFormatException"));
	}
	
	/**
	 * Test the standard login feature. After logging in, a user should end up
	 * at the itrust home page, and the login should be logged.
	 */
	public void testLogin() throws Exception {
		// Log in as a patient
		WebDriver driver = login("2", "pw");
		
		// Wait until redirected to page
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
		wait.until(ExpectedConditions.titleIs("iTrust - Patient Home"));
		
		// Verify Logging
		assertLogged(TransactionType.LOGIN_SUCCESS, 2L, 2L, "");
	}
	
	public void testResetPassword() throws Exception {
		
		// Initialize a WebDriver object named "wd"
		WebDriver wd = new Driver();
		
		// 1.  Go to "http://localhost:8080/iTrust/".
        wd.get("http://localhost:8080/iTrust/");
		
		// 2.  Clicks "Reset Password" at the login screen.
        wd.findElement(By.linkText("Reset Password")).click();
		
		// 3.  assertEquals("iTrust - Reset Password", wd.getTitle());
        assertEquals("iTrust - Reset Password", wd.getTitle());
		
		// 4.  Selects "Patient" from the drop-down list.
		Select patient_list = new Select(wd.findElement(By.name("role")));
        patient_list.selectByVisibleText("Patient");
		
		// 5.  Enters "1" in the 'MID' field.
		WebElement mid = wd.findElement(By.name("mid"));
		mid.clear();
		mid.sendKeys("1");
		
		// 6.  Submits the form.
		mid.submit();

		// 7.  assertTrue(wd.getPageSource().contains("what is your favorite color?"));
		assertTrue(wd.getPageSource().contains("what is your favorite color?"));
		
		// 8.  Enters "blue" in the 'answer' field.
		WebElement ans = wd.findElement(By.name("answer"));
		ans.clear();
        ans.sendKeys("blue");
		
		// 9.  Enters "newPw12345" in the 'New Password' field.
        wd.findElement(By.name("password")).sendKeys("newPw12345");
		
		// 10. Enters "newPw12345" in the 'Confirm' field.
        WebElement newPW = wd.findElement(By.name("confirmPassword"));
        newPW.sendKeys("newPw12345");
		
		// 11. Submits the form.
        newPW.submit();
		
		// 12. assertTrue(wd.getPageSource().contains("Password changed"));
        assertTrue(wd.getPageSource().contains("Password changed"));
	}
	
}