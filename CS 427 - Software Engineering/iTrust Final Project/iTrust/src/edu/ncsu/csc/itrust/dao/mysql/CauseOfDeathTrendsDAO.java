package edu.ncsu.csc.itrust.dao.mysql;

import edu.ncsu.csc.itrust.beans.CauseOfDeathBean;
import edu.ncsu.csc.itrust.beans.loaders.CauseOfDeathBeanLoader;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.enums.Gender;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.ITrustException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import edu.ncsu.csc.itrust.DBUtil;
import edu.ncsu.csc.itrust.exception.DBException;

public class CauseOfDeathTrendsDAO {
    private DAOFactory factory;
    private CauseOfDeathBeanLoader loader;

    public CauseOfDeathTrendsDAO(DAOFactory factory) {
        this.factory = factory;
        this.loader = new CauseOfDeathBeanLoader();
    }

    public List<CauseOfDeathBean> getCauseOfDeath(long hcpID, int startYear, int endYear, Gender gender) throws DBException{
    	Connection conn = null;
		PreparedStatement ps = null;
		try {

			conn = factory.getConnection();
			String patientStatement = "(SELECT PatientID FROM officevisits WHERE HCPID = ? GROUP BY PatientID) ";
			String statement = "SELECT COUNT(*) AS `count`, causeOfDeath AS `code`, Description AS `causeOfDeath` " +
                    "FROM patients JOIN icdcodes ON patients.causeOfDeath = icdcodes.Code " +
                    "WHERE DateOfDeath >= ? AND DateOfDeath <= ? " +
					"AND patients.MID IN "+patientStatement;
			if (gender != Gender.NotSpecified) {
			    statement += "AND gender = ? ";
            }
			statement += "GROUP BY causeOfDeath ORDER BY `count` DESC LIMIT 2;";
			ps = conn.prepareStatement(statement);
            ps.setString(1, startYear + "-01-01");
            ps.setString(2, endYear + "-12-31");
            ps.setLong(3, hcpID);
            if (gender != Gender.NotSpecified) {
                ps.setString(4, gender.toString());
            }
			ResultSet rs = ps.executeQuery();
			List<CauseOfDeathBean> list = loader.loadList(rs);
			rs.close();
			ps.close();
			return list;
		} catch (SQLException e) {
			throw new DBException(e);
		} finally {
			DBUtil.closeConnection(conn, ps);
		}
    }


}
