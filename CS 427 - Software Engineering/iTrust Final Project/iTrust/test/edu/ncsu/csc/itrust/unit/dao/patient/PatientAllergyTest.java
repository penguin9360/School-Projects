package edu.ncsu.csc.itrust.unit.dao.patient;

import edu.ncsu.csc.itrust.beans.PatientAllergyBean;
import edu.ncsu.csc.itrust.beans.PatientBean;
import edu.ncsu.csc.itrust.beans.ProcedureBean;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import junit.framework.TestCase;

import java.util.List;

public class PatientAllergyTest extends TestCase {
    private PatientDAO patientDAO = TestDAOFactory.getTestInstance().getPatientDAO();
    private TestDataGenerator gen;

    @Override
    protected void setUp() throws Exception {
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.patient1();
        gen.patient2();
        gen.patient3();
        gen.patient60();
        gen.patient61();
        gen.patient62();
        gen.patient63();
        gen.patient64();
    }
    public void testGetPatientsWithZipCode() throws Exception {
        List<PatientAllergyBean> list = patientDAO.getPatientAllergyList("61820","");
        assertEquals(2, list.size());
        assertEquals("f1",list.get(1).getFirstName());
    }
    public void testGetPatientsWithPartialZipCode() throws Exception {
        List<PatientAllergyBean> list = patientDAO.getPatientAllergyList("27608","");
        assertEquals(3, list.size());
        assertTrue(list.get(list.size() - 1).getZip().contains("27608"));
    }
    public void testGetPatientsWithPartialAllergy1() throws Exception {
        List<PatientAllergyBean> list = patientDAO.getPatientAllergyList("","gg");
        assertEquals(1, list.size());
        assertEquals("Egg",list.get(0).getAllergy());
    }
    public void testGetPatientsWithPartialAllergy2() throws Exception {
        List<PatientAllergyBean> list = patientDAO.getPatientAllergyList("","Poll");
        assertEquals(4, list.size());
        assertEquals("Pollen",list.get(0).getAllergy());
    }
    public void testGetPatientsWithPartialAllergyPartialZipCode1() throws Exception {
        List<PatientAllergyBean> list = patientDAO.getPatientAllergyList("27608","Poll");
        assertEquals(1, list.size());
        assertTrue(list.get(0).getAllergy().contains("Poll"));
    }
    public void testGetPatientsWithPartialAllergyPartialZipCode2() throws Exception {
        List<PatientAllergyBean> list = patientDAO.getPatientAllergyList("27608","S");
        assertEquals(1, list.size());
        assertTrue(list.get(0).getZip().contains("27608"));
        assertTrue(list.get(list.size() - 1).getAllergy().contains("S"));
    }
}
