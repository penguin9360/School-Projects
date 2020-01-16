<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.ncsu.csc326.coffeemaker.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CoffeeMaker - Make Coffee</title>
<%@include file="head.jsp" %>
</head>
<body>
<div align=center class="font1">
<h1>CoffeeMaker</h1>
<h3>Make Coffee</h3>
<%
	CoffeeMaker cm = (CoffeeMaker)session.getAttribute("cm");

	String value = request.getParameter("recipe");
	String money = request.getParameter("money");
	if (value != null && !"null".equals(value)) {
		try {
			int recipeNum = Integer.parseInt(value);
			int moneyValue = Integer.parseInt(money);
			String name = cm.getRecipes()[recipeNum].getName();
			int change = cm.makeCoffee(recipeNum, moneyValue);
			if (change != moneyValue) {
				out.println("<span class=\"font_success\">" + name + " successfully purchased.</span><br>");
			} else {
				out.println("<span class=\"font_failure\">" + name + " could not be purchased.</span><br>");
			}
			out.println("<span class=\"font_failure\">Here's your change: " + change + ".</span><br>");
		} catch (NumberFormatException e) {
			out.println("<span class=\"font_success\">Amount paid should be an integer</span><br>");
		}
	}

	Recipe [] recipes = cm.getRecipes();
%>
<br>
<form method="post" action="make_coffee.jsp">
<table>
<%
	for (int i = 0; i < recipes.length; i++) {
		if (recipes[i] != null) {
%>
<tr>
<td><input type="radio" name="recipe" value=<%=i %>></td><td><span class="font1"><%=recipes[i].getName() + " " + recipes[i].getPrice()%></span></td>
</tr>
<%
		} 
	}
%>
<tr>
<td><input type="text" name="money"></td><td><span class="font1">Enter Money (integer)</span></td>
</tr>
</table>
<input type="submit" value="Make Coffee">
</form>
<br>
<br>
<a href="index.jsp"><span class="font1">Back to CoffeeMaker Menu</span></a>
</div>
</body>
</html>