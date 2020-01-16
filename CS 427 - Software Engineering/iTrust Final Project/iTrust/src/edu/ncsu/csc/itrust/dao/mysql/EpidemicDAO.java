package edu.ncsu.csc.itrust.dao.mysql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;

public class EpidemicDAO {

    private DAOFactory factory;

    public EpidemicDAO(DAOFactory factory) {
        this.factory = factory;
    }

    /**
     * Finds if a malaria epidemic is happening for a given zipcode and date
     * @param zipcode Zipcode to search for. Query uses first three digits
     * @param d Date to determine if a malaria epidemic is happening
     * @return True if a malaria epidemic is happening, false otherwise
     */
    public boolean isMalariaEpidemic(String zipcode, Date d) throws ITrustException {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        String officeDiagnosis = "(SELECT VisitID FROM ovdiagnosis WHERE ICDCode = 84.50)";
        String patientZip = "(SELECT MID FROM patients WHERE zip LIKE ?)";
        double threshold = this.malariaEpidemicThreshold(zipcode, d);
        boolean ret = false;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT "
                    + "COUNT(officevisits.VisitDate) as NumTotalCases FROM officevisits WHERE officevisits.ID = ANY "
                    + officeDiagnosis + "AND officevisits.PatientID = ANY " + patientZip + " AND "
                    + "officevisits.VisitDate <= ? AND officevisits.VisitDate >= ?");

            ps.setString(1, zipcode.substring(0, 3) + "%");
            ps.setDate(2, new Date(d.getTime() - (8L * 24 * 3600 * 1000)));
            ps.setDate(3, new Date(d.getTime() - (14L * 24 * 3600 * 1000)));

            ResultSet rs;
            rs = ps.executeQuery();
            if(rs.next()){
                if(rs.getInt("NumTotalCases") > threshold){
                    ps2 = conn.prepareStatement("SELECT "
                            + "COUNT(officevisits.VisitDate) as NumTotalCases FROM officevisits WHERE officevisits.ID = ANY "
                            + officeDiagnosis + "AND officevisits.PatientID = ANY " + patientZip + " AND "
                            + "officevisits.VisitDate <= ? AND officevisits.VisitDate >= ?");

                    ps2.setString(1, zipcode.substring(0, 3) + "%");
                    ps2.setDate(2, new Date(d.getTime() - ((long) 24 * 3600 * 1000)));
                    ps2.setDate(3, new Date(d.getTime() - (7L * 24 * 3600 * 1000)));

                    ResultSet rs2;
                    rs2 = ps2.executeQuery();

                    if(rs2.next()){
                        if(rs2.getInt("NumTotalCases") > threshold){
                            ret = true;
                        }
                    }
                }
            }
            return ret;
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    /**
     * Calculates the malaria epidemic threshold
     * @param zipcode Zipcode to search for
     * @param d Date to determine the threshold
     * @return A number representing the threshold for the specified parameters
     */
    public double malariaEpidemicThreshold(String zipcode, Date d) throws ITrustException {
        Connection conn = null;
        PreparedStatement ps = null;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        int yearNumber = c.get(Calendar.YEAR);
        String officeDiagnosis = "(SELECT VisitID FROM ovdiagnosis WHERE ICDCode = 84.50)";
        String patientZip = "(SELECT MID FROM patients WHERE zip LIKE '" + zipcode.substring(0, 3) + "%')";
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT MIN(officevisits.VisitDate) as LowYear, "
                    + "COUNT(officevisits.VisitDate) as NumTotalCases FROM officevisits WHERE officevisits.ID = ANY "
                    + officeDiagnosis + "AND officevisits.PatientID = ANY " + patientZip + " AND "
                    + "YEAR(officevisits.VisitDate) < YEAR(?)");
            ps.setDate(1, d);
            ResultSet rs;

            rs = ps.executeQuery();
            if(rs.next()){
                double yearSpan = yearNumber - rs.getInt("LowYear");
                double threshold = rs.getInt("NumTotalCases")/yearSpan;
                return threshold;
            }

