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
			r.setPrice(price);
			r.setAmtCoffee(amtCoffee);
			r.setAmtMilk(amtMilk);
			r.setAmtSugar(amtSugar);
			r.setAmtChocolate(amtChocolate);
			
			Integer recipeNum = (Integer)session.getAttribute("recipeNum");
			
			String recipeEdited = cm.editRecipe(recipeNum.intValue(), r);
			
			if (recipeEdited != null) {
				out.println("<span class=\"font_success\">" + recipeEdited + " successfully edited.</span><br>");
			} else {
				out.println("<span class=\"font_failure\">" + recipeEdited + " could not be edited.</span><br>");
			}
		} catch (RecipeException e) {
			out.println(e.getMessage());
		}
	} else if (value != null && !"null".equals(value)) {
		Integer recipeNum = Integer.parseInt(value);
		session.setAttribute("recipeNum", recipeNum);
		String name = cm.getRecipes()[recipeNum].getName();
		if (cm.getRecipes()[recipeNum] != null) {
			out.println("<span class=\"font_success\">Editing recipe: " + name + "</span><br>");
				
%>
<form method="post" action="edit_recipe.jsp">
<table>
<tr>
<td><input type="text" name="price" value=<%=
cm.getRecipes()[Integer.parseInt(value)].getPrice()
%>></td><td><span class="font1">Recipe Price (integer)</span></td>
</tr>
<tr>
<td><input type="text" name="amtCoffee" value=<%=
cm.getRecipes()[Integer.parseInt(value)].getAmtCoffee()
%>></td><td><span class="font1">Units Coffee</span></td>
</tr>
<tr>
<td><input type="text" name="amtMilk" value=<%=
cm.getRecipes()[Integer.parseInt(value)].getAmtMilk()
%>></td><td><span class="font1">Units Milk</span></td>
</tr>
<tr>
<td><input type="text" name="amtSugar" value=<%=
cm.getRecipes()[Integer.parseInt(value)].getAmtSugar()
%>></td><td><span class="font1">Units Sugar</span></td>
</tr>
<tr>
<td><input type="text" name="amtChocolate" value=<%=
cm.getRecipes()[Integer.parseInt(value)].getAmtChocolate()
%>></td><td><span class="font1">Units Chocolate</span></td>
</tr>
</table>
<input type="submit" value="Edit Recipe!">
</form>
<%
		} else {
			out.println("<span class=\"font_failure\">" + name + " could not be selected.</span><br>");
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
<td><input type="radio" name="recipe" value=<%=i %>></td><td><span class="font1"><%=recipes[i].getName() %></span></td>
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