package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

public class requestBiosurveillanceTest extends iTrustSeleniumTest{

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
        gen.influenza_epidemic();
        gen.malaria_epidemic();
    }

    /**
     * Test if the the function work on specific known Malaria input
     */
    public void testMalariaEpidemicsTrue() throws Exception{
        HtmlUnitDriver driver = (HtmlUnitDriver)login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        driver.findElement(By.cssSelector("h2.panel-title")).click();
        driver.findElement(By.linkText("Request Biosurveillance")).click();

        assertEquals("iTrust - Request Biosurveillance Page (UC 14)", driver.getTitle());

        assertTrue(driver.getPageSource().contains("ICD Code"));
        assertTrue(driver.getPageSource().contains("Zip Code"));
        assertTrue(driver.getPageSource().contains("Date"));
        assertTrue(driver.getPageSource().contains("Option"));

        driver.findElement(By.name("ICDCode")).sendKeys("84.50");
        driver.findElement(By.name("ZipCode")).sendKeys("27601");
        driver.findElement(By.name("Date")).sendKeys("11/10/2019");
        Select option = new Select(driver.findElement(By.name("Option")));
        option.selectByVisibleText("Epidemics");


        driver.findElement(By.id("submitButton")).submit();
        WebElement elem = driver.findElement(By.className("col-sm-12"));

        assertEquals("Malaria diagnosis: true",elem.findElements(By.className("panel-body")).get(0).getText());
    }

    /**
     * Test if the the function work on specific known Influenza input
     */
    public void testInfluenzaEpidemicsTrue() throws Exception{
        HtmlUnitDriver driver = (HtmlUnitDriver)login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        driver.findElement(By.cssSelector("h2.panel-title")).click();
        driver.findElement(By.linkText("Request Biosurveillance")).click();

        assertEquals("iTrust - Request Biosurveillance Page (UC 14)", driver.getTitle());

        assertTrue(driver.getPageSource().contains("ICD Code"));
        assertTrue(driver.getPageSource().contains("Zip Code"));
        assertTrue(driver.getPageSource().contains("Date"));
        assertTrue(driver.getPageSource().contains("Option"));

        driver.findElement(By.name("ICDCode")).sendKeys("487.00");
        driver.findElement(By.name("ZipCode")).sendKeys("27601");
        driver.findElement(By.name("Date")).sendKeys("11/10/2019");
        Select option = new Select(driver.findElement(By.name("Option")));
        option.selectByVisibleText("Epidemics");


        driver.findElement(By.id("submitButton")).submit();
        WebElement elem = driver.findElement(By.className("col-sm-12"));

        assertEquals("Influenza diagnosis: true",elem.findElements(By.className("panel-body")).get(0).getText());
    }

    /**
     * Test if the error message is displayed with an invalid ICD code
     */
    public void testInvalidDiagnosesCode() throws Exception{//ICD Code = 250.10(invalid for Epidemics), ZipCode = 27601, Date = Oct.20, 1985
        HtmlUnitDriver driver = (HtmlUnitDriver)login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        driver.findElement(By.cssSelector("h2.panel-title")).click();
        driver.findElement(By.linkText("Request Biosurveillance")).click();

        assertEquals("iTrust - Request Biosurveillance Page (UC 14)", driver.getTitle());

        assertTrue(driver.getPageSource().contains("ICD Code"));
        assertTrue(driver.getPageSource().contains("Zip Code"));
        assertTrue(driver.getPageSource().contains("Date"));
        assertTrue(driver.getPageSource().contains("Option"));

        driver.findElement(By.name("ICDCode")).sendKeys("427.00");
        driver.findElement(By.name("ZipCode")).sendKeys("27601");
        driver.findElement(By.name("Date")).sendKeys("11/10/2019");
        Select option = new Select(driver.findElement(By.name("Option")));
        option.selectByVisibleText("Epidemics");


        driver.findElement(By.id("submitButton")).submit();
        WebElement elem = driver.findElement(By.className("col-sm-12"));

        assertEquals("The ICD code is invalid. Please try again.",elem.findElements(By.className("panel-body")).get(0).getText());
    }


    /**
     * Test if the error message is displayed with an invalid zip code that is not in the database
     */
    public void testInvalidZipCode()throws Exception{//ICD Code = 250.10, ZipCode = 12345(invalid), Date = Oct.20, 1985
        HtmlUnitDriver driver = (HtmlUnitDriver)login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        driver.findElement(By.cssSelector("h2.panel-title")).click();
        driver.findElement(By.linkText("Request Biosurveillance")).click();

        assertEquals("iTrust - Request Biosurveillance Page (UC 14)", driver.getTitle());

        assertTrue(driver.getPageSource().contains("ICD Code"));
        assertTrue(driver.getPageSource().contains("Zip Code"));
        assertTrue(driver.getPageSource().contains("Date"));
        assertTrue(driver.getPageSource().contains("Option"));

        driver.findElement(By.name("ICDCode")).sendKeys("487.00");
        driver.findElement(By.name("ZipCode")).sendKeys("52427");
        driver.findElement(By.name("Date")).sendKeys("11/10/2019");
        Select option = new Select(driver.findElement(By.name("Option")));
        option.selectByVisibleText("Epidemics");


        driver.findElement(By.id("submitButton")).submit();
        WebElement elem = driver.findElement(By.className("col-sm-12"));

        assertEquals("The zip code is invalid. Please try again.",elem.findElements(By.className("panel-body")).get(0).getText());
    }

    /**
     * Test if the error message is displayed with an invalid future date
     */
    public void testInvalidDate()throws Exception{//ICD Code = 250.10, ZipCode = 27061, Date = Nov.20, 2050(invalid future date)
        HtmlUnitDriver driver = (HtmlUnitDriver)login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        driver.findElement(By.cssSelector("h2.panel-title")).click();
        driver.findElement(By.linkText("Request Biosurveillance")).click();

        assertEquals("iTrust - Request Biosurveillance Page (UC 14)", driver.getTitle());

        assertTrue(driver.getPageSource().contains("ICD Code"));
        assertTrue(driver.getPageSource().contains("Zip Code"));
        assertTrue(driver.getPageSource().contains("Date"));
        assertTrue(driver.getPageSource().contains("Option"));

        driver.findElement(By.name("ICDCode")).sendKeys("487.00");
        driver.findElement(By.name("ZipCode")).sendKeys("27601");
        driver.findElement(By.name("Date")).sendKeys("11/10/2020");
        Select option = new Select(driver.findElement(By.name("Option")));
        option.selectByVisibleText("Epidemics");


        driver.findElement(By.id("submitButton")).submit();
        WebElement elem = driver.findElement(By.className("col-sm-12"));

        assertEquals("The date is invalid. Please try again.",elem.findElements(By.className("panel-body")).get(0).getText());
    }
}
