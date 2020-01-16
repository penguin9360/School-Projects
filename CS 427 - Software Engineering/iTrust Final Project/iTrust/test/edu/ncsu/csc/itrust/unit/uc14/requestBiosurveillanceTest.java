package edu.ncsu.csc.itrust.unit.uc14;

import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.EpidemicDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

public class requestBiosurveillanceTest {
    private DAOFactory factory = TestDAOFactory.getTestInstance();
    private TestDataGenerator generator = new TestDataGenerator();
    private EpidemicDAO epidemicDAO;

    @Before
    public void setUp() throws Exception {
        epidemicDAO = new EpidemicDAO(factory);
        generator.clearAllTables();
        generator.standardData();
        generator.influenza_epidemic();
		generator.malaria_epidemic();
    }

    @Test
    public void testMalariaThreshold() throws ITrustException {
        assertEquals(1.25, epidemicDAO.malariaEpidemicThreshold("27601", Date.valueOf("2019-11-09")),0.001);
    }

    @Test
    public void testMalariaEpidemic() throws DBException, ITrustException {
        assertTrue(epidemicDAO.isMalariaEpidemic("27601", Date.valueOf("2019-11-10")));
    }

    @Test
    public void testNotMalariaEpidemic() throws DBException, ITrustException {
        assertFalse(epidemicDAO.isMalariaEpidemic("27601", Date.valueOf("2019-11-09")));
    }

    @Test
    public void testNotMalariaEpidemicZip() throws ITrustException {
        assertFalse(epidemicDAO.isMalariaEpidemic("52427", Date.valueOf("2019-11-10")));
    }


    @Test
    public void testInfluenzaThreshold() {
        assertEquals(22, Math.round(epidemicDAO.influenzaEpidemicThreshold(Date.valueOf("2019-11-10"))));
    }

    @Test
    public void testInfluenzaEpidemic() throws ITrustException {
        assertTrue(epidemicDAO.isInfluenzaEpidemic("27601", Date.valueOf("2019-11-10")));
    }

    @Test
    public void testNotInfluenzaEpidemic() throws ITrustException {
        assertFalse(epidemicDAO.isInfluenzaEpidemic("27601", Date.valueOf("2019-11-09")));
    }


    @Test
    public void testNotInfluenzaEpidemicZip() throws ITrustException {
        assertFalse(epidemicDAO.isInfluenzaEpidemic("52427", Date.valueOf("2019-11-10")));
    }


//    @Test
//    public void testDiagnosisRegion() throws ITrustException {
//        try {
//            List<Integer> l = epidemicDAO.diagnosisRegion("27601", new Date(System.currentTimeMillis()), "487.00");
//            assertEquals(100, l.get(6).intValue());
//            assertEquals(200, l.get(4).intValue());
//        } catch (DBException e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void testDiagnosisState(){
//        try {
//            List<Integer> l = epidemicDAO.diagnosisState("27601", new Date(System.currentTimeMillis()), "487.00");
//            assertEquals(200, l.get(4).intValue());
//            assertEquals(100, l.get(6).intValue());
//        } catch (ITrustException e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void testDiagnosisAll(){
//        try {
//            List<Integer> l = epidemicDAO.diagnosisAll(new Date(System.currentTimeMillis()), "84.5");
//            assertEquals(11, l.get(4).intValue());
//            assertEquals(6, l.get(6).intValue());
//        } catch (ITrustException e) {
//            fail();
//        }
//    }

    @Test
    public void testICDCodeExists(){
        assertTrue(epidemicDAO.checkIfDiagnosisCodeExists("487.00"));
    }

    @Test
    public void testICDCodeNotExists(){
        assertFalse(epidemicDAO.checkIfDiagnosisCodeExists("427.00"));
    }

    @Test
    public void testZipCodeExists(){
        assertTrue(epidemicDAO.checkIfZipCodeExists("60123"));
    }

    @Test
    public void testZipCodeNotExists(){
        assertFalse(epidemicDAO.checkIfDiagnosisCodeExists("52427"));
    }
}
