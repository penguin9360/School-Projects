<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<%@ page import="edu.ncsu.csc326.coffeemaker.*, edu.ncsu.csc326.coffeemaker.exceptions.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>CoffeeMaker - Add Inventory</title>
    <%@include file="head.jsp" %>
</head>
<body>
<div align=center class="font1">
    <h1>CoffeeMaker</h1>
    <h3>Add a Inventory</h3>
    <%
        ; // TODO: MP1: complete the AddInventory user story

		String amtCoffee = request.getParameter("amtCoffee");
		String amtMilk = request.getParameter("amtMilk");
		String amtSugar = request.getParameter("amtSugar");
		String amtChocolate = request.getParameter("amtChocolate");
		
		CoffeeMaker cm = (CoffeeMaker)session.getAttribute("cm");
		
		if (cm == null) {
			cm = new CoffeeMaker();
		}
		
		if (amtCoffee != null && amtMilk != null && amtSugar != null && amtChocolate != null) {
			try {
				cm.addInventory(amtCoffee, amtMilk, amtSugar, amtChocolate);
			} catch (InventoryException e) {
				e.printStackTrace();
			}				
			%>
			<span class="font_success">successfully added.</span><br>
			<%
		}
    %>
    <br>
    <form method="post" action="add_inventory.jsp">
        <table>
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
        <input type="submit" name="submit" value="Add Inventory!">
    </form>
    <br>
    <br>
    <a href="index.jsp"><span class="font1">Back to CoffeeMaker Menu</span></a>
</div>
</body>
</html>
