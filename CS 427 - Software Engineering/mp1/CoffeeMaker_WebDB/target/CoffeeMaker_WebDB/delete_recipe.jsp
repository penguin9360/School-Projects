<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.ncsu.csc326.coffeemaker.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CoffeeMaker - Delete Recipe</title>
<%@include file="head.jsp" %>
</head>
<body>
<div align=center class="font1">
<h1>CoffeeMaker</h1>
<h3>Delete a Recipe</h3>
<%
	CoffeeMaker cm = (CoffeeMaker)session.getAttribute("cm");

	String value = request.getParameter("recipe");
	if (value != null && !"null".equals(value)) {
		int recipeNum = Integer.parseInt(value);
		String name = cm.getRecipes()[recipeNum].getName();
		if (cm.deleteRecipe(name) != null) {
			out.println("<span class=\"font_success\">" + name + " successfully deleted.</span><br>");
		} else {
			out.println("<span class=\"font_failure\">" + name + " could not be deleted.</span><br>");
		}
	}

	Recipe [] recipes = cm.getRecipes();
%>
<br>
<form method="post" action="delete_recipe.jsp">
<table>
<%
	for (int i = 0; i < recipes.length; i++) {
		if (recipes[i] != null) {
%>
<tr>
<td><input type="radio" name="recipe" value=<%=i %>></td><td><span class="font1"><%=recipes[i].getName() %></span></td>
</tr>
<%
		} 
	}
%>
</table>
<input type="submit" value="Delete Recipe!">
</form>
<br>
<br>
<a href="index.jsp"><span class="font1">Back to CoffeeMaker Menu</span></a>
</div>
</body>
</html>