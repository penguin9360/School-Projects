<%@page import="edu.ncsu.csc.itrust.action.AddPatientAction"%>
<%@page import="edu.ncsu.csc.itrust.beans.PatientBean"%>
<%@page import="edu.ncsu.csc.itrust.dao.DAOFactory"%>
<%@page import="edu.ncsu.csc.itrust.dao.mysql.AuthDAO"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>User Register</title>
</head>
<body>
</body>
</html>
<div align=center class="font1">

<%@include file="/global.jsp"%>

<%
	pageTitle = "iTrust - Preregister User";
%>

<%@include file="/header.jsp"%>


<%

	String firstName = request.getParameter("firstName");
	String lastName = request.getParameter("lastName");
	String email = request.getParameter("email");
	String password = request.getParameter("password");
	String passwordVerify = request.getParameter("passwordVerify");
	String userAddress = request.getParameter("userAddress");
	String userNumber = request.getParameter("userNumber");
	String provider = request.getParameter("provider");
	String providerAddress = request.getParameter("providerAddress");
	String providerNumber = request.getParameter("providerNumber");
	String height = request.getParameter("userHeight");
	String weight = request.getParameter("userWeight");
	String smoker = request.getParameter("smoker");

	if(firstName == null || (password != null && !password.equals(passwordVerify))) {

	if (password != null && !password.equals(passwordVerify)) {
%>
	<p> Password must match verify password </p>
<%
	}

%>
<h4>Enter your info. * means that field is optional.<h4>
<br>
<form method="post" action="preRegisterUser.jsp">
<table>
<tr>
<td><input type="text" required ='true' name="firstName"></td><td><span class="font1">First Name</span></td>
</tr>
<tr>
<td><input type="text" required ='true' name="lastName"></td><td><span class="font1">Last Name</span></td>
</tr>

<tr>
<td><input type="text" required = 'true'name="email"></td><td><span class="font1">E-mail</span></td>
</tr>
<tr>
<td><input type="text" required = 'true' name="password"></td><td><span class="font1">Password</span></td>
</tr>
<tr>
<td><input type="text" required = 'true'  name="passwordVerify"></td><td><span class="font1">Verify Password</span></td>
</tr>
<tr>
<td><input type="text" name="userAddress"></td><td><span class="font1">Address*</span></td>
</tr>
<tr>
<td><input type="text" name="userNumber"></td><td><span class="font1">Phone Number*</span></td>
</tr>
<tr>
<td><input type="text" name="provider"></td><td><span class="font1">Insurance provider*</span></td>
</tr>
<tr>
<td><input type="text" name="providerAddress"></td><td><span class="font1">Provider Address*</span></td>
</tr>
<tr>
<td><input type="text" name="providerNumber"></td><td><span class="font1">Provider Number*</span></td>
</tr>
<tr>
<td><input type="text" name="userHeight"></td><td><span class="font1">Height in inches*</span></td>
</tr>
<tr>
<td><input type="text" name="userWeight"></td><td><span class="font1">Weight in lbs*</span></td>
</tr>
<tr>
<td><input type="text" name="smoker"></td><td><span class="font1">Smoker(Y/N)*</span></td>
</tr>

</table>
<input type="submit" name="submit" value="Register now!">
</form>
<br>
<br>
<%
    } else {
        DAOFactory factory = DAOFactory.getProductionInstance();
        AddPatientAction action = new AddPatientAction(factory, 1);
        PatientBean p = new PatientBean();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setEmail(email);
        if(userAddress != null) {
            p.setStreetAddress1(userAddress);
        }
        if(userNumber != null) {
            p.setPhone(userNumber);
        }
        if(provider != null) {
            p.setIcName(provider);
        }
        if(providerAddress != null) {
            p.setIcAddress1(providerAddress);
        }
        if(providerNumber != null) {
            p.setIcPhone(providerNumber);
        }
	if(height != null) {
            p.setHeight(height);
        }

	if(weight != null){
            p.setWeight(weight);
	    }
	   if(smoker != null){
           p.setSmoker(smoker);
	   }


        long mid = action.addPreregisterPatient(p, password);
        loggingAction.logEvent(TransactionType.PATIENT_PREREGISTER, mid, 0L, "");

%>
    <p>Pre registered user!</p>
    <p>MID: <%=mid%></p>
<%
    }
%>
<a href="/iTrust/"><span class="font1">Back to Main Menu</span></a>
</div>
</body>
</html>

