package edu.ncsu.csc.itrust.beans.loaders;

import edu.ncsu.csc.itrust.beans.CauseOfDeathBean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CauseOfDeathBeanLoader implements BeanLoader<CauseOfDeathBean> {

    @Override
    public List<CauseOfDeathBean> loadList(ResultSet rs) throws SQLException {
        List<CauseOfDeathBean> list = new ArrayList<CauseOfDeathBean>();
        while (rs.next()) {
            list.add(loadSingle(rs));
        }
        return list;
    }

    @Override
    public CauseOfDeathBean loadSingle(ResultSet rs) throws SQLException {
        CauseOfDeathBean c = new CauseOfDeathBean();
        c.setCauseOfDeath(rs.getString("causeOfDeath"));
        c.setCode(rs.getString("code"));
        c.setCount(rs.getInt("count"));
        return c;
    }

    @Override
    public PreparedStatement loadParameters(PreparedStatement ps, CauseOfDeathBean bean) throws SQLException {
        throw new IllegalStateException("unimplemented!");
    }

}
