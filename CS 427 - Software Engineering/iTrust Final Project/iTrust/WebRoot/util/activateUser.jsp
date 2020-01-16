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

    String param = request.getParameter("index");
    PatientBean pb = patientList.get(Integer.parseInt(param) - 1);
    action.activatePreregisterPatient(pb);

    loggingAction.logEvent(TransactionType.PATIENT_PREREG_ACTIVATE, loggedInMID, pb.getMID(), "");

	%>

<p> Patient activated </p>
