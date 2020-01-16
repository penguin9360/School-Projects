package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AllergyWarningTest extends iTrustSeleniumTest {

    private WebDriver driver = null;

    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();

    }

    protected void tearDown() throws Exception {
        gen.clearAllTables();
    }

    public void testFilterList() throws Exception {
        //log in as admin
        driver = login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());

        //Click the Send Allergy Warnings Link
        driver.findElement(By.linkText("Send Allergy Warnings")).click();
        assertEquals("iTrust - Send Allergy Warning", driver.getTitle());

        assertFalse(driver.getPageSource().contains("Andy Programmer"));
        driver.findElement(By.xpath("//input[@name='ALLERGY']")).sendKeys("pollen");
        driver.findElement(By.id("filterTableBtn")).click();
        assertTrue(driver.getPageSource().contains("Andy Programmer"));

    }

    public void testSendWarning() throws Exception{
        //log in as Random Person
        driver = login("2", "pw");
        assertEquals("iTrust - Patient Home", driver.getTitle());

        //Click the Message Inbox Link
        driver.findElement(By.linkText("Message Inbox")).click();
        assertEquals("iTrust - View My Message", driver.getTitle());

        //Check contains message from admin 'Shape Shifter'
        assertFalse(driver.getPageSource().contains("Shape Shifter"));

        //log in as admin
        driver = login("9000000001", "pw");
        assertEquals("iTrust - Admin Home", driver.getTitle());

        //Click the Send Allergy Warnings Link
        driver.findElement(By.linkText("Send Allergy Warnings")).click();
        assertEquals("iTrust - Send Allergy Warning", driver.getTitle());

        //Send warnings to all patients with allergy 'pollen'
        assertFalse(driver.getPageSource().contains("Warnings sent."));
        driver.findElement(By.xpath("//input[@name='ALLERGY']")).sendKeys("pollen");
        driver.findElement(By.id("sendWarningBtn")).click();
        assertTrue(driver.getPageSource().contains("Warnings sent."));
        driver.close();

        //log in as Random Person
        driver = login("2", "pw");
        assertEquals("iTrust - Patient Home", driver.getTitle());

        //Click the Message Inbox Link
        driver.findElement(By.linkText("Message Inbox")).click();
        assertEquals("iTrust - View My Message", driver.getTitle());

        //Check contains message from admin 'Shape Shifter'
        assertTrue(driver.getPageSource().contains("Shape Shifter"));
    }

}
