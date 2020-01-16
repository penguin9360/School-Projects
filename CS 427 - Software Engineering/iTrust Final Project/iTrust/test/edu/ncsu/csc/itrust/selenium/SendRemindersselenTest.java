//this is the selenium test//
package edu.ncsu.csc.itrust.selenium;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
public class SendRemindersselenTest extends iTrustSeleniumTest{
	private HtmlUnitDriver driver;
	@Before
	public void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.icd9cmCodes();
		gen.ndCodes();
		gen.hospitals();
		gen.hcp1();
		gen.hcp2();
		gen.hcp3();
		gen.er4();
		gen.patient9();
			
		gen.UC32Acceptance();
		gen.clearLoginFailures();
		driver = new HtmlUnitDriver();
	}

	@Test
	public void testSomeElement() throws Exception{
		driver = (HtmlUnitDriver)login("9900000000", "pw");		
		driver.get("http://localhost:8080/iTrust/");
		Assert.assertTrue(driver.getPageSource().contains(("appointment")));
	}

}
