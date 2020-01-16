package edu.ncsu.csc.itrust.selenium;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

public class ViewCauseOfDeathTrendsTest extends iTrustSeleniumTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        gen.standardData();
        gen.uc20();
    }

    @Test
    public void testViewMale() throws Exception {
        HtmlUnitDriver driver = (HtmlUnitDriver)login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        driver.findElement(By.cssSelector("h2.panel-title")).click();
        driver.findElement(By.linkText("Cause of Death Trends")).click();
        Select gender = new Select(driver.findElement(By.name("GENDER")));
        gender.selectByVisibleText("Male");
        driver.findElement(By.name("START_YEAR")).sendKeys("2000");
        driver.findElement(By.name("END_YEAR")).sendKeys("2100");
        driver.findElement(By.id("searchCauseOfDeathBtn")).submit();
        WebElement elem = driver.findElement(By.className("fTable"));

        WebElement code1 = elem.findElements(By.tagName("td")).get(0);
        assertTrue(code1.getText().contains("70.10"));

        WebElement name1 = elem.findElements(By.tagName("td")).get(1);
        assertTrue(name1.getText().contains("Viral hepatitis"));

        WebElement count1 = elem.findElements(By.tagName("td")).get(2);
        assertTrue(count1.getText().contains("4"));

        WebElement code2 = elem.findElements(By.tagName("td")).get(3);
        assertEquals("250.10", code2.getText());

        WebElement name2 = elem.findElements(By.tagName("td")).get(4);
        assertTrue(name2.getText().contains("Diabetes"));

        WebElement count2 = elem.findElements(By.tagName("td")).get(5);
        assertEquals("1", count2.getText());
    }

    @Test
    public void testViewFemale() throws Exception {
        HtmlUnitDriver driver = (HtmlUnitDriver)login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        driver.findElement(By.cssSelector("h2.panel-title")).click();
        driver.findElement(By.linkText("Cause of Death Trends")).click();
        Select gender = new Select(driver.findElement(By.name("GENDER")));
        gender.selectByVisibleText("Female");
        driver.findElement(By.name("START_YEAR")).sendKeys("2000");
        driver.findElement(By.name("END_YEAR")).sendKeys("2100");
        driver.findElement(By.id("searchCauseOfDeathBtn")).submit();
        WebElement elem = driver.findElement(By.className("fTable"));

        WebElement code1 = elem.findElements(By.tagName("td")).get(0);
        assertTrue(code1.getText().contains("84.50"));

        WebElement name1 = elem.findElements(By.tagName("td")).get(1);
        assertTrue(name1.getText().contains("Malaria"));

        WebElement count1 = elem.findElements(By.tagName("td")).get(2);
        assertTrue(count1.getText().contains("3"));

        WebElement code2 = elem.findElements(By.tagName("td")).get(3);
        assertEquals("35.30", code2.getText());

        WebElement name2 = elem.findElements(By.tagName("td")).get(4);
        assertTrue(name2.getText().contains("Age"));

        WebElement count2 = elem.findElements(By.tagName("td")).get(5);
        assertEquals("1", count2.getText());
    }

    @Test
    public void testViewAll() throws Exception {
        HtmlUnitDriver driver = (HtmlUnitDriver)login("9000000000", "pw");
        assertEquals("iTrust - HCP Home", driver.getTitle());
        driver.findElement(By.cssSelector("h2.panel-title")).click();
        driver.findElement(By.linkText("Cause of Death Trends")).click();
        Select gender = new Select(driver.findElement(By.name("GENDER")));
        gender.selectByVisibleText("Not Specified");
        driver.findElement(By.name("START_YEAR")).sendKeys("2000");
        driver.findElement(By.name("END_YEAR")).sendKeys("2100");
        driver.findElement(By.id("searchCauseOfDeathBtn")).submit();
        WebElement elem = driver.findElement(By.className("fTable"));

        WebElement code1 = elem.findElements(By.tagName("td")).get(0);
        assertTrue(code1.getText().contains("70.10"));

        WebElement name1 = elem.findElements(By.tagName("td")).get(1);
        assertTrue(name1.getText().contains("Viral hepatitis"));

        WebElement count1 = elem.findElements(By.tagName("td")).get(2);
        assertTrue(count1.getText().contains("4"));

        WebElement code2 = elem.findElements(By.tagName("td")).get(3);
        assertTrue(code2.getText().contains("84.50"));

        WebElement name2 = elem.findElements(By.tagName("td")).get(4);
        assertTrue(name2.getText().contains("Malaria"));

        WebElement count2 = elem.findElements(By.tagName("td")).get(5);
        assertTrue(count2.getText().contains("3"));
    }
}