            return -1;

        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    /**
     * Determines if an influenza epidemic is happening for a given zipcode and date
     * @param zipcode Zipcode representing area to search for. Query uses first three digits
     * @param d Date to determine if influenza epidemic is happening
     * @return True if an influenza epidemic is occurring for the specified parameters, false otherwise
     */
    public boolean isInfluenzaEpidemic(String zipcode, Date d) throws ITrustException {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement ps2;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        String officeDiagnosis = "(SELECT VisitID FROM ovdiagnosis WHERE ICDCode = 487.00)";
        String patientZip = "(SELECT MID FROM patients WHERE zip LIKE '" + zipcode.substring(0, 3) + "%')";
        double threshold = this.influenzaEpidemicThreshold(d);
        boolean ret = false;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT "
                    + "COUNT(officevisits.VisitDate) as NumTotalCases FROM officevisits WHERE officevisits.ID = ANY "
                    + officeDiagnosis + "AND officevisits.PatientID = ANY " + patientZip + " AND "
                    + "officevisits.VisitDate <= ? AND officevisits.VisitDate >= ?");


            ps.setDate(1, new Date(d.getTime() - (8L * 24 * 3600 * 1000)));
            ps.setDate(2, new Date(d.getTime() - (14L * 24 * 3600 * 1000)));

