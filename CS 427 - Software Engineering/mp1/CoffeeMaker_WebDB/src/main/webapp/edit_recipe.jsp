<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.ncsu.csc326.coffeemaker.*, edu.ncsu.csc326.coffeemaker.exceptions.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CoffeeMaker - Edit Recipe</title>
<%@include file="head.jsp" %>
</head>
<body>
<div align=center class="font1">
<h1>CoffeeMaker</h1>
<h3>Edit a Recipe</h3>
<%
	CoffeeMaker cm = (CoffeeMaker)session.getAttribute("cm");

	String value = request.getParameter("recipe");
	String price = request.getParameter("price");
	String amtCoffee = request.getParameter("amtCoffee");
	String amtMilk = request.getParameter("amtMilk");
	String amtSugar = request.getParameter("amtSugar");
	String amtChocolate = request.getParameter("amtChocolate");
	
	if (price != null && !"null".equals(value)) {
		Recipe r = new Recipe();
		try {
			
			String recipeName = (String)session.getAttribute("recipeName");
			r.setName(recipeName);
			r.setPrice(price);
			r.setAmtCoffee(amtCoffee);
			r.setAmtMilk(amtMilk);
			r.setAmtSugar(amtSugar);
			r.setAmtChocolate(amtChocolate);
						
			String recipeEdited = cm.editRecipe(recipeName, r);
			
			if (recipeEdited != null) {
				out.println("<span class=\"font_success\">" + recipeEdited + " successfully edited.</span><br>");
			} else {
				out.println("<span class=\"font_failure\">" + recipeEdited + " could not be edited.</span><br>");
			}
		} catch (RecipeException e) {
			out.println(e.getMessage());
		}
	} else if (value != null && !"null".equals(value)) {
		session.setAttribute("recipeName", value);
		if (cm.recipeExists(value)) {
			out.println("<span class=\"font_success\">Editing recipe: " + value + "</span><br>");
				
%>
<form method="post" action="edit_recipe.jsp">
<table>
<tr>
<td><input type="text" name="price"></td><td><span class="font1">Recipe Price (integer)</span></td>
</tr>
<tr>
<td><input type="text" name="amtCoffee"></td><td><span class="font1">Units Coffee</span></td>
</tr>
<tr>
<td><input type="text" name="amtMilk"></td><td><span class="font1">Units Milk</span></td>
</tr>
<tr>
<td><input type="text" name="amtSugar"></td><td><span class="font1">Units Sugar</span></td>
</tr>
<tr>
<td><input type="text" name="amtChocolate"></td><td><span class="font1">Units Chocolate</span></td>
</tr>
</table>
<input type="submit" value="Edit Recipe!">
</form>
<%
		} else {
			out.println("<span class=\"font_failure\">" + value + " could not be selected.</span><br>");
		}
	} else {

		Recipe [] recipes = cm.getRecipes();
%>
<br>
<form method="post" action="edit_recipe.jsp">
<table>
<%
		for (int i = 0; i < recipes.length; i++) {
			if (recipes[i] != null) {
%>
<tr>
<td><input type="radio" name="recipe" value=<%=recipes[i].getName() %>></td><td><span class="font1"><%=recipes[i].getName() %></span></td>
</tr>
<%
			} 
		}
%>
</table>
<input type="submit" name="submit" value="Select Recipe!">
</form>
<%
	}
%>
<br>
<br>
<a href="index.jsp"><span class="font1">Back to CoffeeMaker Menu</span></a>
</div>
</body>
</html>
