<%@page errorPage="/auth/exceptionHandler.jsp" %>

<%@page import="java.net.URLEncoder" %>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.*"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.action.SendReminderAction"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Send Reminders";
%>

<%@include file="/header.jsp" %>

<%--[S1] A row for showing the reminder message's subject, the name of the recipient, and the timestamp is then visible in the system's reminder message outbox. A bolded row for showing the message subject, the name of the sender, and the timestamp is then visible in the patient’s message inbox, and a fake email (see the notes in the end of this page) is sent to the patient that indicates that he/she has a new message from the "System Reminder". For a reminder message for an upcoming appointment, the message sender shall be "System Reminder". The message subject shall be "Reminder: upcoming appointment in N day(s)" where N is the number of days between the upcoming appointment date and the current date. The text of the message shall be "You have an appointment on <TIME>, <DATE> with Dr. <DOCTOR>" where <TIME> is the appointment start time, <DATE> is the appointment date, and <DOCTOR> is the name of the LHCP in the appointment.  The event is logged [UC5, S44]. --%>
<%
	SendReminderAction action = new SendReminderAction(prodDAO, loggedInMID);
	if("true".equals(request.getParameter("formIsFilled"))){
		try{
			action.sendReminder(request.getParameter("days"));
			%><span>Reminders sent. </span><%
		} catch(FormValidationException e){
			e.printHTML(pageContext.getOut());
		}
	}
%>
<br /><br />
<form action="sendReminder.jsp" method="post">
<input type=hidden name="formIsFilled" value="true">

Send reminders to patients with appointments within
<input name="days" size="3"> day(s).<br /><br />
<input type=submit value="Send reminders">
</form>
<br /><br />

<%@include file="/footer.jsp" %>