            ResultSet rs;
            rs = ps.executeQuery();
            if(rs.next()){
                if(rs.getInt("NumTotalCases") > threshold){
                    ps2 = conn.prepareStatement("SELECT "
                            + "COUNT(officevisits.VisitDate) as NumTotalCases FROM officevisits WHERE officevisits.ID = ANY "
                            + officeDiagnosis + "AND officevisits.PatientID = ANY " + patientZip + " AND "
                            + "officevisits.VisitDate <= ? AND  officevisits.VisitDate >= ?");
                    ps2.setDate(1, new Date(d.getTime() - ((long) 24 * 3600 * 1000)));
                    ps2.setDate(2, new Date(d.getTime() - (7L * 24 * 3600 * 1000)));

                    ResultSet rs2;
                    rs2 = ps2.executeQuery();

                    if(rs2.next()){
                        if(rs2.getInt("NumTotalCases") > threshold){
                            ret = true;
                        }
                    }
                }
            }
            return ret;
        } catch (SQLException e) {
            System.out.println("Error2: " + e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    /**
     * Gets the threshold to determine if an influenza epidemic is occurring
     * @param d Date to determine for
     * @return A number representing the influenza epidemic threshold
     */
    public double influenzaEpidemicThreshold(Date d){
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        int weekNumber = c.get(Calendar.WEEK_OF_YEAR);
        return calcThreshold(weekNumber);
    }

    /**
     * Calculates the influenza epidemic threshold for a given week number
     * @param weekNumber Week number of the year to determine the threshold
     * @return A number representing the influenza epidemic threshold
     */
    private double calcThreshold(double weekNumber) {
        return 5.34 + 0.271 * weekNumber + 3.45 * Math.sin(2 * Math.PI * weekNumber / 52.0)
                + 8.41 * Math.cos(2 * Math.PI * weekNumber / 52.0);
    }

    /**
     * Diagnoses a given disease for a region given a zipcode and date
     * @param zipcode Zipcode for area interested in
     * @param d Date to diagnose
     * @param icdcode Code pertaining to a disease
     * @return List of numbers representing the number of cases for each week leading up to d. List[0] = Week(d) - 8, List[1] = Week(d) - 7, etc
     */
    public List<Integer> diagnosisRegion(String zipcode, Date d, String icdcode) throws ITrustException {
        Connection conn = null;
        PreparedStatement ps = null;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        List<Integer> ret = new ArrayList<Integer>();
        String officeDiagnosis = "(SELECT VisitID FROM ovdiagnosis WHERE ICDCode = " + icdcode + ")";
        String patientZipRegion = "(SELECT MID FROM patients WHERE zip LIKE '" + zipcode.substring(0, 3) + "%')";
        try {
            conn = factory.getConnection();
            for(int i = 8; i > 0; i--){
                ps = conn.prepareStatement("SELECT "
                        + "COUNT(officevisits.VisitDate) as NumTotalCases FROM officevisits WHERE officevisits.ID = ANY "
                        + officeDiagnosis + "AND officevisits.PatientID = ANY " + patientZipRegion + " AND "
                        + "officevisits.VisitDate <= ? AND officevisits.VisitDate >= ?");

                ps.setDate(1, d);
                ps.setDate(2, new Date(d.getTime() - (7L * i * 24 * 3600 * 1000)));

                ResultSet rs;
                rs = ps.executeQuery();
                rs.next();

                ret.add(rs.getInt("NumTotalCases"));
            }
            return ret;

        } catch (SQLException e) {
            System.out.println("Error2: " + e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    /**
     * Diagnoses a given disease for a state given a zipcode and date
     * @param zipcode Zipcode for area interested in
     * @param d Date to diagnose
     * @param icdcode Code pertaining to a disease
     * @return List of numbers representing the number of cases for each week leading up to d. List[0] = Week(d) - 8, List[1] = Week(d) - 7, etc
     */
    public List<Integer> diagnosisState(String zipcode, Date d, String icdcode) throws ITrustException {
        Connection conn = null;
        PreparedStatement ps = null;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        List<Integer> ret = new ArrayList<Integer>();
        String officeDiagnosis = "(SELECT VisitID FROM ovdiagnosis WHERE ICDCode = " + icdcode + ")";
        String patientZipRegion = "(SELECT MID FROM patients WHERE zip LIKE '" + zipcode.substring(0, 2) + "%')";
        try {
            conn = factory.getConnection();
            for(int i = 8; i > 0; i--){
                ps = conn.prepareStatement("SELECT "
                        + "COUNT(officevisits.VisitDate) as NumTotalCases FROM officevisits WHERE officevisits.ID = ANY "
                        + officeDiagnosis + "AND officevisits.PatientID = ANY " + patientZipRegion + " AND "
                        + "officevisits.VisitDate <= ? AND officevisits.VisitDate >= ?" );

                ps.setDate(1, d);
                ps.setDate(2, new Date(d.getTime() - (7L * i * 24 * 3600 * 1000)));

                ResultSet rs;
                rs = ps.executeQuery();
                rs.next();

                ret.add(rs.getInt("NumTotalCases"));
            }
            return ret;

        } catch (SQLException e) {
            System.out.println("Error2: " + e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    /**
     * Diagnoses a given disease for a zipcode and date
     * @param d Date to diagnose
     * @param icdcode Code pertaining to a disease
     * @return List of numbers representing the number of cases for each week leading up to d. List[0] = Week(d) - 8, List[1] = Week(d) - 7, etc
     */
    public List<Integer> diagnosisAll(Date d, String icdcode) throws ITrustException {
        Connection conn = null;
        PreparedStatement ps = null;
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        List<Integer> ret = new ArrayList<Integer>();
        String officeDiagnosis = "(SELECT VisitID FROM ovdiagnosis WHERE ICDCode = " + icdcode + ")";
        String patientZipRegion = "(SELECT MID FROM patients WHERE zip LIKE '%')";
        try {
            conn = factory.getConnection();
            for(int i = 8; i > 0; i--){
                ps = conn.prepareStatement("SELECT "
                        + "COUNT(officevisits.VisitDate) as NumTotalCases FROM officevisits WHERE officevisits.ID = ANY "
                        + officeDiagnosis + "AND officevisits.PatientID = ANY " + patientZipRegion + " AND "
                        + "officevisits.VisitDate <= ? AND officevisits.VisitDate >= ?");

                ps.setDate(1, d);
                ps.setDate(2, new Date(d.getTime() - (7L * i * 24 * 3600 * 1000)));

                ResultSet rs;
                rs = ps.executeQuery();
                rs.next();

                ret.add(rs.getInt("NumTotalCases"));
            }
            return ret;

        } catch (SQLException e) {
            System.out.println("Error2: " + e.getMessage());
            throw new DBException(e);
        } finally {
            DBUtil.closeConnection(conn, ps);
        }
    }

    /**
     * Check if the input zip code is valid
     * @param zipCode Zipcode for area interested in
     * @return a boolean that indicates if the input zip code is in the database(valid)
     */
    public boolean checkIfZipCodeExists(String zipCode) {
        Connection conn = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT * FROM zipcodes WHERE zip= " + zipCode);
            rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (SQLException e){
            return false;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Check if the input ICD code is valid
     * @param ICDCode representing a disease
     * @return a boolean that indicates if the input ICD code is in the database(valid)
     */
    public boolean checkIfDiagnosisCodeExists(String ICDCode) {
        Connection conn = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            conn = factory.getConnection();
            ps = conn.prepareStatement("SELECT * FROM icdcodes WHERE Code= " + ICDCode);
            rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }
        } catch (SQLException e){
            return false;
        }
        finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    return false;
                }
            }
        }
        return false;
    }
}