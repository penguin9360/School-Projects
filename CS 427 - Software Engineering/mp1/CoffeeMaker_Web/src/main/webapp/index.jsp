<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.ncsu.csc326.coffeemaker.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CoffeeMaker</title>
<%@include file="head.jsp" %>
</head>
<body>
<div align=center class="font1">
<%
	CoffeeMaker cm = (CoffeeMaker)session.getAttribute("cm");
	if (cm == null) {
		session.setAttribute("cm", new CoffeeMaker());
	}
%>
Welcome to the CoffeeMaker.  Please select a menu option below.
<ul>
<li><a href="add_recipe.jsp">Add a recipe</a></li>
<li><a href="delete_recipe.jsp">Delete a recipe</a></li>
<li><a href="edit_recipe.jsp">Edit a recipe</a></li>
<li><a href="add_inventory.jsp">Add inventory</a></li>
<li><a href="check_inventory.jsp">Check inventory</a></li>
<li><a href="make_coffee.jsp">Make coffee</a></li>
</ul>
</div>
</body>
</html>