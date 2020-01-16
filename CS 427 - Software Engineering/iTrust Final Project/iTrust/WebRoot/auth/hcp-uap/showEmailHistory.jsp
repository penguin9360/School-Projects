<%@taglib prefix="itrust" uri="/WEB-INF/tags.tld"%>
<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.beans.Email"%>
<%@page import="edu.ncsu.csc.itrust.action.ViewPersonnelAction"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Email History";
%>

<%@include file="/header.jsp" %>

<br />
<div align=center>
<%
	ViewPersonnelAction action = new ViewPersonnelAction(prodDAO, loggedInMID.longValue());
	List<Email> Emails = action.getEmailHistory();
	%><table class="fTable"><%
	if (Emails.size() != 0){
		loggingAction.logEvent(TransactionType.EMAIL_HISTORY_VIEW, loggedInMID, 0, "");
		%> 
		
			<tr>
			<th> To </th> 
			<th> Subject </th> 
			<th> Body </th>
			<th> Date Sent </th>
			</tr>
		<%
		for (Email email : Emails){
%>
		<tr>
			<td > <%= StringEscapeUtils.escapeHtml("" + (email.getToListStr())) %> </td>
			<td > <%= StringEscapeUtils.escapeHtml("" + (email.getSubject())) %> </td> 
			<td > <%= StringEscapeUtils.escapeHtml("" + (email.getBody())) %> </td>
			<td > <%= StringEscapeUtils.escapeHtml("" + (email.getTimeAdded() )) %> </td>
		</tr>
<%}

}
	else{
	%>
	<tr><td> No Emails on Record </td></tr>
	<%} %>
	</table>
</div>
<br />

<%@include file="/footer.jsp" %>
