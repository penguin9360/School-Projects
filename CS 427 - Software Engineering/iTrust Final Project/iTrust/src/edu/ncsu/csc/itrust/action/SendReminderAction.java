package edu.ncsu.csc.itrust.action;

import edu.ncsu.csc.itrust.action.EventLoggingAction;
import edu.ncsu.csc.itrust.beans.PatientAllergyBean;
import edu.ncsu.csc.itrust.dao.DAOFactory;
import edu.ncsu.csc.itrust.dao.mysql.AccessDAO;
import edu.ncsu.csc.itrust.dao.mysql.ApptDAO;
import edu.ncsu.csc.itrust.dao.mysql.PatientDAO;
import edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.exception.FormValidationException;
import edu.ncsu.csc.itrust.exception.ITrustException;
import edu.ncsu.csc.itrust.beans.Email;
import edu.ncsu.csc.itrust.beans.ApptBean;
import edu.ncsu.csc.itrust.beans.MessageBean;
import edu.ncsu.csc.itrust.enums.TransactionType;
import edu.ncsu.csc.itrust.EmailUtil;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



// copied from ChangeSessionTimeoutAction.java
/**
 * Used to send appointment reminders to patients.
 * 
 */
public class SendReminderAction {
	private AccessDAO accessDAO;
	private EmailUtil emailUtil;
	private ApptDAO apptDAO;
	private EventLoggingAction loggingAction;
	private SendMessageAction sendMessageAction;
	private PatientDAO patientDAO;
    private PersonnelDAO personnelDAO;
	private long loggedInMID;

	/**
	 * Sets up defaults.
	 * 
	 * @param factory
	 */
	public SendReminderAction(DAOFactory factory, long loggedInMID) {
		this.loggedInMID = loggedInMID;
		this.accessDAO = factory.getAccessDAO();
		this.emailUtil = new EmailUtil(factory);
		this.apptDAO = new ApptDAO(factory);
		this.personnelDAO = new PersonnelDAO(factory);
		this.loggingAction = new EventLoggingAction(factory);
		this.sendMessageAction = new SendMessageAction(factory, loggedInMID);
		this.patientDAO = new PatientDAO(factory);
	}

	/**
	 * Sends an appointment reminder to patients with appointments within dayString days.
	 * {@link AccessDAO}
	 * 
	 * @param dayString
	 *            Pass the number of days in the form of a string, greater than 0.
	 * @throws FormValidationException
	 * @throws DBException
	 * @throws SQLException
	 * @throws ITrustException
	 */
	public void sendReminder(String dayString) throws FormValidationException, DBException, SQLException, ITrustException {
		try {
			Integer days = Integer.valueOf(dayString);
			if (days < 1)
				throw new FormValidationException("Must be a number greater than 0");
			List<ApptBean> appts = apptDAO.getApptsWithin(days);
			MessageBean msg;
			for (ApptBean appt : appts) {
				msg = generateReminderMessage(days, appt);
				sendMessageAction.sendMessage(msg);
				loggingAction.logEvent(TransactionType.MESSAGE_SEND, msg.getFrom(), msg.getTo(), "");
			}
		} catch (NumberFormatException e) {
			throw new FormValidationException("That is not a number");
		} 
	}

	 public MessageBean generateReminderMessage(Integer days, ApptBean appt) throws ITrustException {
	// For a reminder message for an upcoming appointment, the message sender shall be "System Reminder". The message subject shall be "Reminder: upcoming appointment in N day(s)" where N is the number of days between the upcoming appointment date and the current date. The text of the message shall be "You have an appointment on <TIME>, <DATE> with Dr. <DOCTOR>" where <TIME> is the appointment start time, <DATE> is the appointment date, and <DOCTOR> is the name of the LHCP in the appointment.
         Timestamp currentDate = new Timestamp(System.currentTimeMillis());
         long dayDelta = (appt.getDate().getTime() - currentDate.getTime()) /  86400000L; // 1 day in ms
         String drName = personnelDAO.getName(appt.getHcp());
		 MessageBean msg = new MessageBean();
		 msg.setFrom(loggedInMID);
		 msg.setSubject("Reminder: upcoming appointment in " + Long.toString(dayDelta) + " day(s)");
		 msg.setBody("You have an appointment on " + appt.getDate().toString() + " with Dr. " + drName); //TODO
		 msg.setSentDate(currentDate);
		 msg.setTo(appt.getPatient());
		 return msg;
	 }

	 public List<PatientAllergyBean> getPatientAllergyList(String zip, String allergy) {
		try {
			return this.patientDAO.getPatientAllergyList(zip, allergy);
		}catch (DBException e) {
			return new ArrayList<>();
		}
	 }

	 public void sendAirQualityAlertBatch(String zip, String allergy, String subject, String body) throws ITrustException, SQLException, FormValidationException{
		List<PatientAllergyBean> list = patientDAO.getPatientAllergyList(zip, allergy);
		for(PatientAllergyBean p : list) {
			sendAirQualityAlert(p.getMID(), subject, body);
		}
     }

     public void sendAirQualityAlert(Long mid, String subject, String body) throws ITrustException, SQLException, FormValidationException{
		MessageBean msg = new MessageBean();
		msg.setFrom(loggedInMID);
		msg.setSubject(subject);
		msg.setBody(body);
		msg.setTo(mid);
		msg.setSentDate(new Timestamp(System.currentTimeMillis()));
		sendMessageAction.sendMessage(msg);
     }

}
