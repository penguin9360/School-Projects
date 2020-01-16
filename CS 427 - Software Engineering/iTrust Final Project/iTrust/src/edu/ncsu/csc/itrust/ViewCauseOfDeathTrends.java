//package edu.ncsu.csc.itrust;
//
//import com.meterware.httpunit.WebConversation;
//import com.meterware.httpunit.WebForm;
//import com.meterware.httpunit.WebResponse;
//
//import edu.ncsu.csc.itrust.datagenerators.TestDataGenerator;
//import edu.ncsu.csc.itrust.http.iTrustHTTPTest;
//
//public class ViewCauseOfDeathTrends {
//    private TestDataGenerator gen = new TestDataGenerator();
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        gen.clearAllTables();
//        gen.standardData();
//    }
//
//    /**
//     * Test if the function works when we select female as parameter.
//     */
//    public void testViewCODTrendsFemale() throws Exception{
//        //Log in as Kelly Doctor
//        WebConversation wc = login("9000000000", "pw");
//        WebResponse wr = wc.getCurrentPage();
//
//        //Go to the CODTrend page
//        wr = wr.getLinkWith("View Cause of Death Trends").click();
//        assertEquals("iTrust - View Cause of Death Trends", wr.getTitle());
//
//        WebForm CODTrends = wr.getForms()[0];
//
//        CODTrends.setParameter("SelectCoverage", "Female");
//        CODTrends.setParameter("StartingYear", "2005");
//        CODTrends.setParameter("EndingYear", "2014");
//        CODTrends.submit();
//
//        wr = wc.getCurrentPage();
//        assertTrue(wr.getText().contains("1. Name: Chicken Pox, Code: 35.00, Number of Deaths: 1"));
//        assertTrue(wr.getText().contains("2. Name: Mumps, Code: 72.00, Number of Deaths: 1"));
//    }
//
//    /**
//     * Test if the function works when we select all as parameter.
//     */
//    public void testViewCODTrendsAll() throws Exception{
//        WebConversation wc = login("9000000000", "pw");
//        WebResponse wr = wc.getCurrentPage();
//
//        wr = wr.getLinkWith("View Cause of Death Trends").click();
//        assertEquals("iTrust - View Cause of Death Trends", wr.getTitle());
//
//        WebForm CODTrends = wr.getForms()[0];
//
//        CODTrends.setParameter("SelectCoverage", "All");
//        CODTrends.setParameter("StartingYear", "2005");
//        CODTrends.setParameter("EndingYear", "2014");
//        CODTrends.submit();
//
//        wr = wc.getCurrentPage();
//        assertTrue(wr.getText().contains("1. Name: Malaria, Code: 84.50, Number of Deaths: 3"));
//    }
//
//    /**
//     * Test if the function works when we select male as parameter.
//     */
//    public void testViewCODTrendsMale() throws Exception{
//        WebConversation wc = login("9000000000", "pw");
//        WebResponse wr = wc.getCurrentPage();
//
//        wr = wr.getLinkWith("View Cause of Death Trends").click();
//        assertEquals("iTrust - View Cause of Death Trends", wr.getTitle());
//
//        WebForm CODTrends = wr.getForms()[0];
//
//        CODTrends.setParameter("SelectCoverage", "Male");
//        CODTrends.setParameter("StartingYear", "2005");
//        CODTrends.setParameter("EndingYear", "2014");
//        CODTrends.submit();
//
//        wr = wc.getCurrentPage();
//        assertTrue(wr.getText().contains("1. Name: Malaria, Code: 84.50, Number of Deaths: 3"));
//        assertTrue(wr.getText().contains("2. Name: Chicken Pox, Code: 35.00, Number of Deaths: 2"));
//    }
//
//    /**
//     * Test if the function works when we input and ending year that's earlier than the starting year
//     */
//    public void testInvalidDate() throws Exception{
//        WebConversation wc = login("9000000000", "pw");
//        WebResponse wr = wc.getCurrentPage();
//
//        wr = wr.getLinkWith("View Cause of Death Trends").click();
//        assertEquals("iTrust - View Cause of Death Trends", wr.getTitle());
//
//        WebForm CODTrends = wr.getForms()[0];
//        //The page shouldn't return any record if the starting year is later than ending year
//        CODTrends.setParameter("SelectCoverage", "All");
//        CODTrends.setParameter("StartingYear", "2014");
//        CODTrends.setParameter("EndingYear", "2005");
//        CODTrends.submit();
//
//        wr = wc.getCurrentPage();
//        assertFalse(wr.getText().contains("1. Name: Malaria, Code: 84.50, Number of Deaths: 3"));
//        assertFalse(wr.getText().contains("2. Name: Chicken Pox, Code: 35.00, Number of Deaths: 2"));
//    }
//}
