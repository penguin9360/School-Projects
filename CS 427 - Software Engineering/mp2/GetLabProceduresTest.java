package edu.ncsu.csc.itrust.unit.dao.labprocedure;

import java.util.List;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.LabProcedureBean;
import edu.ncsu.csc.itrust.dao.mysql.LabProcedureDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;

public class GetLabProceduresTest extends TestCase {
    private LabProcedureDAO lpDAO = TestDAOFactory.getTestInstance().getLabProcedureDAO();
    private TestDataGenerator gen;

    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.labProcedures();
    }

    public void testGetLabProcedures() throws Exception {
        List<LabProcedureBean> procedures = lpDAO.getLabProcedures(9000000000L,1L);
        assertEquals(0, procedures.size());
        //assertEquals("10763-1", procedures.get(0).getLoinc());
        //assertEquals("10666-6", procedures.get(1).getLoinc());
        //assertEquals("10640-1", procedures.get(2).getLoinc());
    }

    public void testNullMid() throws Exception {
        try {
            lpDAO.getLabProcedures(0L, 666L);
            fail();
        } catch (DBException e) {
            assertEquals("HCP id cannot be null", e.getExtendedMessage());
        }
    }

    public void testNullPid() throws Exception {
        try {
            lpDAO.getLabProcedures(666L, 0L);
            fail();
        } catch (DBException e) {
            assertEquals("HCP id cannot be null", e.getExtendedMessage());
        }
    }

}