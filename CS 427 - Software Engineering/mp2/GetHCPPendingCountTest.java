package edu.ncsu.csc.itrust.unit.dao.labprocedure;

import java.util.List;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.LabProcedureBean;
import edu.ncsu.csc.itrust.dao.mysql.LabProcedureDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;

public class GetHCPPendingCountTest extends TestCase {
    private LabProcedureDAO lpDAO = TestDAOFactory.getTestInstance().getLabProcedureDAO();
    private TestDataGenerator gen;

    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.labProcedures();
    }

    public void testGetHCPPendingCount() throws Exception {
        int count = lpDAO.getHCPPendingCount(9000000000L);
        assertEquals(0, count);

        //count = lpDAO.getHCPPendingCount(8000000000L);
        //assertEquals(0, count);
    }
}