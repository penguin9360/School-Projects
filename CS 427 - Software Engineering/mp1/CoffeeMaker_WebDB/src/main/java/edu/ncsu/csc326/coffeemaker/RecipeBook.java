package edu.ncsu.csc326.coffeemaker;

import edu.ncsu.csc326.coffeemaker.db.RecipeBookDB;
import edu.ncsu.csc326.coffeemaker.exceptions.RecipeException;

public class RecipeBook {
	
	/** Number of recipes in coffee maker */
	private final int NUM_RECIPES = 3; 	
		
	/**
	 * Default constructor for a RecipeBook.
	 */
	public RecipeBook() {
	}
	
	/**
	 * Returns the recipe array.
	 * @param r
	 * @return Recipe[]
	 */
	public synchronized Recipe[] getRecipes() {
		try {
			return RecipeBookDB.getRecipes();
		} catch (RecipeException e) {
			e.printStackTrace();
			return new Recipe[NUM_RECIPES];
		}
	}
	
	public synchronized boolean addRecipe(Recipe r) {
		//Assume recipe doesn't exist in the array until 
		//find out otherwise
		boolean exists = false;
		//Check that recipe doesn't already exist in array
		if (RecipeBookDB.recipeExists(r.getName())) {
			exists = true;
		}
		//Assume recipe cannot be added if there are the max number
		//of recipes
		boolean added = false;
		//Check for first empty spot in array
		if (!exists) {
			if (RecipeBookDB.recipeCount() < NUM_RECIPES) {
				//There's room
				RecipeBookDB.addRecipe(r);
				added = true;
			}
		}
		return added;
	}

	/**
	 * Returns the name of the recipe deleted at the position specified
	 * and null if the recipe does not exist.
	 * @param recipeToDelete
	 * @return String
	 */
	public synchronized String deleteRecipe(String name) {
		return RecipeBookDB.deleteRecipe(name);
	}
	
	/**
	 * Returns the name of the recipe edited at the position specified
	 * and null if the recipe does not exist.
	 * @param recipeToEdit
	 * @param newRecipe
	 * @return String
	 */
	public synchronized String editRecipe(String name, Recipe r) {
		return RecipeBookDB.editRecipe(name, r);
	}
	
	/**
	 * Return true if the recipe exists
	 * @param name
	 * @return boolean
	 */
	public synchronized boolean recipeExists(String name) {
		return RecipeBookDB.recipeExists(name);
	}

}
