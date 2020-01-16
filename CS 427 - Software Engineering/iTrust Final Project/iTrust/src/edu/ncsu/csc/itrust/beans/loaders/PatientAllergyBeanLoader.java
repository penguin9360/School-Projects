package edu.ncsu.csc.itrust.beans.loaders;

import edu.ncsu.csc.itrust.beans.PatientAllergyBean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientAllergyBeanLoader implements BeanLoader<PatientAllergyBean> {

    @Override
    public List<PatientAllergyBean> loadList(ResultSet rs) throws SQLException {
        List<PatientAllergyBean> list = new ArrayList<PatientAllergyBean>();
        while (rs.next()) {
            list.add(loadSingle(rs));
        }
        return list;
    }

    @Override
    public PatientAllergyBean loadSingle(ResultSet rs) throws SQLException {
        PatientAllergyBean p = new PatientAllergyBean();
        p.setAllergy(rs.getString("Description"));
        p.setMID(rs.getLong("MID"));
        p.setLastName(rs.getString("lastName"));
        p.setFirstName(rs.getString("firstName"));
        p.setZip(rs.getString("zip"));
        if("null".equals(p.getAllergy())) {
            p.setAllergy("");
        }
        return p;
    }

    @Override
    public PreparedStatement loadParameters(PreparedStatement ps, PatientAllergyBean bean) throws SQLException {
        return null;
    }

}
