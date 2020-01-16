<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPatientAction"%>
<%@page import="edu.ncsu.csc.itrust.BeanBuilder"%>
<%@page import="edu.ncsu.csc.itrust.enums.Ethnicity"%>
<%@page import="edu.ncsu.csc.itrust.enums.BloodType"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@page import="edu.ncsu.csc.itrust.enums.Gender"%>
<%@page import="edu.ncsu.csc.itrust.beans.DiagnosisBean"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Audit Page (UC62)";
%>

<%@include file="/header.jsp" %>
<!--  Verify that the requested individual really is a valid Patient  -->
  <% 
    /* Require a Patient ID first */
	String pidString = (String) session.getAttribute("pid");
	if (pidString == null || pidString.equals("") || 1 > pidString.length()) {
		out.println("pidstring is null");
		response.sendRedirect("/iTrust/auth/getPatientID.jsp?forward=hcp/auditPage.jsp");
		return;
	}
	
	/* If the patient id doesn't check out, then kick 'em out to the exception handler */
	EditPatientAction action = new EditPatientAction(prodDAO, loggedInMID.longValue(), pidString);
	
	PatientBean p = action.getPatient();
	
	String deactive = p.getDateOfDeactivationStr();
	boolean isActive = deactive == null || deactive.isEmpty();
	String title = isActive ? "Deactivate" : "Activate";
	
	if (request.getParameter("formIsFilled") != null && request.getParameter("formIsFilled").equals("true") &&
			request.getParameter("understand") != null && request.getParameter("understand").equals("I UNDERSTAND")) {
		try {
			if(isActive)
				action.deactivate();
			else
				action.activate();
			loggingAction.logEvent(TransactionType.PATIENT_DEACTIVATE, loggedInMID.longValue(), Long.valueOf((String)session.getAttribute("pid")).longValue(), "");
			session.removeAttribute("pid");
	
%>
	<br />
	<div align=center>
		<span class="iTrustMessage">Patient Successfully <%=StringEscapeUtils.escapeHtml("" + title + "d") %></span>
	</div>
	<br />
<%
		} catch (Exception e) {
%>
	<br />
	<div align=center>
		<span class="iTrustError"><%=StringEscapeUtils.escapeHtml(e.getMessage()) %></span>
	</div>
	<br />
<%
		}
	} else {
		p = action.getPatient();

	if (request.getParameter("formIsFilled") != null && request.getParameter("formIsFilled").equals("true") &&
			(request.getParameter("understand") == null || !request.getParameter("understand").equals("I UNDERSTAND"))) {
%>
		<br />
		<div align=center>
			<span class="iTrustError">You must type "I UNDERSTAND" in the textbox.</span>
		</div>
		<br />
<%
	}
	
%>
<!--  activate Patients -->


<!--  deactivate Patients -->


<form id="deactivateForm" action="auditPage.jsp" method="post">
<input type="hidden" name="formIsFilled" value="true"><br />
<table cellspacing=0 align=center cellpadding=0>
	<tr>
		<td valign=top>
		<table class="fTable" align=center style="width: 350px;">
			<tr>
				<th colspan="4"><%=StringEscapeUtils.escapeHtml("" + title) %> Patient</th>
			</tr>		
			<tr>
			
				<td class="subHeaderVertical">First Name:</td>
				<td><%= StringEscapeUtils.escapeHtml("" + (p.getFirstName())) %></td>
				<td class="subHeaderVertical">Last Name:</td>
				<td><%= StringEscapeUtils.escapeHtml("" + (p.getLastName())) %></td>
			</tr>
			<tr>
				<td colspan="4">Are you absolutely sure you want to <%=StringEscapeUtils.escapeHtml("" + title.toLowerCase()) %> this
				patient? If you are sure, type "I UNDERSTAND" into the box below and click the
				button</td>
			</tr>
			<tr>
				<td colspan="4"><div align="center"><input name="understand" type="text"></div></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
<br />
<div align=center>
	<input type="submit" name="action"
		style="font-size: 16pt; font-weight: bold;" value="<%=StringEscapeUtils.escapeHtml("" + title) %> Patient"><br /><br />
</div>
</form>
<%} %>

<%@include file="/footer.jsp" %>
