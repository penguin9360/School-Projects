package edu.ncsu.csc.itrust.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

public class ViewTransactionLogTest extends iTrustSeleniumTest {

    private HtmlUnitDriver driver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen.clearAllTables();
        gen.standardData();
    }

    /**
     * Tests whether a tester can view Transaction Logs
     * @throws Exception
     */
    public void testViewTransactionLogsFromTester() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9999999999", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();

        assertTrue(driver.getPageSource().contains("Transaction Log"));

    }

    /**
     * Tests whether an Admin can view Transaction Logs
     * @throws Exception
     */
    public void testViewTransactionLogsFromAdmin() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9000000001", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();

        assertTrue(driver.getPageSource().contains("Transaction Log"));
    }

    /**
     * Insure logging in shows up in transaction logs
     * @throws Exception
     */
    public void testTransactionLogsIncludeLogin() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9999999999", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();
        driver.findElement(By.xpath("//input[@name='view']")).click();

        assertTrue(driver.getPageSource().contains("LOGIN_SUCCESS"));
        assertTrue(driver.getPageSource().contains("9999999999"));
        assertTrue(driver.getPageSource().contains("Login Succeded"));
    }

    /**
     * Filter to a different role than logged in user and make sure user logging in
     * does not show up in transaction logs
     * @throws Exception
     */
    public void testTransactionLogRoleFilteringExcludesLogin() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9999999999", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();

        driver.findElement(By.xpath("//select[@name='roles']/option[2]")).click();
        driver.findElement(By.xpath("//input[@name='view']")).click();

        assertFalse(driver.getPageSource().contains("Login Succeded"));
    }

    /**
     * Filter to a different secondary role than logged in user and make sure user logging in
     * does not show up in transaction logs
     * @throws Exception
     */
    public void testTransactionLogSecondaryRoleFilteringExcludesLogin() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9999999999", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();

        driver.findElement(By.xpath("//select[@name='roles']/option[2]")).click();

        driver.findElement(By.xpath("//input[@name='view']")).click();

        assertFalse(driver.getPageSource().contains("Login Succeded"));
    }

    /**
     * Filter to a different transaction type than logging in and make sure logging in
     * does not show up in transaction log
     * @throws Exception
     */
    public void testTransactionLogTypeFilteringExcludesLogin() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9999999999", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();

        driver.findElement(By.xpath("//select[@name='trans_type']/option[2]")).click();

        driver.findElement(By.xpath("//input[@name='view']")).click();

        assertFalse(driver.getPageSource().contains("Login Succeded"));
    }

    /**
     * Filter role to tester and make sure user logging in shows up in
     * transaction log
     * @throws Exception
     */
    public void testTransactionLogRoleFilteringIncludesLogin() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9999999999", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();

        driver.findElement(By.xpath("//select[@name='roles']/option[10]")).click();

        driver.findElement(By.xpath("//input[@name='view']")).click();

        assertTrue(driver.getPageSource().contains("LOGIN_SUCCESS"));
        assertTrue(driver.getPageSource().contains("9999999999"));
        assertTrue(driver.getPageSource().contains("Login Succeded"));
    }

    /**
     * Filter secondary role to tester and make sure user logging in shows up
     * in transaction log
     * @throws Exception
     */
    public void testTransactionLogSecondaryRoleFilteringIncludesLogin() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9999999999", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();

        driver.findElement(By.xpath("//select[@name='2nd_roles']/option[10]")).click();

        driver.findElement(By.xpath("//input[@name='view']")).click();

        assertTrue(driver.getPageSource().contains("LOGIN_SUCCESS"));
        assertTrue(driver.getPageSource().contains("9999999999"));
        assertTrue(driver.getPageSource().contains("Login Succeded"));
    }

    /**
     * Filter transaction type to LOGIN_SUCCESS and make sure user logging in
     * shows up in transaction log
     * @throws Exception
     */
    public void testTransactionLogTypeFilteringIncludesLogin() throws Exception {
        // Login
        driver = (HtmlUnitDriver)login("9999999999", "pw");
        driver.findElement(By.xpath("//div[@class='iTrustMenuContents']/div/div"))
                .click();
        driver.findElement(
                By.linkText("View Filtered Transaction Log")).click();

        driver.findElement(By.xpath("//select[@name='trans_type']/option[@value='300']")).click();

        driver.findElement(By.xpath("//input[@name='view']")).click();

        assertTrue(driver.getPageSource().contains("LOGIN_SUCCESS"));
        assertTrue(driver.getPageSource().contains("9999999999"));
        assertTrue(driver.getPageSource().contains("Login Succeded"));
    }
}

















