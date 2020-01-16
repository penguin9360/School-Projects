<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.ncsu.csc326.coffeemaker.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CoffeeMaker - Check Inventory</title>
<%@include file="head.jsp" %>
</head>
<body>
<div align=center class="font1">
<h1>CoffeeMaker</h1>
<h3>Check Inventory</h3>
<%	
	CoffeeMaker cm = (CoffeeMaker)session.getAttribute("cm");
	
	String inventory = cm.checkInventory();
%>
<span class="font1" name="inventory"><%=inventory %></span>
<br><br>
<span class="font1"><a href="index.jsp"><span class="font1">Back to CoffeeMaker Menu</span></a>
</span>
</div>
</body>
</html>