<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.AuthDAO"%>
<%@page import="java.util.List"%>
<%@page import="edu.ncsu.csc.itrust.beans.loaders.PatientLoader"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.PatientDAO"%>
<%@page import="edu.ncsu.csc.itrust.action.AddPatientAction"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@include file="/global.jsp" %>
<%@include file="/header.jsp" %>
<%
pageTitle = "iTrust - Manage Preregistered Patients";
%>

<html>
<head>
<meta charset="ISO-8859-1">
<title>Manage Pre Registered Users</title>
</head>
<body>
</body>
</html>
<div align=center class="font1">

	<%
	
	Connection conn = null;
	PreparedStatement ps = null;
	PatientLoader pl = new PatientLoader();
	DAOFactory factory = DAOFactory.getProductionInstance();
	conn = factory.getConnection();
	ps = conn.prepareStatement("SELECT * FROM patients WHERE lastName like '%.PREREGISTERED'");
	ResultSet rs = ps.executeQuery();
	List<PatientBean> patientList = pl.loadList(rs);
	rs.close();
	ps.close();
	int listsize = patientList.size();

    AddPatientAction action = new AddPatientAction(factory, loggedInMID);

	%>

<table border="1">
		<thead>
			<tr>
				<th>#</th>
				<th>User Name</th>
				<th>Address</th>
				<th>Email</th>
				<th>Phone</th>
				<th>IC Name</th>
				<th>IC Address</th>
				<th>Ic Number</th>
				<th>Height</th>
				<th>Weight</th>
				<th>Smoker</th>
				<th>Activate</th>
				<th>Deactivate</th>

			</tr>
		</thead>
		<tbody>
			<%
				int i = 1;
				
			%>
 
			<%
				for (PatientBean pb : patientList) {
			%>
			<tr>
				<td><%=i++%></td>
				<td><%=pb.getFullName().substring(0, pb.getFullName().length() - 14)%></td>
				<td><%=pb.getStreetAddress1()%></td>
				<td><%=pb.getEmail()%></td>
				<td><%=pb.getPhone()%></td>
				<td><%=pb.getIcName()%></td>
				<th><%=pb.getIcAddress1()%></td>
				<th><%=pb.getIcPhone()%></td>
				<td><%=pb.getHeight()%></td>
				<td><%=pb.getWeight()%></td>
				<td><%=pb.getSmoker()%></td>
				<td><a href="/iTrust/util/activateUser.jsp?index=<%=i - 1%>">Activate</a></td>
				<td><a href="/iTrust/util/deactivateUser.jsp?index=<%=i - 1%>">Deactivate</a></td>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>
