<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.util.List"%>

<%@page import="edu.ncsu.csc.itrust.action.ViewMyMessagesAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.MessageBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="edu.ncsu.csc.itrust.action.EditPersonnelAction"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PersonnelDAO"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPatientAction"%>
<%@page import="edu.ncsu.csc.itrust.action.EditPersonnelAction"%>
<%@page import="edu.ncsu.csc.itrust.action.SendReminderAction"%>
<%@page import="edu.ncsu.csc.itrust.exception.FormValidationException"%>
<%@ page import="edu.ncsu.csc.itrust.beans.PatientAllergyBean" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Send Allergy Warning";
session.setAttribute("outbox",false);
session.setAttribute("isHCP",userRole.equals("hcp"));
loggingAction.logEvent(TransactionType.INBOX_VIEW, loggedInMID.longValue(), 0L, "");
%>

<%@include file="/header.jsp" %>

<div align=center>
	<h2>Send Allergy Warning</h2>

<%
    String allergy = request.getParameter("ALLERGY");
    String zip = request.getParameter("ZIP");
    String startDate = request.getParameter("START_DATE");
    String endDate = request.getParameter("END_DATE");
    String condition = request.getParameter("CONDITION");

    if(allergy == null) {
        allergy = "";
    }
    if(zip == null) {
        zip = "";
    }
    if(condition == null) {
        condition = "Enter condition";
    }

	SendReminderAction action = new SendReminderAction(prodDAO, loggedInMID);
    List<PatientAllergyBean> patientAllergyList = null;
    boolean showTable = false;
	if("true".equals(request.getParameter("formIsFilled"))){
		try{
            if (request.getParameter("batch") != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String location = zip;
                if(StringUtils.isEmpty(location)) {
                    location = "all areas";
                }
                String body = String.format("Air quality alert! %s is expected in %s on %s.", condition, location, sdf.format(new Date()));
                System.out.println(body);
                action.sendAirQualityAlertBatch(zip, allergy, "Allergy Warning", body);
                %><span>Warnings sent. </span><%
            } else if (request.getParameter("filter") != null) {
                showTable = true;
                patientAllergyList = action.getPatientAllergyList(zip, allergy);
            }
		} catch(FormValidationException e){
			e.printHTML(pageContext.getOut());
		}
	}
%>
<table align=center>
    <tr> </tr>
    <form id="userSearchForm" action="allergyWarning.jsp" method="post">
        <input type="hidden" name="formIsFilled" value="true"><br />
        <tr>
            <td><b>Allergy:</b></td>
            <td style="width: 150px; border: 1px solid Gray;">
                <input name="ALLERGY" type="text" value="<%= StringEscapeUtils.escapeHtml("" + ( allergy )) %>" />
            </td>
            <td><b>ZIP Code:</b></td>
            <td style="width: 150px; border: 1px solid Gray;">
                <input name="ZIP" type="text" value="<%= StringEscapeUtils.escapeHtml("" + ( zip )) %>" />
            </td>
            <td><b>Condition:</b></td>
            <td style="width: 150px; border: 1px solid Gray;">
                <input name="CONDITION" type="text" value="<%= StringEscapeUtils.escapeHtml("" + ( condition )) %>" />
            </td>
            <%--<td><b>Start Date (YYYY-MM-DD):</b></td>
            <td style="width: 150px; border: 1px solid Gray;">
                <input name="START_DATE" type="text" value="<%= StringEscapeUtils.escapeHtml("" + ( startDate )) %>" />
            </td>
            <td><b>End Date (YYYY-MM-DD):</b></td>
            <td style="width: 150px; border: 1px solid Gray;">
                <input name="END_DATE" type="text" value="<%= StringEscapeUtils.escapeHtml("" + ( endDate )) %>" />
            </td>--%>
            <td align="center" colspan="2">
                <input type="submit" id="sendWarningBtn" value="Send Warnings to All Matching Patients" name="batch"/>
            </td>
            <td align="center" colspan="2">
                <input type="submit" id="filterTableBtn" value="Filter Table" name="filter"/>
            </td>
        </tr>
    </form>
</table>

    <% if(showTable && patientAllergyList != null) {%>
    <br>
    <table align=center class="fTable" style="width:60%">
        <tr> </tr>
        <tr>
            <th>MID</th>
            <th>Name</th>
            <th>Zip</th>
            <th>Allergy</th>
        </tr>
        <%
            for (PatientAllergyBean p : patientAllergyList) {
                out.println("<tr>");
                out.println("<td>" + p.getMID() + "</td>");
                out.println("<td>" + p.getFirstName() + " " + p.getLastName() + "</td>");
                out.println("<td>" + p.getZip() + "</td>");
                out.println("<td>" + p.getAllergy() + "</td>");
                out.println("<tr>");
            }
        %>
    </table>
    <%}%>

<br>

