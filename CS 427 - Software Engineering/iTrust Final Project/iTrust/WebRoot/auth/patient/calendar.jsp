<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="edu.ncsu.csc.itrust.action.GenerateCalendarAction"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewOfficeVisitAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.ApptBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.OfficeVisitBean"%>
<%@page import="edu.ncsu.csc.itrust.beans.LabProcedureBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.Calendar"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Appointment Calendar";
%>

<%@include file="/header.jsp" %>

<%
	GenerateCalendarAction action = new GenerateCalendarAction(prodDAO, loggedInMID.longValue());
	List<ApptBean> send;
	boolean conflicts[];
	Hashtable<Integer, ArrayList<ApptBean>> atable;
	Hashtable<Integer, ArrayList<OfficeVisitBean>> rtable;
	Hashtable<Integer, ArrayList<LabProcedureBean>> ptable;
	loggingAction.logEvent(TransactionType.CALENDAR_VIEW, loggedInMID, 0, "");

	//Calendar Stuff
	String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	String weekDays[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	Calendar cal = Calendar.getInstance();
	int origDay = cal.get(Calendar.DAY_OF_MONTH);
	int origMonth = cal.get(Calendar.MONTH);
	int origYear = cal.get(Calendar.YEAR);
	cal.set(Calendar.DAY_OF_MONTH, 1);
	
	int c_month = cal.get(Calendar.MONTH);
	
	//Change Month from JSP
	if(request.getParameter("c") != null) {
		String cur = request.getParameter("c");
		try {
			c_month = Integer.parseInt(cur);
		} catch (NumberFormatException nfe) {
			response.sendRedirect("calendar.jsp");
		}
	}
	
	int month = c_month;
	if(request.getParameter("m") != null) {
		String m = request.getParameter("m");
		int move = 0;
		try {
			move = Integer.parseInt(m);
		} catch (NumberFormatException nfe) {
			response.sendRedirect("calendar.jsp");
		}
		month = c_month + move;
	}
	//Line to set month directly
	cal.set(Calendar.MONTH, month);
	int thisMonth = cal.get(Calendar.MONTH);
	int thisYear = cal.get(Calendar.YEAR);
	//Get First Day of Month
	cal.set(Calendar.DAY_OF_MONTH, 1);
	int firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK);
	//Get Last Day and Week of Month
	cal.add(Calendar.MONTH, 1);
    cal.add(Calendar.DAY_OF_MONTH, -1);
	int lastDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
	int lastWeekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);
	
	//Compile Appointments for this month
	atable = action.getApptsTable(thisMonth, thisYear);
	send = action.getSend();
	conflicts = action.getConflicts();
	
	//Compile Office Visits for this month
	rtable = action.getOfficeVisitsTable(thisMonth, thisYear);
	
	//Compile Lab Procedures for this month
	ptable = action.getLabProceduresTable(thisMonth, thisYear);
%>
<div align="center">
<table width="90%">
	<tr>
		<td style="text-align: left;">
			<a href="calendar.jsp?c=<%= StringEscapeUtils.escapeHtml("" + ( month )) %>&m=-1">Last</a>	
		</td>
		<td style="text-align: center;">
			<h3><%= StringEscapeUtils.escapeHtml("" + ( "Appointments for "+months[thisMonth]+" "+thisYear )) %></h3>
		</td>
		<td style="text-align: right;">
			<a href="calendar.jsp?c=<%= StringEscapeUtils.escapeHtml("" + ( month )) %>&m=1">Next</a>
		</td>
	</tr>
