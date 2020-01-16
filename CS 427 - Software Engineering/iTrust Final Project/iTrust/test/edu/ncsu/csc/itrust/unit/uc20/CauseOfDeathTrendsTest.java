package edu.ncsu.csc.itrust.unit.uc20;

import edu.ncsu.csc.itrust.action.CauseOfDeathAction;
import edu.ncsu.csc.itrust.beans.CauseOfDeathBean;
import edu.ncsu.csc.itrust.beans.TransactionBean;
import edu.ncsu.csc.itrust.dao.mysql.CauseOfDeathTrendsDAO;
import edu.ncsu.csc.itrust.dao.mysql.TransactionDAO;
import edu.ncsu.csc.itrust.enums.Gender;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class CauseOfDeathTrendsTest extends TestCase {
    private CauseOfDeathTrendsDAO causeOfDeathDAO = TestDAOFactory.getTestInstance().getCauseOfDeathTrendsDAO();
    private TestDataGenerator gen;
    private CauseOfDeathAction causeOfDeathAction = new CauseOfDeathAction(TestDAOFactory.getTestInstance(), 9000000000L);
    private TransactionDAO transDAO = TestDAOFactory.getTestInstance().getTransactionDAO();

    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.uc20();
        gen.icd9cmCodes();
    }

    @Test
    public void testLog() throws DBException, ITrustException {
        causeOfDeathAction.topTwoCommon(2011, 2015, Gender.NotSpecified);
        List<TransactionBean> list = transDAO.getAllTransactions();
        boolean containsTransaction = false;
        for(TransactionBean trans : list) {
            if(trans.getTransactionType().getCode() == 2000) {
                containsTransaction = true;
                break;
            }
        }
        assertTrue(containsTransaction);
    }

    @Test
    public void testMostCommonDeathAll() throws DBException, ITrustException {
        List<CauseOfDeathBean> list = causeOfDeathDAO.getCauseOfDeath(9000000000L, 2011, 2015, Gender.NotSpecified);
        assertEquals(2, list.size());
        assertEquals(list.get(0).getCode(), "70.10");
        assertEquals(list.get(1).getCode(), "84.50");
        assertEquals(list.get(0).getCount(), 4);
        assertEquals(list.get(1).getCount(), 3);
    }

    @Test
    public void testMostCommonDeathMale() throws DBException, ITrustException {
        List<CauseOfDeathBean> list = causeOfDeathDAO.getCauseOfDeath(9000000000L, 2011, 2015, Gender.Male);
        assertEquals(2, list.size());
        assertEquals("70.10", list.get(0).getCode());
        assertEquals("72.00", list.get(1).getCode());
        assertEquals(4, list.get(0).getCount());
        assertEquals(1, list.get(1).getCount());
    }

    @Test
    public void testMostCommonDeathFemale() throws DBException, ITrustException {
        List<CauseOfDeathBean> list = causeOfDeathDAO.getCauseOfDeath(9000000000L, 2011, 2015, Gender.Female);
        assertEquals(2, list.size());
        assertEquals("84.50", list.get(0).getCode());
        assertEquals("35.30", list.get(1).getCode());
        assertEquals(3, list.get(0).getCount());
        assertEquals(1, list.get(1).getCount());
    }
}