<%-- <%
    if(formIsFilled) {
        try {
            String yearPattern = "[1|2][0-9]{3}";
            if(!Pattern.matches(yearPattern, startYear) && !Pattern.matches(yearPattern, endYear)) {
                throw new FormValidationException("Start Year", "End Year");
            }
            if(!Pattern.matches(yearPattern, startYear)) {
                throw new FormValidationException("Start Year");
            }
            if(!Pattern.matches(yearPattern, endYear)) {
                throw new FormValidationException("End Year");
            }
            int startYearInt = Integer.parseInt(startYear);
            int endYearInt = Integer.parseInt(endYear);
            if (startYearInt > endYearInt) {
                throw new Exception("Start Year must be less or equal to End Year!");
            }
            CauseOfDeathAction action = new CauseOfDeathAction(prodDAO, loggedInMID);
            List<CauseOfDeathBean> list = action.topTwoCommon(startYearInt, endYearInt, gender);
            %> --%>
    <%--
			<script src="/iTrust/DataTables/media/js/jquery.dataTables.js" type="text/javascript"></script>
			<script type="text/javascript">
				jQuery.fn.dataTableExt.oSort['lname-asc']  = function(x,y) {
					var a = x.split(" ");
					var b = y.split(" ");
					return ((a[1] < b[1]) ? -1 : ((a[1] > b[1]) ?  1 : 0));
				};

				jQuery.fn.dataTableExt.oSort['lname-desc']  = function(x,y) {
					var a = x.split(" ");
					var b = y.split(" ");
					return ((a[1] < b[1]) ? 1 : ((a[1] > b[1]) ?  -1 : 0));
				};
			</script>
			<script type="text/javascript">
   				$(document).ready(function() {
       				$("#mailbox").dataTable( {
       					"aaColumns": [ [2,'dsc'] ],
       					"aoColumns": [ { "sType": "lname" }, null, null, {"bSortable": false} ],
       					"sPaginationType": "full_numbers"
       				});
   				});
			</script>
			<style type="text/css" title="currentStyle">
				@import "/iTrust/DataTables/media/css/demo_table.css";
			</style>

<%

boolean outbox=(Boolean)session.getAttribute("outbox");
boolean isHCP=(Boolean)session.getAttribute("isHCP");

String pageName="messageInbox.jsp";
if(outbox){
	pageName="messageOutbox.jsp";
}

PersonnelDAO personnelDAO = new PersonnelDAO(prodDAO);
PatientDAO patientDAO = new PatientDAO(prodDAO);

DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

ViewMyMessagesAction action = new ViewMyMessagesAction(prodDAO, loggedInMID.longValue());

List<MessageBean> messages = outbox?action.getAllMySentMessages():action.getAllMyMessages();
session.setAttribute("messages", messages);

%>
<table id="mailbox" class="display fTable">
	<thead>
		<tr>
			<th><%= outbox?"Receiver":"Sender" %></th>
			<th>Subject</th>
			<th><%= outbox?"Sent":"Received" %></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
	<%
	int index=-1;
	for(MessageBean message : messages) {
		String style = "";
		if(message.getRead() == 0) {
			style = "style=\"font-weight: bold;\"";
		}

		if(!outbox || message.getOriginalMessageId()==0){
			index ++;
			String primaryName = action.getName(outbox?message.getTo():message.getFrom());
			List<MessageBean> ccs = action.getCCdMessages(message.getMessageId());
			String ccNames = "";
			int ccCount = 0;
			for(MessageBean cc:ccs){
				ccCount++;
				long ccMID = cc.getTo();
				ccNames += action.getPersonnelName(ccMID) + ", ";
			}
			ccNames = ccNames.length() > 0?ccNames.substring(0, ccNames.length()-2):ccNames;
			String toString = primaryName;
			if(ccCount>0){
				String ccNameParts[] = ccNames.split(",");
				toString = toString + " (CC'd: ";
				for(int i = 0; i < ccNameParts.length-1; i++) {
					toString += ccNameParts[i] + ", ";
				}
				toString += ccNameParts[ccNameParts.length - 1] + ")";
			}
			%>
				<tr <%=style%>>
					<td><%= StringEscapeUtils.escapeHtml("" + ( toString)) %>
						<span style="display:none"><%= StringEscapeUtils.escapeHtml(message.getBody()) %></span>
					</td>
					<td><%= StringEscapeUtils.escapeHtml("" + ( message.getSubject() )) %></td>
					<td><%= StringEscapeUtils.escapeHtml("" + ( dateFormat.format(message.getSentDate()) )) %></td>
					<td><a href="<%= outbox?"viewMessageOutbox.jsp?msg=" + StringEscapeUtils.escapeHtml("" + ( index )):"viewMessageInbox.jsp?msg=" + StringEscapeUtils.escapeHtml("" + ( index )) %>">Read</a></td>
				</tr>
			<%
		}

	}
	%>
	</tbody>
</table>
</div>

--%>
<%@include file="/footer.jsp" %>
