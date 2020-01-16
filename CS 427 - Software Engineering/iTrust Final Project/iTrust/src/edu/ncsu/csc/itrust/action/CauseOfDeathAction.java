package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.beans.CauseOfDeathBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AuthDAO;
import edu.ncsu.csc.itrust.dao.mysql.CauseOfDeathTrendsDAO;
import edu.ncsu.csc.itrust.dao.mysql.TransactionDAO;
import edu.ncsu.csc.itrust.enums.Gender;
import edu.ncsu.csc.itrust.enums.Role;
import edu.ncsu.csc.itrust.enums.TransactionType;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides cause of death reports Used by causeOfDeath.jsp
 *
 */
public class CauseOfDeathAction {

    private CauseOfDeathTrendsDAO causeOfDeathDAO;
    private TransactionDAO transDAO;
    private long loggedInMID;

    /**
     * Set up defaults
     * @param factory The DAOFactory used to create the DAOs used in this action.
     */
    public CauseOfDeathAction(DAOFactory factory, long loggedInMID) {
        this.causeOfDeathDAO = factory.getCauseOfDeathTrendsDAO();
        this.transDAO = factory.getTransactionDAO();
        this.loggedInMID = loggedInMID;
    }

    public List<CauseOfDeathBean> topTwoCommon(int startYear, int endYear, Gender gender) throws ITrustException {
        transDAO.logTransaction(TransactionType.DEATH_TRENDS_VIEW, loggedInMID, 0L, "");
        return causeOfDeathDAO.getCauseOfDeath(loggedInMID, startYear, endYear, gender);
    }




}
