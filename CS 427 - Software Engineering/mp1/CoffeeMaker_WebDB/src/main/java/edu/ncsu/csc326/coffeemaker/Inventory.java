package edu.ncsu.csc326.coffeemaker;

import edu.ncsu.csc326.coffeemaker.exceptions.InventoryException;


import edu.ncsu.csc326.coffeemaker.db.InventoryDB;

/**
 * @author Sarah Heckman
 *
 * Inventory for the coffee maker
 */
public class Inventory {

    
    /**
     * Creates a coffee maker inventory object and
     * fills each item in the inventory with 15 units.
     */
    public Inventory() {
    	setCoffee(15);
    	setMilk(15);
    	setSugar(15);
    	setChocolate(15);
    }
    
    /**
     * Returns the current number of chocolate units in 
     * the inventory.
     * @return int
     */
    public int getChocolate() {
        return InventoryDB.checkInventory()[3];
    }
    
    /**
     * Sets the number of chocolate units in the inventory
     * to the specified amount.
     * @param chocolate
     */
    public synchronized void setChocolate(int chocolate) {
    	if(chocolate >= 0) {
    		int[] arr = new int[4];
    		arr[0] = 15;
    		arr[1] = 15;
    		arr[2] = 15;
    		arr[3] = chocolate;
    		InventoryDB.setup(arr);
    	}
        
    }
    
    /**
     * Add the number of chocolate units in the inventory 
     * to the current amount of chocolate units.
     * @param chocolate
     * @throws InventoryException
     */
    public synchronized void addChocolate(String chocolate) throws InventoryException {
    	int amtChocolate = 0;
    	try {
    		amtChocolate = Integer.parseInt(chocolate);
    	} catch (NumberFormatException e) {
    		throw new InventoryException("Units of chocolate must be a positive integer");
    	}
		if (amtChocolate >= 0) {
			int[] arr = new int[4] ;
			arr[3] = amtChocolate;
			InventoryDB.addInventory(arr);
		} else {
			throw new InventoryException("Units of chocolate must be a positive integer");
		}
    }
    
    /**
     * Returns the current number of coffee units in
     * the inventory.
     * @return int
     */
    public int getCoffee() {
        return InventoryDB.checkInventory()[0];
    }
    
    /**
     * Sets the number of coffee units in the inventory 
     * to the specified amount.
     * @param coffee
     */
    public synchronized void setCoffee(int coffee) {
    	if(coffee >= 0) {
    		int[] arr = new int[4];
    		arr[0] = coffee;
    		arr[1] = 15;
    		arr[2] = 15;
    		arr[3] = 15;
    		InventoryDB.setup(arr);
    	}
    }
    
    /**
     * Add the number of coffee units in the inventory 
     * to the current amount of coffee units.
     * @param coffee
     * @throws InventoryException
     */
    public synchronized void addCoffee(String coffee) throws InventoryException {
    	int amtCoffee = 0;
    	try {
    		amtCoffee = Integer.parseInt(coffee);
    	} catch (NumberFormatException e) {
    		throw new InventoryException("Units of coffee must be a positive integer");
    	}
		if (amtCoffee >= 0) {
			int[] arr = new int[4] ;
			arr[0] = amtCoffee;
			InventoryDB.addInventory(arr);
		} else {
			throw new InventoryException("Units of coffee must be a positive integer");
		}
    }
    
    /**
     * Returns the current number of milk units in
     * the inventory.
     * @return int
     */
    public int getMilk() {
        return InventoryDB.checkInventory()[1];
    }
    
    /**
     * Sets the number of milk units in the inventory
     * to the specified amount.
     * @param milk
     */
    public synchronized void setMilk(int milk) {
    	if(milk >= 0) {
    		int[] arr = new int[4];
    		arr[0] = 15;
    		arr[1] = milk;
    		arr[2] = 15;
    		arr[3] = 15;
    		InventoryDB.setup(arr);
    	}
    }
    
    /**
     * Add the number of milk units in the inventory 
     * to the current amount of milk units.
     * @param milk
     * @throws InventoryException
     */
    public synchronized void addMilk(String milk) throws InventoryException {
    	int amtMilk = 0;
    	try {
    		amtMilk = Integer.parseInt(milk);
    	} catch (NumberFormatException e) {
    		throw new InventoryException("Units of milk must be a positive integer");
    	}
		if (amtMilk >= 0) {
			int[] arr = new int[4] ;
			arr[1] = amtMilk;
			InventoryDB.addInventory(arr);
		} else {
			throw new InventoryException("Units of milk must be a positive integer");
		}
    }
    
    /**
     * Returns the current number of sugar units in 
     * the inventory.
     * @return int
     */
    public int getSugar() {
        return InventoryDB.checkInventory()[2];
    }
    
    /**
     * Sets the number of sugar units in the inventory
     * to the specified amount.
     * @param sugar
     */
    public synchronized void setSugar(int sugar) {
    	if(sugar >= 0) {
    		int[] arr = new int[4];
    		arr[0] = 15;
    		arr[1] = 15;
    		arr[2] = sugar;
    		arr[3] = 15;
    		InventoryDB.setup(arr);
    	}
    }
    
    /**
     * Add the number of sugar units in the inventory 
     * to the current amount of sugar units.
     * @param sugar
     * @throws InventoryException
     */
    public synchronized void addSugar(String sugar) throws InventoryException {
    	int amtSugar = 0;
    	try {
    		amtSugar = Integer.parseInt(sugar);
    	} catch (NumberFormatException e) {
    		throw new InventoryException("Units of sugar must be a positive integer");
    	}
		if (amtSugar >= 0) { 
			int[] arr = new int[4] ;
			arr[2] = amtSugar;
			InventoryDB.addInventory(arr);
		} else {
			throw new InventoryException("Units of sugar must be a positive integer");
		}
    }
    
    /**
     * Returns true if there are enough ingredients to make
     * the beverage.
     * @param r
     * @return boolean
     */
    protected synchronized boolean enoughIngredients(Recipe r) {
        boolean isEnough = true;
		int arr[] = InventoryDB.checkInventory();
        if(arr[0] < r.getAmtCoffee()) {
            isEnough = false;
        }
        if(arr[1] < r.getAmtMilk()) {
            isEnough = false;
        }
        if(arr[2] < r.getAmtSugar()) {
            isEnough = false;
        }
        if(arr[3] < r.getAmtChocolate()) {
            isEnough = false;
        }
        return isEnough;
    }
    
    /**
     * Removes the ingredients used to make the specified 
     * recipe.  Assumes that the user has checked that there
     * are enough ingredients to make 
     * @param r
     */
    public synchronized boolean useIngredients(Recipe r) {
    	if (enoughIngredients(r)) {
	    	InventoryDB.useInventory(r);
	    	return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Returns a string describing the current contents 
     * of the inventory.
     * @return String
     */
    public String toString() {
    	StringBuffer buf = new StringBuffer();
    	buf.append("Coffee: ");
    	buf.append(getCoffee());
    	buf.append("\n");
    	buf.append("Milk: ");
    	buf.append(getMilk());
    	buf.append("\n");
    	buf.append("Sugar: ");
    	buf.append(getSugar());
    	buf.append("\n");
    	buf.append("Chocolate: ");
    	buf.append(getChocolate());
    	buf.append("\n");
    	return buf.toString();
    }
}
