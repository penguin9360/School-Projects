
//this is the test for setreminder/
package edu.ncsu.csc.itrust.unit.action;
import edu.ncsu.csc.itrust.beans.MessageBean;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.dao.mysql.MessageDAO;
import junit.framework.TestCase;
import edu.ncsu.csc.itrust.action.SendReminderAction;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AccessDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.beans.Email;
import edu.ncsu.csc.itrust.EmailUtil;
import java.sql.Timestamp;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;
import java.util.List;
import edu.ncsu.csc.itrust.beans.ApptBean;

import edu.ncsu.csc.itrust.exception.FormValidationException;
import org.junit.Test;

public class SendRemindersActionTest extends TestCase {

    private DAOFactory factory;
    private TestDataGenerator gen;

	private ApptDAO apptdao;
	private SendReminderAction action;
	private Timestamp time;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gen = new TestDataGenerator();
        gen.clearAllTables();
        gen.standardData();

        this.factory = TestDAOFactory.getTestInstance();
        this.apptdao = factory.getApptDAO();
        this.action = new SendReminderAction(factory, 9000000001L);
        this.time = new Timestamp(1,1,1,1,1,1,1);
    }

	@Test
	public void testGenerateReminderMessage() throws Exception{
		List<ApptBean> apptwithin = apptdao.getApptsWithin(10);
		MessageBean m = action.generateReminderMessage(10, apptwithin.get(0));
		assertNotNull(m.getBody());
	}

	@Test
	public void testApptsWithin() throws Exception {
		action.sendReminder("10");
		MessageDAO msgdao = new MessageDAO(factory);
		assertNotNull(msgdao.getMessagesFrom(9000000000L).get(0));
	}
}