</table>
<table id="calendarTable" border="1px">
	<%
		out.print("<tr>\n");
		for(String d : weekDays) {
			out.print("<th>"+StringEscapeUtils.escapeHtml("" + (d))+"</th>");
		}
		out.print("</tr>\n");
		int calDay = 1 + Calendar.SUNDAY - firstDayOfMonth;
		int p = 0;
		for(int i=0; i<lastWeekOfMonth; i++) {
			out.print("<tr>\n");
			for(int j=0; j<7; j++) {
				if(calDay == origDay && thisMonth == origMonth && thisYear == origYear)
					out.print("<td class=\"today\"><div class=\"cell\">");
				else
					out.print("<td><div class=\"cell\">");
				if(calDay > 0 && calDay <= lastDayOfMonth) out.print("<div style=\"float: right;\">"+StringEscapeUtils.escapeHtml("" + (calDay))+"</div>");
				else out.print("<div class=\"blankDay\"></div>");
				//Add Appointments to Calendar
				if(atable.containsKey(calDay)) {
					ArrayList<ApptBean> l = atable.get(calDay);
					for(ApptBean b : l) {	
						out.print("<div class=\"calendarEntry "+StringEscapeUtils.escapeHtml("" + ((conflicts[p]?"conflict":"")))+" \">"+StringEscapeUtils.escapeHtml("" + (b.getApptType()))+"<br /><a name=\""+StringEscapeUtils.escapeHtml("" + (b.getApptType()))+"-"+StringEscapeUtils.escapeHtml("" + (calDay))+"\" href=\"viewAppt.jsp?apt="+ StringEscapeUtils.escapeHtml("" + b.getApptID()) +"\" >Read Details</a></div>");
					}
				}
				//Add Office Visits to Calendar
				if(rtable.containsKey(calDay)) {
					ArrayList<OfficeVisitBean> l = rtable.get(calDay);
					for(OfficeVisitBean b : l) {
						ViewOfficeVisitAction view = new ViewOfficeVisitAction(prodDAO, loggedInMID.longValue(), b.getVisitID()+"");
						OfficeVisitBean ov = new ViewOfficeVisitAction(prodDAO, loggedInMID.longValue(), b.getVisitID()+"").getOfficeVisit();
						String in = (view.getDiagnoses().size() == 0)?"Office Visit":view.getDiagnoses().get(0).getICDCode()+"-"+view.getDiagnoses().get(0).getDescription();
						out.print("<div class=\"calendarEntry\">"+StringEscapeUtils.escapeHtml("" + (((view.getDiagnoses().size() == 0)?"Office Visit":in)))+"<br /><a name=\""+StringEscapeUtils.escapeHtml("" + (in))+"-"+StringEscapeUtils.escapeHtml("" + (calDay))+"\" href=\"viewOfficeVisit.jsp?ovID="+ StringEscapeUtils.escapeHtml("" + (b.getVisitID())) +"\" >Read Details</a></div>");
						if(view.getPrescriptions().size() != 0) {
							String in2 = view.getPrescriptions().get(0).getMedication().getNDCode()+"-"+view.getPrescriptions().get(0).getMedication().getDescription();
							out.print("<div class=\"calendarEntry\">"+StringEscapeUtils.escapeHtml("" + (in2))+"<br /><a name=\""+StringEscapeUtils.escapeHtml("" + (in2))+"-"+StringEscapeUtils.escapeHtml("" + (calDay))+"\" href=\"viewPrescriptionInformation.jsp?visitID="+ StringEscapeUtils.escapeHtml("" + (b.getVisitID())) +"&presID="+ StringEscapeUtils.escapeHtml("" + (view.getPrescriptions().get(0).getId())) +"\" >Read Details</a></div>");
						}
					}
				}
				//Add Lab Procedures to Calendar
				if(ptable.containsKey(calDay)) {
					ArrayList<LabProcedureBean> l = ptable.get(calDay);
					for(LabProcedureBean b : l) {	
						out.print("<div class=\"calendarEntry\">"+StringEscapeUtils.escapeHtml("" + (b.getLoinc()))+"<br /><a name=\""+StringEscapeUtils.escapeHtml("" + (b.getLoinc()))+"-"+StringEscapeUtils.escapeHtml("" + (calDay))+"\" href=\"viewLabProc.jsp?id="+StringEscapeUtils.escapeHtml("" + (b.getPid()))+"\" >Read Details</a></div>");
					}
				}
				calDay++;
				out.print("</div></td>\n");
			}
			out.print("</tr>");
		}
		session.setAttribute("appts", send);
	%>
</table>
<br />
<br />
</div>
<%@include file="/footer.jsp" %>
