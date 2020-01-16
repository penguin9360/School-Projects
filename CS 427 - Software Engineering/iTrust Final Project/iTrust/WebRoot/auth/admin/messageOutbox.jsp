<%@page errorPage="/auth/exceptionHandler.jsp"%>

<%@page import="java.util.List"%>

<%@page import="edu.ncsu.csc.itrust.action.ViewMyMessagesAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.MessageBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>

<%@include file="/global.jsp" %>

<%
pageTitle = "iTrust - Reminder Outbox";
%>

<%@include file="/header.jsp" %>

<div align=center>
	<h2>Reminder Messages</h2>
<%
	loggingAction.logEvent(TransactionType.OUTBOX_VIEW, loggedInMID.longValue(), 0, "");

	ViewMyMessagesAction action = new ViewMyMessagesAction(prodDAO, loggedInMID.longValue());
	List<MessageBean> messages = null;
	if(request.getParameter("sortby") != null) {
		if(request.getParameter("sortby").equals("name")) {
			if(request.getParameter("sorthow").equals("asce")) {
				messages = action.getAllMySentMessagesNameAscending();
			} else {
				messages = action.getAllMySentMessagesNameDescending();
			}
		} else if(request.getParameter("sortby").equals("time")) {
			if(request.getParameter("sorthow").equals("asce")) {
				messages = action.getAllMySentMessagesTimeAscending();
			} else {
				messages = action.getAllMySentMessages();
			}
		}
	}
	else {
		messages = action.getAllMySentMessages();
	}
	session.setAttribute("messages", messages);
	if (messages.size() > 0) { %>
	<form method="post" action="messageOutbox.jsp">
	<table>
	<tr>
		<td>
			<select name="sortby">
					<option value="time">Sort</option>
					<option value="name">Name</option>
					<option value="time">Time</option>
			</select>
		</td>
		<td>
			<select name="sorthow">
					<option value="desc">Order</option>
					<option value="asce">Ascending</option>
					<option value="desc">Descending</option>
			</select>
		</td>
		<td>
			<input type="submit" value="Sort" />
		</td>
	</tr>
	</table>
	</form>
	<br />
	<table class="fancyTable">
		<tr>
			<th>To</th>
			<th>Subject</th>
			<th>Sent</th>
			<th></th>
		</tr>
<%		int index = 0; %>
<%		for(MessageBean message : messages) { %>
		<tr <%=(index%2 == 1)?"class=\"alt\"":"" %>>
			<td><%= StringEscapeUtils.escapeHtml("" + ( action.getName(message.getTo()) )) %></td>
			<td><%= StringEscapeUtils.escapeHtml("" + ( message.getSubject() )) %></td>
			<td><%= StringEscapeUtils.escapeHtml("" + ( message.getSentDate() )) %></td>
			<td><a href="viewMessageOutbox.jsp?msg=<%= StringEscapeUtils.escapeHtml("" + ( index )) %>">Read</a></td>
		</tr>
<%			index ++; %>
<%		} %>
	</table>
<%	} else { %>
	<div>
		<i>There are no sent reminders</i>
	</div>
<%	} %>
	<br />
</div>

<%@include file="/footer.jsp" %>
